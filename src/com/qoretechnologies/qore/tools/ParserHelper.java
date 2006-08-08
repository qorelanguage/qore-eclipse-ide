package com.qoretechnologies.qore.tools;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class ParserHelper
{
	/**
	 * Searches from currentOffset backwards until startOffset, counting the
	 * regexBracket occurences found.
	 * 
	 * @param finder
	 * @param currentOffset
	 *            offset at which the completion was invoked
	 * @param startOffset
	 *            offset in the document until which it should be searched
	 *            (usually < than currentOffset)
	 * @param regexBracket
	 *            regext to search for
	 * @return count of brackets found
	 * @throws BadLocationException
	 */
	public static int getBracketCountBackwards(FindReplaceDocumentAdapter finder, int currentOffset, int startOffset, String regexBracket) throws BadLocationException
	{
		IRegion bracketRegion = null;
		int bracketCount = 0;
		int lastOffsetForBrackets = currentOffset;
		do
		{
			bracketRegion = finder.find(lastOffsetForBrackets, regexBracket, false, false, false, true);
			if (bracketRegion != null)
			{
				lastOffsetForBrackets = bracketRegion.getOffset() - 1; // bracket.getLength('{')
				if (lastOffsetForBrackets > startOffset)
					bracketCount++;
			}
		}
		while (bracketRegion != null && lastOffsetForBrackets > startOffset);
		return bracketCount;
	}

	/**
	 * Return the offset of last closing bracket of the block starting from
	 * fromOffset. A semicolon is also considered as end of a block, e.g. when defining a class like 
	 * class OMQ::SomeClass; the methods follow not directly in a {} block, but are defined elsewhere. 
	 * 
	 * @param finder
	 * @param fromOffset
	 *            starts from this offset (e.g. "namespace ABC ...")
	 * @return document offset of the last closing bracket
	 * @throws BadLocationException
	 */
	public static int getNextBracketBlock(IDocument doc, FindReplaceDocumentAdapter finder, int fromOffset) throws BadLocationException
	{
		int endOffset = fromOffset;
		IRegion bracketReg = null;
        int opening = 0, closing = 0;
        int nextSemicolOffset = -1;
        boolean semicolFound = false;
        
		bracketReg = finder.find(fromOffset, ";", true, false, false, false);
		if(bracketReg!=null)
		{
			nextSemicolOffset = bracketReg.getOffset();
			semicolFound = true;
		}
		
		do
		{
			bracketReg = finder.find(endOffset, "[{}]", true, false, false, true);
			if (bracketReg != null)
			{
				// if the next semicolon comes before first {, consider it as and of block
				if(semicolFound==true && bracketReg.getOffset() > nextSemicolOffset)
				{
					endOffset = nextSemicolOffset;
					break;
				}
				else // ignore the other semicolons inside {} block
					semicolFound = false;  
				
				switch (doc.getChar(bracketReg.getOffset()))
				{
					case '{':  opening++; break;
					case '}':  closing++;
					default : 
				}
				endOffset = bracketReg.getOffset()+1;
			}
		}
		while (bracketReg != null && opening!=closing);
		return endOffset;
	}

}
