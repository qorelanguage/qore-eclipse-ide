package com.qoretechnologies.qore.outline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class OutlineContentProvider implements ITreeContentProvider
{

	private IDocumentProvider documentProvider;

	protected final static String TAG_POSITIONS = "__tag_positions";

	protected IPositionUpdater positionUpdater = new DefaultPositionUpdater(TAG_POSITIONS);

	public OutlineContentProvider(TreeViewer treeViewer, IDocumentProvider provider)
	{
		super();
		this.documentProvider = provider;
	}

	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof TreeItem)
		{

		}
		return null; // for now the implementation does not support Qore
					 // classes
	}

	public Object getParent(Object element)
	{
		return null; // flat Qore source, no classes
	}

	public boolean hasChildren(Object element)
	{
		if (element instanceof TreeItem)
		{
			if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.VARIABLE
					|| ((TreeItem) element).getType() == TreeItem.ITEM_TYPE.FUNCTION
					|| ((TreeItem) element).getType() == TreeItem.ITEM_TYPE.CONSTANT )
				return false;
			else
				// namespace or class
				return true;
		}
		return true;
	}

	/**
	 * 
	 * @param inputElement
	 * @param regex to search for
	 * @param fromOffset start from here
	 * @param toOffset search only to this position in document
	 * @param type type of the TreeItem which is to be returned
	 * @param searchInEntireDoc true if search should be performed in entire document, false if the search should be done only in current block
	 * @return
	 */
	private List getElementsByRegex(Object inputElement, String regex, int fromOffset, int toOffset,
			TreeItem.ITEM_TYPE type)
	{
		ArrayList<TreeItem> results = new ArrayList<TreeItem>();
		IDocument doc = documentProvider.getDocument(inputElement);
		FindReplaceDocumentAdapter finder = new FindReplaceDocumentAdapter(doc);
		int lastOffset = fromOffset;
		Pattern p = Pattern.compile(regex);

		IRegion var = null;
		do
		{
			try
			{
				var = finder.find(lastOffset, regex, true, false, false, true);
				if (var != null && var.getOffset() + var.getLength() < toOffset)
				{
					lastOffset = var.getOffset() + var.getLength();
					String text = doc.get(var.getOffset(), var.getLength());

					// use the group from regex as label for the tree item
					Matcher matcher = p.matcher(text);
					matcher.matches(); // will match always, we just need
										// capture the group
					if (matcher.groupCount() > 0)
						text = matcher.group(1).trim();
					else
						text = text.substring(text.indexOf(" ")).trim();

					TreeItem temp = new TreeItem(type, text, text);
					// set offset at the start of the word (ignore the
					// whitespace before it)
					temp.setOffset(var.getOffset() + (var.getLength() - text.length()));
					temp.setLength(var.getLength());
					results.add(temp);
				}
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
		while (var != null && var.getOffset() + var.getLength() < toOffset);
		return results;
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement)
	{
		ArrayList results = new ArrayList();
		int docLength = documentProvider.getDocument(inputElement).getLength();
		List namespaces = getElementsByRegex(inputElement, "^\\s*namespace\\s*\\w*", 0, docLength,
				TreeItem.ITEM_TYPE.NAMESPACE);
		List globals = getElementsByRegex(inputElement, "our\\s*\\$\\w*", 0, docLength, TreeItem.ITEM_TYPE.VARIABLE);
		List consts = getElementsByRegex(inputElement, "const\\s*(\\w*.*=.*);", 0, docLength, TreeItem.ITEM_TYPE.CONSTANT);
		List subs = getElementsByRegex(inputElement, "^\\s*sub\\s.*\\)", 0, docLength, TreeItem.ITEM_TYPE.FUNCTION);

		results.addAll(namespaces);
		results.addAll(globals);
		results.addAll(consts);
		results.addAll(subs);

		return results.toArray();
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (oldInput != null)
		{
			IDocument document = documentProvider.getDocument(oldInput);
			if (document != null)
			{
				try
				{
					document.removePositionCategory(TAG_POSITIONS);
				}
				catch (BadPositionCategoryException x)
				{
				}
				document.removePositionUpdater(positionUpdater);
			}
		}
		// input = (IEditorInput) newInput;
		if (newInput != null)
		{
			IDocument document = documentProvider.getDocument(newInput);
			if (document != null)
			{
				document.addPositionCategory(TAG_POSITIONS);
				document.addPositionUpdater(positionUpdater);
			}
		}
	}

}
