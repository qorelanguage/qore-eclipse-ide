package com.qoretechnologies.qore.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.MessageConsoleStream;

import com.qoretechnologies.qore.tools.EclipseTools;

public class GenerateDocAction implements IObjectActionDelegate
{
	private final int UNIX_NEW_LINE = 10;

	/**
	 * Constructor for Action1.
	 */
	public GenerateDocAction()
	{
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{

	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action)
	{
		MessageConsoleStream out = EclipseTools.getConsole().newMessageStream();
		String processedDoc = EclipseTools.getSelectedFileAbsolutePath();

		out.println("Generating Qore documentation ...");

		List<String> lines;
		try
		{
			lines = getAllLines(processedDoc);
			List<FunctionHeader> functionHeaders = parseFunctions(lines);
			
			if(areHeadersAlreadyGenerated(lines,functionHeaders))
			{
				
			}

			// insert the documentation lines as needed
			int origPosOffsetDiff = 0;
			for (FunctionHeader h : functionHeaders)
			{
				StringWriter newLines = new StringWriter();
				for (int x = 0; x < 80; x++)
					newLines.append("# ");
				newLines.append("\n");
				newLines.append("# name: " + h.getName() + "\n");
				newLines.append("# desc: " + h.toString() + "\n");
				Iterator i = h.getParams().iterator();
				while (i.hasNext())
					newLines.append("# param: " + i.next() + " - \n");
				for (int x = 0; x < 80; x++)
					newLines.append("# ");

				((Vector<String>) lines).insertElementAt(newLines.toString(), h.getInsertOffset() + origPosOffsetDiff);
				origPosOffsetDiff++;
			}
			// now save the modified source file
			saveDocumentedSource(lines, processedDoc);
			out.println("Done, "+origPosOffsetDiff+" functions documented.");
		}
		catch (IOException e)
		{
			out.println("Error generating documentation: " + e.getMessage());
		}
	}

	private boolean areHeadersAlreadyGenerated(List<String> lines, List<FunctionHeader> functionHeaders)
	{
		
		return false;
	}

	private void saveDocumentedSource(List<String> lines, String processedDoc) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(processedDoc));
		for (String line : lines)
		{
			bw.write(line);
			bw.write(UNIX_NEW_LINE); // only LF, no CR
		}
		bw.flush();
		bw.close();
	}

	private List<FunctionHeader> parseFunctions(List<String> lines)
	{
		ArrayList<FunctionHeader> headers = new ArrayList<FunctionHeader>();
		Pattern p = Pattern.compile("sub\\s(.*)\\((.*)\\)");

		for (int x = 0; x < lines.size(); x++)
		{
			Matcher m = p.matcher(lines.get(x));
			if (m.find())
			{
				String functionName = m.group(1);
				ArrayList<String> params = new ArrayList<String>();
				StringTokenizer tok = new StringTokenizer(m.group(2), ",");
				while (tok.hasMoreTokens())
					params.add(tok.nextToken().trim());

				headers.add(new FunctionHeader(functionName, params, x));
			}
		}
		return headers;
	}

	private List<String> getAllLines(String selectedFileAbsolutePath) throws IOException
	{
		Vector<String> lines = new Vector<String>();
		BufferedReader r = new BufferedReader(new FileReader(selectedFileAbsolutePath));
		while (r.ready())
			lines.add(r.readLine());
		r.close();
		return lines;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	class FunctionHeader
	{
		private String name;

		private ArrayList<String> params;

		private int insertOffset;

		public FunctionHeader(String name, ArrayList<String> params, int insertOffset)
		{
			this.name = name;
			this.params = params;
			this.insertOffset = insertOffset;
		}

		public String getName()
		{
			return name;
		}

		public ArrayList<String> getParams()
		{
			return params;
		}

		public int getInsertOffset()
		{
			return insertOffset;
		}

		@Override
		public String toString()
		{
			StringBuffer buf = new StringBuffer(this.getName());
			buf.append("(");
			for (String param : getParams())
				buf.append(param + ",");

			if (buf.charAt(buf.length() - 1) == ',')
				buf.deleteCharAt(buf.length() - 1);
			buf.append(")");
			return buf.toString();
		}

	}

}
