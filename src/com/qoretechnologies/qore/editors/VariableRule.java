package com.qoretechnologies.qore.editors;

import org.eclipse.jface.text.rules.*;

public class VariableRule extends SingleLineRule {

	public VariableRule(IToken token) {
		super("$", " ", token);
	}
	protected boolean sequenceDetected(
		ICharacterScanner scanner,
		char[] sequence,
		boolean eofAllowed) {
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		return super.endSequenceDetected(scanner);
		/*
		int c;
		while (true)
		{
			c = scanner.read();
			if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' && c > '9'))
				return true;
		}
	*/
	}
}
