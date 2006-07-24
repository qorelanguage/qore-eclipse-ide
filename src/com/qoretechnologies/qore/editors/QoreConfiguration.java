package com.qoretechnologies.qore.editors;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.qoretechnologies.qore.completion.QoreCompletionProcessor;

public class QoreConfiguration extends SourceViewerConfiguration {
	private QoreDoubleClickStrategy doubleClickStrategy;

	private QoreScanner scanner;

	private ColorManager colorManager;

	public QoreConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		IAutoEditStrategy strategy = (IDocument.DEFAULT_CONTENT_TYPE
				.equals(contentType) ? new QoreAutoIndentStrategy()
				: new DefaultIndentLineAutoEditStrategy());
		return new IAutoEditStrategy[] { strategy };
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				QorePartitionScanner.COMMENT, };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new QoreDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected QoreScanner getQoreScanner() {
		if (scanner == null) {
			scanner = new QoreScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(
					colorManager.getColor(IQoreColorConstants.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getQoreScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager
						.getColor(IQoreColorConstants.COMMENT)));
		reconciler.setDamager(ndr, QorePartitionScanner.COMMENT);
		reconciler.setRepairer(ndr, QorePartitionScanner.COMMENT);

		return reconciler;
	}

	// #!PP^
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant ca = new ContentAssistant();
		IContentAssistProcessor pr = new QoreCompletionProcessor();
		ca.setContentAssistProcessor(pr, IDocument.DEFAULT_CONTENT_TYPE);
		ca.enableAutoActivation(true);
		//ca.enableAutoInsert(true); // let the assistent display constructor parameters
		ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return ca;
	}
	// #!PP$
}