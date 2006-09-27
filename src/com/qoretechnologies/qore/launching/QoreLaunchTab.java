package com.qoretechnologies.qore.launching;

import static com.qoretechnologies.qore.launching.LaunchConfigConstants.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class QoreLaunchTab extends AbstractLaunchConfigurationTab
{
	private QoreScriptLauchParamsTabUI pnlQoreSettings;

	private QoreTextParamListener listenerText = new QoreTextParamListener();

	private QoreSelectableParamListener listenerSelectable = new QoreSelectableParamListener();

	private class QoreTextParamListener extends SelectionAdapter implements ModifyListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	private class QoreSelectableParamListener implements SelectionListener
	{

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			// no action
		}
	}

	public void createControl(Composite parent)
	{
		pnlQoreSettings = new QoreScriptLauchParamsTabUI(parent, SWT.NONE);
		pnlQoreSettings.initEncodingCombo();
		pnlQoreSettings.getChckShowModErrors().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckFuncTrace().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getTxtLogLevel().addModifyListener(listenerText);
		pnlQoreSettings.getCmbEncodings().addSelectionListener(listenerSelectable);

		pnlQoreSettings.getChckClassesIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckExternIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckNoGlobVars().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckOurRequired().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckSubprogOptNotRestr().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckThreadsIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckTopLevelIllegal().addSelectionListener(listenerSelectable);
		
		pnlQoreSettings.getChkConstIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChkNamespcIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChkNewIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChkNoChanges().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChkProcContIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChkSubIllegal().addSelectionListener(listenerSelectable);
		pnlQoreSettings.getChckDisplayVersionOnly().addSelectionListener(listenerSelectable);
		
		setControl(pnlQoreSettings);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// do not preset any options
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		return canSave();
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			pnlQoreSettings.getChckShowModErrors().setSelection(configuration.getAttribute(K_chckShowModErrors, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckShowModErrors().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckFuncTrace().setSelection(configuration.getAttribute(K_chckFuncTrace, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckFuncTrace().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getTxtLogLevel().setText(configuration.getAttribute(K_txtLogLevel, "0"));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getTxtLogLevel().setText("0");
		}

		int index = -1;
		try
		{
			for (int x = 0; x < pnlQoreSettings.getCmbEncodings().getItemCount(); x++)
			{
				if (pnlQoreSettings.getCmbEncodings().getItem(x).equals(configuration.getAttribute(K_cmbEncodings, "")))
				{
					index = x;
					break;
				}
			}
		}
		catch (CoreException e)
		{
			// nothing, index remains -1
		}
		if (index != -1)
			pnlQoreSettings.getCmbEncodings().select(index);

		try
		{
			pnlQoreSettings.getChckNoGlobVars().setSelection(configuration.getAttribute(K_chckNoGlobVars, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckNoGlobVars().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkSubIllegal().setSelection(configuration.getAttribute(K_chkSubIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkSubIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckThreadsIllegal().setSelection(configuration.getAttribute(K_chckThreadsIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckThreadsIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckTopLevelIllegal().setSelection(configuration.getAttribute(K_chckTopLevelIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckTopLevelIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckClassesIllegal().setSelection(configuration.getAttribute(K_chckClassesIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckClassesIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkNamespcIllegal().setSelection(configuration.getAttribute(K_chkNamespcIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkNamespcIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckExternIllegal().setSelection(configuration.getAttribute(K_chckExternIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckExternIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkNoChanges().setSelection(configuration.getAttribute(K_chkNoChanges, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkNoChanges().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkProcContIllegal().setSelection(configuration.getAttribute(K_chkProcContIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkProcContIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkConstIllegal().setSelection(configuration.getAttribute(K_chkConstIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkConstIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChkNewIllegal().setSelection(configuration.getAttribute(K_chkNewIllegal, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChkNewIllegal().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckSubprogOptNotRestr().setSelection(configuration.getAttribute(K_chckSubprogOptNotRestr, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckSubprogOptNotRestr().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckOurRequired().setSelection(configuration.getAttribute(K_chckOurRequired, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckOurRequired().setSelection(false);
		}
		try
		{
			pnlQoreSettings.getChckDisplayVersionOnly().setSelection(configuration.getAttribute(K_chckDisplayVersion, false));
		}
		catch (CoreException e)
		{
			pnlQoreSettings.getChckDisplayVersionOnly().setSelection(false);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// save general options
		configuration.setAttribute(K_chckShowModErrors, pnlQoreSettings.getChckShowModErrors().getSelection());
		configuration.setAttribute(K_chckFuncTrace, pnlQoreSettings.getChckFuncTrace().getSelection());
		configuration.setAttribute(K_txtLogLevel, pnlQoreSettings.getTxtLogLevel().getText());
		if (pnlQoreSettings.getCmbEncodings().getSelectionIndex() != -1)
			configuration.setAttribute(K_cmbEncodings, pnlQoreSettings.getCmbEncodings().getItem(pnlQoreSettings.getCmbEncodings().getSelectionIndex()));
		// save parser options
		configuration.setAttribute(K_chckNoGlobVars, pnlQoreSettings.getChckNoGlobVars().getSelection());
		configuration.setAttribute(K_chkSubIllegal, pnlQoreSettings.getChkSubIllegal().getSelection());
		configuration.setAttribute(K_chckThreadsIllegal, pnlQoreSettings.getChckThreadsIllegal().getSelection());
		configuration.setAttribute(K_chckTopLevelIllegal, pnlQoreSettings.getChckTopLevelIllegal().getSelection());
		configuration.setAttribute(K_chckClassesIllegal, pnlQoreSettings.getChckClassesIllegal().getSelection());
		configuration.setAttribute(K_chkNamespcIllegal, pnlQoreSettings.getChkNamespcIllegal().getSelection());
		configuration.setAttribute(K_chckExternIllegal, pnlQoreSettings.getChckExternIllegal().getSelection());
		configuration.setAttribute(K_chkNoChanges, pnlQoreSettings.getChkNoChanges().getSelection());
		configuration.setAttribute(K_chkProcContIllegal, pnlQoreSettings.getChkProcContIllegal().getSelection());
		configuration.setAttribute(K_chkConstIllegal, pnlQoreSettings.getChkConstIllegal().getSelection());
		configuration.setAttribute(K_chkNewIllegal, pnlQoreSettings.getChkNewIllegal().getSelection());
		configuration.setAttribute(K_chckSubprogOptNotRestr, pnlQoreSettings.getChckSubprogOptNotRestr().getSelection());
		configuration.setAttribute(K_chckOurRequired, pnlQoreSettings.getChckOurRequired().getSelection());
		configuration.setAttribute(K_chckDisplayVersion, pnlQoreSettings.getChckDisplayVersionOnly().getSelection());
	}

	@Override
	public Image getImage()
	{
		// steal the icon from the first, common tab
		return getLaunchConfigurationDialog().getTabs()[0].getImage();
	}

	public String getName()
	{
		return "Qore settings";
	}

	@Override
	public boolean canSave()
	{
		try
		{
			Integer.parseInt(pnlQoreSettings.getTxtLogLevel().getText().trim());
		}
		catch (NumberFormatException nfe)
		{
			setErrorMessage("Log level must be an integer!");
			return false;
		}
		return true;
	}

}
