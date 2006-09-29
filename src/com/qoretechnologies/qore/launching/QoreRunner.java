package com.qoretechnologies.qore.launching;

import static com.qoretechnologies.qore.launching.LaunchConfigConstants.K_chckDisplayVersion;
import static com.qoretechnologies.qore.launching.LaunchConfigConstants.QORE_EXECUTABLE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.texteditor.MarkerUtilities;

import com.qoretechnologies.qore.tools.EclipseTools;

public class QoreRunner
{
	private final static Color CONSOLE_RED = new Color(Display.getDefault(), new RGB(255, 0, 0));

	private final static Color CONSOLE_BLACK = new Color(Display.getDefault(), new RGB(0, 0, 0));

	private final static Color CONSOLE_BLUE = new Color(Display.getDefault(), new RGB(0, 0, 255));

	private final static Pattern patternQoreExcLine = Pattern.compile("unhandled QORE System exception thrown at .*:(\\d+)");

	private final static Pattern patternQoreExcInfo = Pattern.compile("(.*EXCEPTION): (.*)");

	public static void executeQore(String scriptFile, Map<String, Object> cmdLineOptions)
	{
		// check the scriptFile
		if (scriptFile == null
				&& (!cmdLineOptions.containsKey(K_chckDisplayVersion) || (cmdLineOptions.containsKey(K_chckDisplayVersion) && ((Boolean) cmdLineOptions.get(K_chckDisplayVersion)) == false)))
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
		out.println(SimpleDateFormat.getDateTimeInstance().format(new Date()) + ": Executing: " + cmdLine + "");

		// delete the old problem markers
		try
		{
			ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(null,true,IResource.DEPTH_INFINITE);
		}
		catch (CoreException e1)
		{
			// nada
		}

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

			QoreException qe = new QoreException();
			while ((s = is.readLine()) != null)
			{
				out.println(s);
				// try to parse the line to get QoreException instance
				fillQoreExceptionFromLine(qe, s);
				if (qe.isComplete()) // then create the marker for the error
										// in the source file
				{
					try
					{
						HashMap<String, Object> map = new HashMap<String, Object>();
						MarkerUtilities.setLineNumber(map, qe.getLine());
						MarkerUtilities.setMessage(map, qe.getDescription());
						map.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						MarkerUtilities.createMarker(DebugUITools.getSelectedResource(), map, IMarker.PROBLEM);
					}
					catch (CoreException e)
					{
						// do nothing if the marker cannot be created
					}
					qe = new QoreException();
				}
			}
			long endTms = System.currentTimeMillis();
			try
			{
				if (p.waitFor() != 0)
					out.setColor(CONSOLE_RED);
			}
			catch (InterruptedException e)
			{
				// nothing, just let the color as it is
			}
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

	private static void fillQoreExceptionFromLine(QoreException qe, String line)
	{
		Matcher m = patternQoreExcLine.matcher(line);
		if (m.matches())
			qe.setLine(Integer.parseInt(m.group(1)));
		else
		{
			m = patternQoreExcInfo.matcher(line);
			if (m.matches())
			{
				qe.setExceptionType(m.group(1));
				qe.setDescription(m.group(2));
			}
		}
	}

}
