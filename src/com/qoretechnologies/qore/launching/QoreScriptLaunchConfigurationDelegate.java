package com.qoretechnologies.qore.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import com.qoretechnologies.qore.tools.EclipseTools;

public class QoreScriptLaunchConfigurationDelegate implements ILaunchConfigurationDelegate
{
	@SuppressWarnings("unchecked")
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask("Executing qore", IProgressMonitor.UNKNOWN);
		QoreRunner.executeQore(EclipseTools.getSelectedFileAbsolutePath(), configuration.getAttributes());
		monitor.done();
	}
}
