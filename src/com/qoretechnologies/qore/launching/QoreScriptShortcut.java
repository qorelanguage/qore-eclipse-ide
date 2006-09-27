package com.qoretechnologies.qore.launching;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.qoretechnologies.qore.tools.EclipseTools;

public class QoreScriptShortcut implements ILaunchShortcut
{

	public void launch(ISelection selection, String mode)
	{
		String relativePath = selection.toString().substring(selection.toString().indexOf('/'));
		String absolutePath = EclipseTools.convertPathToAbsolute(relativePath);
		QoreRunner.executeQore(absolutePath, null);
	}

	public void launch(IEditorPart editor, String mode)
	{
		QoreRunner.executeQore(EclipseTools.getSelectedFileAbsolutePath(), null);
	}

}
