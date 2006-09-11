package com.qoretechnologies.qore.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.qoretechnologies.qore.tools.ParserHelper;
import com.qoretechnologies.qore.tools.ResourceLoader;

/**
 * The class searches for suitable proposals for word completion.
 * 
 * @author potancpa
 */
public class QoreCompletionSuggester
{

	private static QoreCompletionSuggester theInstance = null;

	private static Collection<QoreCompletionElement> qoreFunctions = new ArrayList<QoreCompletionElement>();

	private static Collection<QoreCompletionElement> qoreClasses = new ArrayList<QoreCompletionElement>();

	private static Collection<QoreCompletionElement> qoreVariables = null;

	private static Hashtable<String, ArrayList> classMethods = new Hashtable<String, ArrayList>();

	@SuppressWarnings("unchecked")
	public QoreCompletionSuggester()
	{
		ResourceLoader loader = new ResourceLoader();
		qoreFunctions = loader.loadElements("/com/qoretechnologies/qore/editors/resources/qore-functions.xml");
		qoreClasses = loader.loadElements("/com/qoretechnologies/qore/editors/resources/class-lib.xml");

		// create the lookup table for class methods
		for (QoreCompletionElement e : qoreClasses.toArray(new QoreCompletionElement[0]))
		{
			String className = e.getName().substring(e.getName().indexOf("::") + 2, e.getName().lastIndexOf("::"));
			// get only the classname out of the entire declaration which
			// includes also namespaces
			do
			{
				if (className.contains("::"))
				{
					if (className.indexOf("::") != className.lastIndexOf("::"))
						className = className.substring(className.indexOf("::") + 2, className.lastIndexOf("::"));
					else
						className = className.substring(className.indexOf("::") + 2, className.length());
				}
			}
			while (className.contains("::"));

			// use just method names, i.e. from Qore::File::close() to close()
			e.setName(e.getName().substring(e.getName().lastIndexOf("::") + 2));

			if (!e.getName().startsWith("constructor") && !e.getName().startsWith("destructor"))
			{
				if (!classMethods.containsKey(className)) // there are no
				// methods
				// for this class yet
				{
					ArrayList<QoreCompletionElement> a = new ArrayList<QoreCompletionElement>();
					a.add(e);
					classMethods.put(className, a);
				}
				else
				{
					ArrayList temp = classMethods.get(className);
					temp.add(e);
				}
			}
			else
				if (e.getName().startsWith("constructor")) // save constructors
				// as special
				// methods
				{
					e.setName(className + "()");
					qoreFunctions.add(e);
				}
		}
	}

	public static QoreCompletionSuggester getInstance()
	{
		if (theInstance == null)
			theInstance = new QoreCompletionSuggester();
		return theInstance;
	}

