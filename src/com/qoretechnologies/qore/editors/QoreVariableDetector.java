package com.qoretechnologies.qore.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class QoreVariableDetector implements IWordDetector {

	public boolean isWordStart(char c) {
		return (c == '$');
	}

	public boolean isWordPart(char c) {
		return Character.isLetterOrDigit(c) || (c == '_');
	}

}
