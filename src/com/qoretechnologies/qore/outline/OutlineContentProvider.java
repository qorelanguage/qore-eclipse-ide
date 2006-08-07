package com.qoretechnologies.qore.outline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.qoretechnologies.qore.completion.QoreCompletionElement;
import com.qoretechnologies.qore.tools.ParserHelper;
import com.qoretechnologies.qore.tools.ResourceLoader;

public class OutlineContentProvider implements ITreeContentProvider
{

	private IDocumentProvider documentProvider;

	protected final static String TAG_POSITIONS = "__tag_positions";

	protected IPositionUpdater positionUpdater = new DefaultPositionUpdater(TAG_POSITIONS);

	public OutlineContentProvider(TreeViewer treeViewer, IDocumentProvider provider)
	{
		super();
		this.documentProvider = provider;
	}

	/**
	 * The method gets called only if an item having children is opened. An
	 * example of such item can be namespace or class. See also hasChildren()
	 * method of this class.
	 */
	@SuppressWarnings("unchecked")
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof TreeItem)
		{
			IDocument doc = ((TreeItem) parentElement).getParentDoc();
			FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(doc);
			ArrayList results = new ArrayList();
			
			if (((TreeItem) parentElement).getType() == TreeItem.ITEM_TYPE.NAMESPACE)
			{
				// find the length of the namespace in document
				int endOfBlockOffset = 0;
				try
				{
					endOfBlockOffset = ParserHelper.getNextBracketBlock(doc, finder, ((TreeItem) parentElement).getOffset());
				}
				catch (BadLocationException e)
				{
					endOfBlockOffset = doc.getLength();
				}

				List namespaces = getChildElements(doc, "\\s*namespace\\s*(\\w*)", ((TreeItem) parentElement).getOffset(), endOfBlockOffset, TreeItem.ITEM_TYPE.NAMESPACE);
				List<TreeItem> classes = getTopElements(doc, "\\s*class\\s*.*" + ((TreeItem) parentElement).getName() + "::(\\w*)[^:\\w]", 0, doc.getLength(), TreeItem.ITEM_TYPE.CLASS,true);
				List<TreeItem> nestedClasses = getChildElements(doc, "\\s*class\\s*(\\w*)", ((TreeItem) parentElement).getOffset(), endOfBlockOffset, TreeItem.ITEM_TYPE.CLASS);
				// the context in which is to be searched is determined by
				// parentElement.getOffset()
				List consts = getChildElements(doc, "const\\s*(\\w*).*=.*", ((TreeItem) parentElement).getOffset(), endOfBlockOffset, TreeItem.ITEM_TYPE.CONSTANT);

				results.addAll(namespaces);
				for (TreeItem t : classes)
				{
					if(!results.contains(t))
						results.add(t);
				}
				for (TreeItem t : nestedClasses)
				{
					if(!results.contains(t))
						results.add(t);
				}
				results.addAll(consts);
				return results.toArray();
			}
			if (((TreeItem) parentElement).getType() == TreeItem.ITEM_TYPE.CLASS)
			{
				// find the length of the class in document
				int endOfBlockOffset = 0;
				try
				{
					endOfBlockOffset = ParserHelper.getNextBracketBlock(doc, finder, ((TreeItem) parentElement).getOffset());
				}
				catch (BadLocationException e)
				{
					endOfBlockOffset = doc.getLength();
				}
				List consts = getChildElements(doc, "const\\s*(\\w*).*=.*", ((TreeItem) parentElement).getOffset(), endOfBlockOffset, TreeItem.ITEM_TYPE.CONSTANT);
				results.addAll(consts);
				return results.toArray();
			}
		}
		return null;
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		if (element instanceof TreeItem)
		{
			if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.VARIABLE || ((TreeItem) element).getType() == TreeItem.ITEM_TYPE.FUNCTION
					|| ((TreeItem) element).getType() == TreeItem.ITEM_TYPE.CONSTANT)
				return false;
			else
				// namespace or class
				return true;
		}
		return true;
	}

	/**
	 * 
	 * @param inputElement
	 * @param regex
	 *            to search for
	 * @param fromOffset
	 *            start from here
	 * @param toOffset
	 *            search only to this position in document
	 * @param type
	 *            type of the TreeItem which is to be returned
	 * @param ignoreEnclosingElement
	 *            true if the elements should be included regardless of their
	 *            belongness to a upper level element; false = element enclosed
	 *            in any parent element won't be included
	 * @return
	 */
	private List getChildElements(IDocument doc, String regex, int fromOffset, int toOffset, TreeItem.ITEM_TYPE type)
	{
		ArrayList<TreeItem> results = new ArrayList<TreeItem>();
		FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(doc);
		int lastOffset = fromOffset;
		Pattern p = Pattern.compile(regex);

		IRegion var = null;
		do
		{
			try
			{
				var = finder.find(lastOffset, regex, true, false, false, true);
				if (var != null && var.getOffset() + var.getLength() < toOffset)
				{
					lastOffset = var.getOffset() + var.getLength();

					int openingBrackets = ParserHelper.getBracketCountBackwards(finder, lastOffset, fromOffset, "\\{");
					int closingBrackets = ParserHelper.getBracketCountBackwards(finder, lastOffset, fromOffset, "\\}");

					String text = doc.get(var.getOffset(), var.getLength());
					// use the group from regex as label for the tree item
					Matcher matcher = p.matcher(text);
					matcher.matches(); // will match always, we just need
					// capture the group
					if (matcher.groupCount() > 0)
						text = matcher.group(1).trim();
					else
						text = text.substring(text.indexOf(" ")).trim();

					// show first children level
					if (openingBrackets-closingBrackets==1)
					{
						TreeItem temp = new TreeItem(doc, type, text, text);
						// set offset at the start of the word (ignore the
						// whitespace before it)
						temp.setOffset(var.getOffset() + (var.getLength() - text.length()));
						temp.setLength(var.getLength());
						results.add(temp);
					}
				}
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		while (var != null && var.getOffset() + var.getLength() < toOffset);
		return results;
	}

	private List getTopElements(IDocument doc, String regex, int fromOffset, int toOffset, TreeItem.ITEM_TYPE type)
	{
		return getTopElements(doc,regex,fromOffset, toOffset, type, false);
	}

	 /**
	 * 
	 * @param inputElement
	 * @param regex
	 *            to search for
	 * @param fromOffset
	 *            start from here
	 * @param toOffset
	 *            search only to this position in document
	 * @param type
	 *            type of the TreeItem which is to be returned
	 * @param ignoreEnclosingElement
	 *            true if the elements should be included regardless of their
	 *            belongness to a upper level element; false = element enclosed
	 *            in any parent element won't be included
	 * @return
	 */
	private List getTopElements(IDocument doc, String regex, int fromOffset, int toOffset, TreeItem.ITEM_TYPE type, boolean ignoreBrackets)
	{
		ArrayList<TreeItem> results = new ArrayList<TreeItem>();
		FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(doc);
		int lastOffset = fromOffset;
		Pattern p = Pattern.compile(regex);

		IRegion var = null;
		do
		{
			try
			{
				boolean isInsideBrackets = false;
				var = finder.find(lastOffset, regex, true, false, false, true);
				if (var != null && var.getOffset() + var.getLength() < toOffset)
				{
					lastOffset = var.getOffset() + var.getLength();

					// now try to find find out if we in the top level (not
					// inside of any namespace or class)
					int openingBrackets = ParserHelper.getBracketCountBackwards(finder, lastOffset, 0, "\\{");
					int closingBrackets = ParserHelper.getBracketCountBackwards(finder, lastOffset, 0, "\\}");
					if (openingBrackets != closingBrackets)
						isInsideBrackets = true;

					String text = doc.get(var.getOffset(), var.getLength());
					// use the group from regex as label for the tree item
					Matcher matcher = p.matcher(text);
					matcher.matches(); // will match always, we just need
					// capture the group
					if (matcher.groupCount() > 0)
						text = matcher.group(1).trim();
					else
						text = text.substring(text.indexOf(" ")).trim();

					if (!isInsideBrackets || ignoreBrackets)
					{
						TreeItem temp = new TreeItem(doc, type, text, text);
						// set offset at the start of the word (ignore the
						// whitespace before it)
						temp.setOffset(var.getOffset() + (var.getLength() - text.length()));
						temp.setLength(var.getLength());
						results.add(temp);
					}
				}
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		while (var != null && var.getOffset() + var.getLength() < toOffset);
		return results;
	}

	/**
	 * Called to get top-level elements in the tree.
	 */
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement)
	{
		ArrayList results = new ArrayList();
		int docLength = documentProvider.getDocument(inputElement).getLength();
		IDocument doc = documentProvider.getDocument(inputElement);
		List namespaces = getTopElements(doc, "^\\s*namespace\\s*\\w*", 0, docLength, TreeItem.ITEM_TYPE.NAMESPACE);
		List classes = getTopElements(doc, "^\\s*class\\s*\\w+\\s", 0, docLength, TreeItem.ITEM_TYPE.CLASS);
		List globals = getTopElements(doc, "our\\s*\\$\\w*", 0, docLength, TreeItem.ITEM_TYPE.VARIABLE);
		// constants can be defined in namespaces
		List consts = getTopElements(doc, "const\\s*(\\w*).*=.*", 0, docLength, TreeItem.ITEM_TYPE.CONSTANT);
		// functions can be defined in classes as well (methods)
		List subs = getTopElements(doc, "^\\s*sub\\s.*\\)", 0, docLength, TreeItem.ITEM_TYPE.FUNCTION);

		results.addAll(namespaces);
		results.addAll(classes);
		results.addAll(globals);
		results.addAll(consts);
		results.addAll(subs);

		return results.toArray();
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (oldInput != null)
		{
			IDocument document = documentProvider.getDocument(oldInput);
			if (document != null)
			{
				try
				{
					document.removePositionCategory(TAG_POSITIONS);
				}
				catch (BadPositionCategoryException x)
				{
				}
				document.removePositionUpdater(positionUpdater);
			}
		}
		// input = (IEditorInput) newInput;
		if (newInput != null)
		{
			IDocument document = documentProvider.getDocument(newInput);
			if (document != null)
			{
				document.addPositionCategory(TAG_POSITIONS);
				document.addPositionUpdater(positionUpdater);
			}
		}
	}

}
