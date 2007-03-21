package com.qoretechnologies.qore.editors;

import org.eclipse.jface.text.rules.IWordDetector;

public class QoreParseDirectiveDetector implements IWordDetector {

	public boolean isWordStart(char c) {
		return Character.isLetterOrDigit(c) || (c == '_');
	}

	public boolean isWordPart(char c) {
		return Character.isLetterOrDigit(c) || (c == '-');
	}
}
