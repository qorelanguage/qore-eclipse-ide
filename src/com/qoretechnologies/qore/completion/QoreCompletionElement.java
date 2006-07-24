package com.qoretechnologies.qore.completion;

public class QoreCompletionElement implements Comparable
{
	private String name;

	private String description;

	private String paramString = "";

	private org.eclipse.swt.graphics.Image image;
	
	private String className;

	private boolean classMethod;
	
	public QoreCompletionElement()
	{
	}

	public QoreCompletionElement(String name, String description, org.eclipse.swt.graphics.Image image )
	{
		this.name = name;
		this.description = description;
		this.image = image;
	}
	

	public boolean isClassMethod() {
		return classMethod;
	}

	public void setClassMethod(boolean classMethod) {
		this.classMethod = classMethod;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public org.eclipse.swt.graphics.Image getImage()
	{
		return image;
	}

	public void setImage(org.eclipse.swt.graphics.Image image)
	{
		this.image = image;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof QoreCompletionElement))
			throw new IllegalArgumentException(
					"Trying to compare QoreElement instance to an instance of different class!");
		return name.equals(((QoreCompletionElement) obj).getName());
	}

	public int compareTo(Object obj)
	{
		if (!(obj instanceof QoreCompletionElement))
			throw new IllegalArgumentException(
					"Trying to compare QoreElement instance to an instance of different class!");
		return this.getName().compareTo(((QoreCompletionElement) obj).getName());
	}

	public String getParamString()
	{
		return paramString;
	}

	public void setParamString(String paramString)
	{
		this.paramString = paramString;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

}
