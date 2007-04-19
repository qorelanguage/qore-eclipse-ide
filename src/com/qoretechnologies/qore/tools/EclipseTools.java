package com.qoretechnologies.qore.tools;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class EclipseTools
{
	/**
	 * Returns default qore console which can be used e.g. to write output from
	 * Runtime.exec() to. If no instance of qore console exists, new one is
	 * created, otherwise the existing one is returned.
	 * 
	 * @return MessageConsole instance to be used by Qore plugin
	 */
	public static MessageConsole getConsole()
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		MessageConsole currentCons = null;

		for (int i = 0; i < existing.length; i++)
			if ("Qore".equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		currentCons = new MessageConsole("Qore", null);
		conMan.addConsoles(new IConsole[] { currentCons });
		conMan.showConsoleView(currentCons);
		return currentCons;
	}

	/**
	 * Retrieves the name of the selected resource in the workbench/editor.
	 * 
	 * @return
	 */
	public static String getSelectedFilename()
	{
		return DebugUITools.getSelectedResource().getName();
	}

	/**
	 * Gets absolute path of the file currently opened in active editor.
	 * 
	 * @return absolute path to currently edited resource or null of no resource
	 *         is selected
	 */
	public static String getSelectedFileAbsolutePath()
	{
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IResource file = DebugUITools.getSelectedResource();
		if (file != null)
			return workspaceRoot.getLocation() + file.getFullPath().toPortableString();
		else
			return null;
	}

	/**
	 * Returns absolute system path of the passed file.
	 * 
	 * @param relativeWrkspcResource -
	 *            path of the resource relative to the workspace path; e.g.
	 *            /TestQoreSource/ikea-constants-v1.0.q
	 * @return absolute system path, e.g.
	 *         /home/abc/workspace/TestQoreSource/ikea-constants-v1.0.q
	 */
	public static String convertPathToAbsolute(String relativeWrkspcResource)
	{
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		return workspaceRoot.getLocation() + relativeWrkspcResource;
	}

}
