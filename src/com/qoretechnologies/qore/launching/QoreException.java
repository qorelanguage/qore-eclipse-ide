package com.qoretechnologies.qore.launching;

public class QoreException
{
	int line;

	String exceptionType;

	String description;
	
	public QoreException()
	{
		line = -1;
		exceptionType = null;
		description = null;
	}
	
	
	public QoreException(int line,String exceptionType,String description)
	{
		this.line =line;
		this.exceptionType = exceptionType;
		this.description = description;
	}
	
	public boolean isComplete()
	{
		if(line>-1 && exceptionType!=null && description!=null)
			return true;
		else
			return false;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getExceptionType()
	{
		return exceptionType;
	}

	public void setExceptionType(String exceptionType)
	{
		this.exceptionType = exceptionType;
	}

	public int getLine()
	{
		return line;
	}

	public void setLine(int line)
	{
		this.line = line;
	}
}