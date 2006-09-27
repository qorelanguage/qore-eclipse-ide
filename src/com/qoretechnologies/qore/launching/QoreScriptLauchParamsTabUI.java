package com.qoretechnologies.qore.launching;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class QoreScriptLauchParamsTabUI extends Composite
{
	private Button chckDisplayVersionOnly;
	private Button chckOurRequired;
	private Button chckSubprogOptNotRestr;
	private Button chkNewIllegal;
	private Button chkConstIllegal;
	private Button chkProcContIllegal;
	private Button chkNoChanges;
	private Button chckExternIllegal;
	private Button chkNamespcIllegal;
	private Button chckClassesIllegal;
	private Button chckTopLevelIllegal;
	private Button chckThreadsIllegal;
	private Button chkSubIllegal;
	private Button chckNoGlobVars;
	private Button chckShowModErrors;
	private Button chckFuncTrace;
	private Combo cmbEncodings;
	private Text txtLogLevel;

	/**
	 * Create the composite
	 * 
	 * @param parent
	 * @param style
	 */
	public QoreScriptLauchParamsTabUI(Composite parent, int style)
	{
		super(parent, style);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout(gridLayout);

		final Group generalOptionsGroup = new Group(this, SWT.NONE);
		generalOptionsGroup.setText("General options");
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1);
		generalOptionsGroup.setLayoutData(gridData);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		generalOptionsGroup.setLayout(gridLayout_1);

		chckShowModErrors = new Button(generalOptionsGroup, SWT.CHECK);
		final GridData gridData_3 = new GridData();
		chckShowModErrors.setLayoutData(gridData_3);
		chckShowModErrors.setText("show module errors during execution");
		new Label(generalOptionsGroup, SWT.NONE);

		chckFuncTrace = new Button(generalOptionsGroup, SWT.CHECK);
		final GridData gridData_5 = new GridData();
		chckFuncTrace.setLayoutData(gridData_5);
		chckFuncTrace.setText("turn on function tracing");
		new Label(generalOptionsGroup, SWT.NONE);

		final Label debuggingLevelLabel = new Label(generalOptionsGroup, SWT.NONE);
		final GridData gridData_2 = new GridData();
		debuggingLevelLabel.setLayoutData(gridData_2);
		debuggingLevelLabel.setText("debugging level (higher number=more output)");

		txtLogLevel = new Text(generalOptionsGroup, SWT.BORDER);
		txtLogLevel.setText("1");

		final Label charsetEncodingLabel = new Label(generalOptionsGroup, SWT.NONE);
		final GridData gridData_1 = new GridData();
		charsetEncodingLabel.setLayoutData(gridData_1);
		charsetEncodingLabel.setText("charset encoding");

		cmbEncodings = new Combo(generalOptionsGroup, SWT.READ_ONLY);

		final Group agsGroup = new Group(this, SWT.NONE);
		agsGroup.setText("Parse options");
		final GridData gridData_4 = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 2);
		agsGroup.setLayoutData(gridData_4);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 2;
		agsGroup.setLayout(gridLayout_2);

		chckNoGlobVars = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_6 = new GridData();
		chckNoGlobVars.setLayoutData(gridData_6);
		chckNoGlobVars.setText("no global variables");

		chkNoChanges = new Button(agsGroup, SWT.CHECK);
		chkNoChanges.setLayoutData(new GridData());
		chkNoChanges.setText("no changes to parse options in program");

		chkSubIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_7 = new GridData();
		chkSubIllegal.setLayoutData(gridData_7);
		chkSubIllegal.setText("subroutine definitions illegal");

		chkProcContIllegal = new Button(agsGroup, SWT.CHECK);
		chkProcContIllegal.setText("process control illegal (fork(), exit(), etc)");

		chckThreadsIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_8 = new GridData();
		chckThreadsIllegal.setLayoutData(gridData_8);
		chckThreadsIllegal.setText("thread operations illegal");

		chkConstIllegal = new Button(agsGroup, SWT.CHECK);
		chkConstIllegal.setText("constant definitions illegal");

		chckTopLevelIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_9 = new GridData();
		chckTopLevelIllegal.setLayoutData(gridData_9);
		chckTopLevelIllegal.setText("top-level statements illegal");

		chkNewIllegal = new Button(agsGroup, SWT.CHECK);
		chkNewIllegal.setText("'new' operator illegal");

		chckClassesIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_10 = new GridData();
		chckClassesIllegal.setLayoutData(gridData_10);
		chckClassesIllegal.setText("class definitions illegal");

		chckSubprogOptNotRestr = new Button(agsGroup, SWT.CHECK);
		chckSubprogOptNotRestr.setLayoutData(new GridData());
		chckSubprogOptNotRestr.setText("do not restrict subprograms' parse options");

		chkNamespcIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_11 = new GridData();
		chkNamespcIllegal.setLayoutData(gridData_11);
		chkNamespcIllegal.setText("namespace declarations illegal");

		chckOurRequired = new Button(agsGroup, SWT.CHECK);
		chckOurRequired.setLayoutData(new GridData());
		chckOurRequired.setText("requires global variables to be declared with 'our'");

		chckExternIllegal = new Button(agsGroup, SWT.CHECK);
		final GridData gridData_12 = new GridData();
		chckExternIllegal.setLayoutData(gridData_12);
		chckExternIllegal.setText("access to external processes illegal");

		chckDisplayVersionOnly = new Button(agsGroup, SWT.CHECK);
		chckDisplayVersionOnly.setText("show qore version and quit");
		//
	}

	public void initEncodingCombo()
	{
		cmbEncodings.add("US-ASCII");
		cmbEncodings.add("UTF-8");
		cmbEncodings.add("ISO-8859-1");
		cmbEncodings.add("ISO-8859-2");
		cmbEncodings.add("ISO-8859-3");
		cmbEncodings.add("ISO-8859-4");
		cmbEncodings.add("ISO-8859-5");
		cmbEncodings.add("ISO-8859-6");
		cmbEncodings.add("ISO-8859-7");
		cmbEncodings.add("ISO-8859-8");
		cmbEncodings.add("ISO-8859-9");
		cmbEncodings.add("ISO-8859-10");
		cmbEncodings.add("ISO-8859-11");
		cmbEncodings.add("ISO-8859-13");
		cmbEncodings.add("ISO-8859-14");
		cmbEncodings.add("ISO-8859-15");
		cmbEncodings.add("ISO-8859-16");
		cmbEncodings.add("KOI8-R");
		cmbEncodings.add("KOI8-U");
		cmbEncodings.add("KOI7");
		cmbEncodings.select(0);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	protected void checkSubclass()
	{
		// Disable the check that prevents subclassing of SWT components
	}

	public Button getChckClassesIllegal()
	{
		return chckClassesIllegal;
	}

	public Button getChckExternIllegal()
	{
		return chckExternIllegal;
	}

	public Button getChckFuncTrace()
	{
		return chckFuncTrace;
	}

	public Button getChckNoGlobVars()
	{
		return chckNoGlobVars;
	}

	public Button getChckOurRequired()
	{
		return chckOurRequired;
	}

	public Button getChckShowModErrors()
	{
		return chckShowModErrors;
	}

	public Button getChckSubprogOptNotRestr()
	{
		return chckSubprogOptNotRestr;
	}

	public Button getChckThreadsIllegal()
	{
		return chckThreadsIllegal;
	}

	public Button getChckTopLevelIllegal()
	{
		return chckTopLevelIllegal;
	}

	public Button getChkConstIllegal()
	{
		return chkConstIllegal;
	}

	public Button getChkNamespcIllegal()
	{
		return chkNamespcIllegal;
	}

	public Button getChkNewIllegal()
	{
		return chkNewIllegal;
	}

	public Button getChkNoChanges()
	{
		return chkNoChanges;
	}

	public Button getChkProcContIllegal()
	{
		return chkProcContIllegal;
	}

	public Button getChkSubIllegal()
	{
		return chkSubIllegal;
	}

	public Button getChckDisplayVersionOnly()
	{
		return chckDisplayVersionOnly;
	}

	public Combo getCmbEncodings()
	{
		return cmbEncodings;
	}

	public Text getTxtLogLevel()
	{
		return txtLogLevel;
	}

}
