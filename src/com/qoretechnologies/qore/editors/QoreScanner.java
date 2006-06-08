package com.qoretechnologies.qore.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.SWT;
//import org.eclipse.jface.text.*;

public class QoreScanner extends RuleBasedScanner {

	private static String[] qKeywords = { 
		"background", "break", "case", "catch", 
		"class", "const", "continue", "context", "delete", "do", 
		"elements", "else", "exists", "for", "foreach", "if", "in", 
		"inherits", "keys", "my", "namespace", "new", "NOTHING", 
		"NULL", "our", "rethrow", "sub", "thread_exit", "where", 
		"private", "return", "sortBy", "sortDescendingBy", "switch", 
		"subcontext", "summarize", "synchronized", "throw", "try", "while",
		"splice", "push", "pop", "shift", "unshift"
	}; 

	public QoreScanner(ColorManager manager) {
		// constants
		IToken string     = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.STRING)));
		IToken number     = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.NUMBER)));
		IToken date       = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.DATE)));

		IToken keyword    = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.KEYWORD), null, SWT.BOLD));
		IToken variable   = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.VARIABLE)));
		IToken in_obj_ref = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.IN_OBJECT_REF)));
		IToken method     = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.METHOD)));
		IToken namespace  = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.NAMESPACE), null, SWT.ITALIC));
		IToken other      = new Token(new TextAttribute(manager.getColor(IQoreColorConstants.DEFAULT)));

		ArrayList rules = new ArrayList();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", string, '\\'));
		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", string, '\\'));

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new QoreWhitespaceDetector()));

		rules.add(new QoreNumberTokenRule(number, date));

		QoreWordDetector qwDetector = new QoreWordDetector();

		QoreWordTokenRule wordTokenRule = new QoreWordTokenRule(other, method, keyword, namespace);
		for (int i = 0; i < qKeywords.length; i++)
			wordTokenRule.addKeyword(qKeywords[i]);

		rules.add(wordTokenRule	);

		WordPatternRule wpRule = new WordPatternRule(qwDetector, "$.", null, in_obj_ref);
		rules.add(wpRule);

		wpRule = new WordPatternRule(qwDetector, "$", null, variable);
		rules.add(wpRule);
		
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
