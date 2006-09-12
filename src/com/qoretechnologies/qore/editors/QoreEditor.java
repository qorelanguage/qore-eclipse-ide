package com.qoretechnologies.qore.editors;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import com.qoretechnologies.qore.outline.EditorContentOutlinePage;

public class QoreEditor extends TextEditor
{

	private ColorManager colorManager;

	private EditorContentOutlinePage outlinePage;

	private IEditorInput input;

	public QoreEditor()
	{
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new QoreConfiguration(colorManager));
		setDocumentProvider(new QoreDocumentProvider());
	}

	public void dispose()
	{
		colorManager.dispose();
		if (outlinePage != null)
			outlinePage.setInput(null);
		super.dispose();
	}
	
	protected void editorSaved()
	{
		if (outlinePage != null)
			outlinePage.update();	
		super.editorSaved();
	}

	protected void doSetInput(IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		this.input = newInput;

		if (outlinePage != null)
			outlinePage.setInput(input);
	}

	protected IDocument getInputDocument()
	{
		IDocument document = getDocumentProvider().getDocument(input);
		return document;
	}

	public IEditorInput getInput()
	{
		return input;
	}

	/**
	 * Supply our outline page impl if asked for, return the default adapter for other classes otherwise.
	 */
	public Object getAdapter(Class adapter)
	{
		if (adapter.getSimpleName().equals("IContentOutlinePage"))
		{
			if (outlinePage == null)
			{
				outlinePage = new EditorContentOutlinePage(this);
				if (getEditorInput() != null)
					outlinePage.setInput(getEditorInput());
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	protected void createActions()
	{
		super.createActions();
		ResourceBundle rb = new ContentAssistResource();
		Action action = new ContentAssistAction(rb, "ContentAssistProposal.", this);
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssistProposal", action);
		markAsStateDependentAction("ContentAssistProposal", true);
	}

	/**
	 * Inner class for content assist initialization.
	 * @author potancpa
	 *
	 */
	class ContentAssistResource extends ResourceBundle
	{

		private Hashtable<String, String> labels = new Hashtable<String, String>();

		public ContentAssistResource()
		{
			super();
			labels.put("ContentAssistProposal.label", "Content assist");
			labels.put("ContentAssistProposal.tooltip", "Content assist");
			labels.put("ContentAssistProposal.description", "Provides Qore Content Assistance");
		}

		public Object handleGetObject(String key)
		{
			return labels.get(key);
		}

		public Enumeration<String> getKeys()
		{
			return labels.keys();
		}

	}
}
