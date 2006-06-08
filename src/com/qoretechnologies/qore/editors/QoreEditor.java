package com.qoretechnologies.qore.editors;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class QoreEditor extends TextEditor {

	private ColorManager colorManager;

	public QoreEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new QoreConfiguration(colorManager));
		setDocumentProvider(new QoreDocumentProvider());
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	// #!PP^
	protected void createActions() {
		super.createActions();
		ResourceBundle rb = new ContentAssistResource();
		Action action = new ContentAssistAction(rb, "ContentAssistProposal.",
				this);
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssistProposal", action);
		markAsStateDependentAction("ContentAssistProposal", true);
	}

	class ContentAssistResource extends ResourceBundle {

		private Hashtable<String,String> labels = new Hashtable<String,String>();

		public ContentAssistResource() {
			super();
			labels.put("ContentAssistProposal.label", "Content assist");
			labels.put("ContentAssistProposal.tooltip", "Content assist");
			labels.put("ContentAssistProposal.description",
					"Provides Qore Content Assistance");
		}

		public Object handleGetObject(String key) {
			return labels.get(key);
		}

		public Enumeration<String> getKeys() {
			return labels.keys();
		}

	}
	// #!PP$

}
