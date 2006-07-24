package com.qoretechnologies.qore.tools;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;

public class ParserHelper
{
	public static int getBracketCount(FindReplaceDocumentAdapter finder, int currentOffset, int startOffset, String regexBracket) throws BadLocationException
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

}
