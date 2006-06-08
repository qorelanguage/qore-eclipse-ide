package com.qoretechnologies.qore.editors;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;

/**
 * An implementation of <code>IRule</code> capable of detecting qore numbers
 * and dates.
 *
 * @see IWordDetector
 */
public class QoreNumberTokenRule implements IRule {
	/** the token to be returned when a number is found */
	private IToken tNumber;
    /** the token to be returned when a method or function call is found */
	private IToken tDate;

	/**
	 * Creates a rule that will return the token associated with the detected
	 * input.  If the input is a number or date, the appropriate token will be returned.
	 * hex, octal, base 10, and floating-point numbers are recognized, as well as
	 * dates with and without time components and times without dates
	 *
	 * @param nt the default token to be returned when a number is found, may not be <code>null</code>
	 * @param dt the token to be returned when a date is found, may not be <code>null</code>
	 */
	public QoreNumberTokenRule(IToken nt, IToken dt)
	{
		Assert.isNotNull(nt);
		Assert.isNotNull(dt);
		tNumber = nt;
		tDate = dt;
	}	

	private IToken unwind(ICharacterScanner scanner, int n, IToken token)
	{
		while (n-- > 0)
			scanner.unread();
		return token;
	}
	
	/**
	 * Uses the scanner to recognize complete dates.  If non-conformant input is found, 
	 * the scanner is rewound to the last valid input and the appropriate token is returned.
	 * 
	 * qore dates have the format: YYYY-MM-DD 
	 * or optionally with a time component: YYYY-MM-DD-HH:mm:SS
	 * by the time this method is called, YYYY- has already been read
	 * 
	 * @param scanner the character scanner for the input to scan
	 */
	private IToken processDate(ICharacterScanner scanner)
	{
		// read nn-nn
		char c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 2, tNumber);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 3, tNumber);
			
		c = (char)scanner.read();
		if (c != '-')
			return unwind(scanner, 4, tNumber);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 5, tNumber);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 6, tNumber);
			
		// now we have a complete date, check for time component -nn:nn:nn
		c = (char)scanner.read();
		if (c != '-')
			return unwind(scanner, 1, tDate);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 2, tDate);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 3, tDate);
		
		c = (char)scanner.read();
		if (c != ':')
			return unwind(scanner, 4, tDate);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 5, tDate);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 6, tDate);

		c = (char)scanner.read();
		if (c != ':')
			return unwind(scanner, 7, tDate);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 8, tDate);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 9, tDate);

		return tDate;
	}
	
	private boolean isHexDigit(char c)
	{
		if (Character.isDigit(c))
			return true;
		if ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
			return true;

		return false;
	}
	
	private IToken processHexNumber(ICharacterScanner scanner) {
		char c = (char)scanner.read();
		if (!isHexDigit(c))
			return unwind(scanner, 2, tNumber);

		do {
			c = (char)scanner.read();
		} while (isHexDigit(c));
		scanner.unread();
		
		return tNumber;
	}
	
	private IToken processTime(ICharacterScanner scanner) {
		char c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 2, tNumber);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 3, tNumber);

		c = (char)scanner.read();
		if (c != ':')
			return unwind(scanner, 4, tNumber);

		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 5, tNumber);
			
		c = (char)scanner.read();
		if (!Character.isDigit(c))
			return unwind(scanner, 6, tNumber);

		return tDate;
	}
	
	/**
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		
		if (!Character.isDigit((char)c))
		{
			scanner.unread();
			return Token.UNDEFINED;
		}
		
		int len = 1;
		boolean dot = false;
		do {
			c = scanner.read();
			len++;
			if (c == '.')
			{
				if (!dot)
				{
					dot = true;
					c = '0';
				}
				else
					break;
			}
			if (c == ':' && len == 3)
				return processTime(scanner);
			else if (c == '-' && len == 5)
				return processDate(scanner);
			else if (c == 'x' && len == 2)
				return processHexNumber(scanner);
		} while (Character.isDigit((char)c));
		// "unread" last character
		scanner.unread();
		
		return tNumber;
	}
}
