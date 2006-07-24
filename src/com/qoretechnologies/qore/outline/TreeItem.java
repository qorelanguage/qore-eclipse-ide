package com.qoretechnologies.qore.outline;

public class TreeItem
{
	public enum ITEM_TYPE { FUNCTION, VARIABLE, CONSTANT, NAMESPACE, CLASS };
	private ITEM_TYPE type;
	private String name;
	private String desc;
	private int offset;
	private int length;
	
	public TreeItem()
	{
	}

	public TreeItem(ITEM_TYPE type, String name, String description)
	{
		this.type = type;
		this.name = name;
		this.desc = description;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public ITEM_TYPE getType()
	{
		return type;
	}

	public void setType(ITEM_TYPE type)
	{
		this.type = type;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}
	

}
