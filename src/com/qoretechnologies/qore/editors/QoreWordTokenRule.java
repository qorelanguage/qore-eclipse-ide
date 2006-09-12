package com.qoretechnologies.qore.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;

/**
 * An implementation of <code>IRule</code> capable of detecting qore words,
 * keywords, method/function calls/declarations, and namespace specifiers.
 *
 * @see IWordDetector
 */
public class QoreWordTokenRule implements IRule {
    /** the token to be returned when a word is found */
	private IToken tWord;
    /** the token to be returned when a method or function call is found */
	private IToken tMethod;
    /** the token to be returned when a keyword is found */
	private IToken tKeyword;
    /** the token to be returned when a namespace specifier is found */
	private IToken tNamespace;
    /** the hash map of keywords */
	protected Map<String,Object> keywords = new HashMap<String,Object>();
    /** the buffer for the word being scanned */
	private StringBuffer buf = new StringBuffer();

	/**
	 * Creates a rule that will return the token associated with the detected
	 * word.  If the word is a keyword or a method or function call, then
	 * the appropriate token will be returned.d.
	 *
	 * @param wt the default token to be returned when a word is found, may not be <code>null</code>
	 * @param mt the token to be returned when a method or function call is found, may not be <code>null</code>
	 * @param kwt the token to be returned when a keyword is found, may not be <code>null</code>
	 * @param kwt the token to be returned when a namespace is found, may not be <code>null</code>
	 *
	 * @see #addKeyword(String, IToken)
	 */
	public QoreWordTokenRule(IToken wt, IToken mt, IToken kwt, IToken nt)
	{
		Assert.isNotNull(wt);
		Assert.isNotNull(mt);
		Assert.isNotNull(kwt);
		Assert.isNotNull(nt);
		tWord = wt;
		tMethod = mt;
		tKeyword = kwt;
		tNamespace = nt;
	}

	/**
	 * Adds a keyword to the keywords HashMap.
	 *
	 * @param word the keyword this rule will search for, may not be <code>null</code>
	 */
	public void addKeyword(String word) {
		Assert.isNotNull(word);

		keywords.put(word, null);
	}

	/**
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		
		if (!Character.isLetterOrDigit((char)c))
		{
			scanner.unread();
			return Token.UNDEFINED;
		}

		boolean ns = false;
		buf.setLength(0);
		do {
			buf.append((char)c);
			c = scanner.read();
			if (c == '(')
			{
				scanner.unread();
				return tMethod;
			}
			if (c == ':')
			{
				if (ns)
					return tNamespace;
				ns = true;
			}
			else if (ns)
			{
				scanner.unread();
				buf.setLength(buf.length() - 1);
				break;
			}
		} while (c != ICharacterScanner.EOF && (Character.isLetterOrDigit((char)c) || c == '_' || c == ':'));
		scanner.unread();

		// see if it's a keyword
		if (keywords.containsKey(buf.toString()))
			return tKeyword;
		
		return tWord;
	}
}