	/**
	 * The method returns all Qore variables from current document.
	 * 
	 * @param document
	 *            current document
	 * @param prefix
	 *            the word/part of the word after which the completion has been
	 *            initiated (e.g. '$psf' )
	 * @return list of QoreElement instances
	 * @throws BadLocationException
	 */
	@SuppressWarnings("unchecked")
	private Collection<QoreCompletionElement> loadVariables(IDocument document, int offset, String prefix) throws BadLocationException
	{
		List<QoreCompletionElement> temp = new ArrayList<QoreCompletionElement>();
		// these variables are accessible always: $ARGV, $QORE_ARGV and $ENV
		temp.add(new QoreCompletionElement("$ARGV", "global variable $ARGV", ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE)));
		temp.add(new QoreCompletionElement("$QORE_ARGV", "global variable $QORE_ARGV", ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE)));
		temp.add(new QoreCompletionElement("$ENV", "global variable $ENV", ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE)));

		Collection<QoreCompletionElement> classVars = loadVariablesByRegex(document, offset, prefix, "(\\$\\w*).*=.*new\\s*(\\w*)", true);
		for (Iterator<QoreCompletionElement> i = classVars.iterator(); i.hasNext();)
		{
			QoreCompletionElement current = i.next();
			if (!temp.contains(current))
				temp.add(current);
		}

		Collection<QoreCompletionElement> localVars = loadVariablesByRegex(document, offset, prefix, "\\$\\w*", true);
		for (Iterator<QoreCompletionElement> i = localVars.iterator(); i.hasNext();)
		{
			QoreCompletionElement current = i.next();
			if (!temp.contains(current))
				temp.add(current);
		}

		Collection<QoreCompletionElement> globalVars = loadVariablesByRegex(document, offset, prefix, "our\\s*(\\$\\w*)", false);
		for (Iterator<QoreCompletionElement> i = globalVars.iterator(); i.hasNext();)
		{
			QoreCompletionElement current = i.next();
			if (!temp.contains(current))
				temp.add(current);
		}

		Collections.sort(temp);
		return temp;
	}

	private Collection<QoreCompletionElement> loadVariablesByRegex(IDocument document, int offset, String prefix, String regex, boolean searchContextOffset)
			throws BadLocationException
	{
		List<QoreCompletionElement> temp = new ArrayList<QoreCompletionElement>();
		FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(document);

		IRegion var = null;
		int lastOffset = 0;
		boolean isInsideSub = false;

		// search from last sub/method definition
		if (searchContextOffset)
		{
			// find previous function definition
			var = finder.find(offset, "sub\\s.*\\)", false, false, false, true);
			if (var != null)
				lastOffset = var.getOffset();

			// now try to find find out if we are inside a sub or not
			//int openingBrackets = ParserHelper.getBracketCountBackwards(finder, offset, lastOffset, "\\{");
			//int closingBrackets = ParserHelper.getBracketCountBackwards(finder, offset, lastOffset, "\\}");
			int openingBrackets = ParserHelper.getBracketCountBackwards(document, offset, lastOffset, '{');
			int closingBrackets = ParserHelper.getBracketCountBackwards(document, offset, lastOffset, '}');
			if (openingBrackets > closingBrackets) // the we are inside of a sub
			{
				isInsideSub = true;
				temp.add(new QoreCompletionElement("$argv", "local variable $argv", ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE)));
			}
		}

		do
		{
			String varName = "", className = "";
			var = finder.find(lastOffset, regex, true, false, false, true);
			if (var != null)
			{
				lastOffset = var.getOffset() + var.getLength();
				String text = document.get(var.getOffset(), var.getLength());
				Pattern p = Pattern.compile(regex);
				Matcher matcher = p.matcher(text);
				matcher.matches(); // will match always because if uses the
				// same regex as finder.find()
				if (matcher.groupCount() == 0)
					varName = text;
				if (matcher.groupCount() > 0)
					varName = matcher.group(1);
				if (matcher.groupCount() > 1)
					className = matcher.group(2);
			}
			// do not include ST $Id or trailing $ or the just written new
			// variable or any variable found after current cursor position
			if (!varName.equals("$") && !varName.toLowerCase().equals("$id") && !varName.equals(prefix) && lastOffset < offset)
			{
				if ((searchContextOffset && isInsideSub) || !searchContextOffset)
				{
					QoreCompletionElement qe = new QoreCompletionElement();
					qe.setName(varName);
					if (className.equals(""))
						qe.setDescription("variable " + varName);
					else
					{
						qe.setDescription("instance of " + className + " class");
						qe.setClassName(className);
					}
					qe.setImage(ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE));
					if (!temp.contains(qe))
					{
						temp.add(qe);
					}
				}
			}
		}
		while (var != null && lastOffset < offset);
		return temp;
	}

	/**
	 * Returns proposals for completion based on passed parameters.
	 * 
	 * @param entireLine
	 *            line at which the event of opening the completion list occured
	 * @param lastWordStartOffset
	 *            offset of the start of last written word (in front of cursor)
	 * @param offset
	 *            current offset (where the cursor was when Ctrl+Space was
	 *            pressed)
	 * @param prefix
	 *            the part of the word written before pressing Ctrl+Space
	 * @param after
	 *            if any, the part from the cursor position up to the end of the
	 *            line
	 * @return collection of CompletionProposal instances There can be several
	 *         scenarios when the completion can be initiated in the editor: 1.
	 *         on the beginning of an empty/new line - all suggestions will be
	 *         displayed 2. after several characters, at the end of the line (no
	 *         characters the cursor) 3. in the middle of the line, there are
	 *         statements before and after the cursor's location
	 * @throws BadLocationException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ICompletionProposal> getProposals(IDocument document, int offset) throws BadLocationException
	{
		int lastWordStartOffset = getLastWordStartOffset(document, offset);
		String entireLine = document.get(document.getLineInformationOfOffset(offset).getOffset(), document.getLineInformationOfOffset(offset).getLength());
		String prefix;
		if (offset > 0 && lastWordStartOffset != -1)
		{
			prefix = document.get(lastWordStartOffset, offset - lastWordStartOffset);
		}
		else
		{
			prefix = document.get(0, offset);
			lastWordStartOffset = 0;
		}
		String textAfterCursor = getPartAfterCursor(document, offset);

		// load variables suggestions from current document
		ArrayList searchTerms = new ArrayList(qoreFunctions);
		qoreVariables = loadVariables(document, offset, prefix);
		searchTerms.addAll(qoreVariables);

		if (prefix.startsWith("$") && prefix.indexOf(".") != -1) // search
		// for
		// class methods
		{
			Collection<ICompletionProposal> results = new ArrayList<ICompletionProposal>();
			Iterator<QoreCompletionElement> i = searchTerms.iterator();
			int dotOffset = prefix.indexOf(".");
			lastWordStartOffset += dotOffset;// last offset is in this case
			// the start of the method name
			String methodStart = "";
			if (dotOffset < prefix.length())
				methodStart = prefix.substring(dotOffset + 1, prefix.length());
			prefix = prefix.substring(0, dotOffset);

			while (i.hasNext())
			{
				QoreCompletionElement qe = i.next();
				if (qe.getClassName() != null && qe.getName().startsWith(prefix))
				{
					ArrayList<QoreCompletionElement> methods = classMethods.get(qe.getClassName());
					if (methods != null)
						for (QoreCompletionElement method : methods)
						{
							if (method.getName().startsWith(methodStart))
							{
								if (textAfterCursor.startsWith("(")) // e.g.
								// substr|(
								{
									String noParentheses = method.getName().substring(0, method.getName().length() - 2);
									results.add(new CompletionProposal(noParentheses, offset - methodStart.length(), methodStart.length(), noParentheses.length(), method
											.getImage(), method.getName(), null, method.getDescription()));
								}
								else
									results.add(new CompletionProposal(method.getName(), offset - methodStart.length(), methodStart.length(), method.getName().length(), method
											.getImage(), method.getName(), null, method.getDescription()));
							}
						}
				}
			}
			return results;
		}
		else
		{ // all other proposals except class methods
			Collection<ICompletionProposal> results = new ArrayList<ICompletionProposal>();
			Iterator<QoreCompletionElement> i = searchTerms.iterator();

			while (i.hasNext())
			{
				QoreCompletionElement qe = i.next();

				if (qe.getName().startsWith(prefix)) // we found a candidate
				{
					int cursorAfter; // if a function has params, place the
					// cursor inside ()
					if (qe.getName().endsWith("()") && !qe.getParamString().equals(""))
						cursorAfter = qe.getName().length() - 1;
					else
						cursorAfter = qe.getName().length();

					if (entireLine.trim().equals("")) // 1_empty_line
					{
						results.add(new CompletionProposal(qe.getName(), offset, 0, cursorAfter, qe.getImage(), qe.getName(), null, qe.getDescription()));
					}
					else
						if (!prefix.trim().equals("") && textAfterCursor.trim().equals("")) // 2_line_end
						{
							results.add(new CompletionProposal(qe.getName(), lastWordStartOffset, prefix.length(), cursorAfter, qe.getImage(), qe.getName(), null, qe
									.getDescription()));
						}
						else
						// somewhere in the middle
						{
							if (textAfterCursor.startsWith("(")) // e.g.
							// substr|(
							{
								String noParentheses = qe.getName().substring(0, qe.getName().length() - 2);
								results.add(new CompletionProposal(noParentheses, lastWordStartOffset, prefix.length(), noParentheses.length(), qe.getImage(), qe.getName(), null,
										qe.getDescription()));
							}
							else
							{
								if (textAfterCursor.indexOf("(") != -1) // e.g.s|ubstr(
								{
									String noParentheses = qe.getName().substring(0, qe.getName().length() - 2);
									int replaceLength = (offset - lastWordStartOffset) + textAfterCursor.indexOf("(");
									results.add(new CompletionProposal(noParentheses, lastWordStartOffset, replaceLength, noParentheses.length(), qe.getImage(), qe.getName(),
											null, qe.getDescription()));
								}
								else
								{
									results.add(new CompletionProposal(qe.getName(), lastWordStartOffset, prefix.length(), cursorAfter, qe.getImage(), qe.getName(), null, qe
											.getDescription()));
								}
							}
						}
				}
			}
			return results;
		}
	}

	private int getLastWordStartOffset(IDocument doc, int offset)
	{
		try
		{
			for (int n = offset - 1; n >= 0; n--)
			{
				char c = doc.getChar(n);
				if (Character.isISOControl(c) || Character.isSpaceChar(c) || c == '(' || c == ',')
					return n + 1;
			}
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	private String getPartAfterCursor(IDocument doc, int offset)
	{
		try
		{
			int start = offset;
			while (start >= 0 && doc.getChar(start) != '\n')
				start++;
			String after = doc.get(offset, start - offset);
			return after;
		}
		catch (BadLocationException e)
		{
			// nothing, there may be no characters after cursor (e.g. when at
			// EOL)
		}
		return "";
	}

}
