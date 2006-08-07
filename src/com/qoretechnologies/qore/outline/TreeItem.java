package com.qoretechnologies.qore.outline;

import org.eclipse.jface.text.IDocument;

public class TreeItem
{
	public enum ITEM_TYPE
	{
		FUNCTION, VARIABLE, CONSTANT, NAMESPACE, CLASS
	};

	private ITEM_TYPE type;

	private String name;

	private String desc;

	private int offset;

	private int length;

	private IDocument parentDoc;

	public TreeItem()
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof TreeItem))
			return false;

		TreeItem t = (TreeItem) obj;
		if (t.getName() == this.getName() && t.getDesc() == this.getDesc() && t.getLength() == this.getLength() && t.getOffset() == this.getOffset()
				&& t.getParentDoc() == t.getParentDoc() && t.getType() == this.getType())
			return true;
		else
			return false;
	}

	public TreeItem(IDocument parentDoc, ITEM_TYPE type, String name, String description)
	{
		this.type = type;
		this.name = name;
		this.desc = description;
		this.parentDoc = parentDoc;
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

	public IDocument getParentDoc()
	{
		return parentDoc;
	}

	public void setParentDoc(IDocument parentDoc)
	{
		this.parentDoc = parentDoc;
	}

}
