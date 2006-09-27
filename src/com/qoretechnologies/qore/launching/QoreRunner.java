package com.qoretechnologies.qore.launching;

import static com.qoretechnologies.qore.launching.LaunchConfigConstants.QORE_EXECUTABLE;
import static com.qoretechnologies.qore.launching.LaunchConfigConstants.K_chckDisplayVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

import com.qoretechnologies.qore.tools.EclipseTools;
import static com.qoretechnologies.qore.launching.LaunchConfigConstants.QORE_EXECUTABLE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

import com.qoretechnologies.qore.tools.EclipseTools;

public class QoreRunner
{
	private final static Color CONSOLE_RED = new Color(Display.getDefault(), new RGB(255, 0, 0));

	private final static Color CONSOLE_BLACK = new Color(Display.getDefault(), new RGB(0, 0, 0));

	private final static Color CONSOLE_BLUE = new Color(Display.getDefault(), new RGB(0, 0, 255));

	public static void executeQore(String scriptFile, Map<String, Object> cmdLineOptions)
	{
		// check the scriptFile
		if (scriptFile == null && !cmdLineOptions.containsKey(K_chckDisplayVersion))
		{
			MessageConsoleStream out = EclipseTools.getConsole().newMessageStream();
			out.println("There is nothing to run by qore. Please select or open a source file.");
			return;
		}
		// prepare the command
		List<String> qoreCmdLineElements = new ArrayList<String>();
		qoreCmdLineElements.add(QORE_EXECUTABLE);

		// use command line options if available from configuration
		if (cmdLineOptions != null)
		{
			for (String option : cmdLineOptions.keySet())
			{
				if (cmdLineOptions.get(option) instanceof String)
					qoreCmdLineElements.add((String) option + (String) cmdLineOptions.get(option));
				else
					if (cmdLineOptions.get(option) instanceof Boolean && (Boolean) cmdLineOptions.get(option) == true)
						qoreCmdLineElements.add(option);
			}
		}
		qoreCmdLineElements.add(scriptFile);

		// construct command line for debug purposes
		StringBuffer cmdLine = new StringBuffer();
		for (String param : qoreCmdLineElements)
			cmdLine.append(param + " ");
		cmdLine.deleteCharAt(cmdLine.length() - 1);

		// inform that we are starting
		MessageConsoleStream out = EclipseTools.getConsole().newMessageStream();
		out.setColor(CONSOLE_BLACK);
		out.println(SimpleDateFormat.getDateTimeInstance().format(new Date()) + ": Executing: \"" + cmdLine + "\"");
 
		// construct & start the process
		ProcessBuilder pb = new ProcessBuilder(qoreCmdLineElements);
		pb.redirectErrorStream(true);
		try
		{
			long startTms = System.currentTimeMillis();
			Process p = pb.start();
			BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			out = EclipseTools.getConsole().newMessageStream();
			out.setColor(CONSOLE_BLUE);
			while ((s = is.readLine()) != null)
				out.println(s);
			long endTms = System.currentTimeMillis();
			if (p.exitValue() != 0)
				out.setColor(CONSOLE_RED);
			out = EclipseTools.getConsole().newMessageStream();
			out.setColor(CONSOLE_BLACK);
			out.println("Execution completed in " + new SimpleDateFormat("mm:ss SS ").format(new Date(endTms - startTms)) + "msec.");
			out.flush();
		}
		catch (IOException e)
		{
			out = EclipseTools.getConsole().newMessageStream();
			out.setColor(CONSOLE_RED);
			out.println("Error during execution: " + e.getMessage());
		}
	}

}