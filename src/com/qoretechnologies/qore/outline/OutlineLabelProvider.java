package com.qoretechnologies.qore.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.qoretechnologies.qore.tools.ResourceLoader;

public class OutlineLabelProvider implements ILabelProvider
{

	public OutlineLabelProvider()
	{
		super();
	}

	public Image getImage(Object element)
	{
		if (element instanceof TreeItem)
		{
			try
			{
				if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.VARIABLE)
				{
					return ResourceLoader.getIcon(ResourceLoader.ICONTYPE.VARIABLE);
				}
				if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.FUNCTION)
				{
					return ResourceLoader.getIcon(ResourceLoader.ICONTYPE.FUNCTION);
				}
				if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.CONSTANT)
				{
					return ResourceLoader.getIcon(ResourceLoader.ICONTYPE.CONSTANT);
				}
				if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.NAMESPACE)
				{
					return ResourceLoader.getIcon(ResourceLoader.ICONTYPE.NAMESPACE);
				}
				if (((TreeItem) element).getType() == TreeItem.ITEM_TYPE.CLASS)
				{
					return ResourceLoader.getIcon(ResourceLoader.ICONTYPE.CLASS);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getText(Object element)
	{
		if (element instanceof TreeItem)
			return ((TreeItem) element).getName();
		return element.toString();
	}

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}

}
