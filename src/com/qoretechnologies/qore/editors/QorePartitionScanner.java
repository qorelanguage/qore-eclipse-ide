package com.qoretechnologies.qore.editors;

import java.util.ArrayList;

//import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.*;

public class QorePartitionScanner extends RuleBasedPartitionScanner {
	public final static String COMMENT = "__qore_comment";
	
	public QorePartitionScanner() {

		IToken comment = new Token(COMMENT);
		
		ArrayList rules = new ArrayList();

		// Add rule for double-quoted strings to make sure they are picked up by the QoreScanner
		rules.add(new MultiLineRule("\"", "\"", Token.UNDEFINED, '\\'));
		// Add a rule for single-quoted strings to make sure they are picked up by the QoreScanner
		rules.add(new MultiLineRule("'", "'", Token.UNDEFINED, '\\'));

		// Add a rule for multi-line comments
		rules.add(new MultiLineRule("/*", "*/", comment));
		// Add a rule for single-line comments
		rules.add(new SingleLineRule("#", "\n", comment));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
