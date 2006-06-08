package com.qoretechnologies.qore.editors;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class QoreCompletionProcessor implements IContentAssistProcessor {

	private final IContextInformation[] NO_CONTEXTS = new IContextInformation[0];

	private final char[] PROPOSAL_ACTIVATION_CHARS = new char[] { '.', '(', '{' };

	private ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		try {
			IDocument document = viewer.getDocument();
			Collection<ICompletionProposal> results = QoreCompletionSuggester
					.getInstance().getProposals(document, offset);
			return (ICompletionProposal[]) results
					.toArray(new ICompletionProposal[results.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return NO_COMPLETIONS;
		}
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return NO_CONTEXTS;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return PROPOSAL_ACTIVATION_CHARS;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
