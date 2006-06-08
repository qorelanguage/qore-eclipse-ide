package com.qoretechnologies.qore.editors;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CompletionDocbookLoader {
	private Image ICON_FUNCTION, ICON_VARIABLE;

	private static HashMap<ICONTYPE, Image> icons;

	enum ICONTYPE {
		FUNCTION, VARIABLE
	};

	public CompletionDocbookLoader() {
		try {
			icons = new HashMap<ICONTYPE, Image>();

			InputStream is = getClass().getResourceAsStream("resources/dot_function.gif");
			ICON_FUNCTION = new Image(Display.getDefault(), is);
			icons.put(ICONTYPE.FUNCTION, ICON_FUNCTION);

			is = getClass().getResourceAsStream("resources/dot_variable.gif");
			ICON_VARIABLE = new Image(Display.getDefault(), is);
			icons.put(ICONTYPE.VARIABLE, ICON_VARIABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method loads functions/other elements from docbook source. The
	 * elements are cached internally.
	 * 
	 * @return collection of loaded QoreElement instances
	 */
	@SuppressWarnings("unchecked")
	public Collection<QoreCompletionElement> loadElements(String resourceName) {

		Collection<QoreCompletionElement> temp = new ArrayList<QoreCompletionElement>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		InputStream is = getClass().getResourceAsStream(resourceName);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			NodeList allSect = doc.getElementsByTagName("sect2");
			if (allSect != null && allSect.getLength() > 0) {
				for (int i = 0; i < allSect.getLength(); i++) {
					Element el = (Element) allSect.item(i);
					QoreCompletionElement qe = getQoreElement(el);
					qe.setImage(ICON_FUNCTION);
					temp.add(qe);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort((List) temp);
		return temp;
	}

	/**
	 * Returns the QoreElement entity based on the passed sect2 xml element.
	 * 
	 * @param sect2
	 *            DOM element describing the function
	 * @return QoreElement instance
	 */
	private QoreCompletionElement getQoreElement(Element sect2) {

		QoreCompletionElement qe = new QoreCompletionElement();

		String title = sect2.getElementsByTagName("title").item(0).getTextContent();
		qe.setName(title);

		NodeList varList = sect2.getElementsByTagName("varlistentry");
		String synops = ((Element) varList.item(0)).getElementsByTagName("para").item(0).getTextContent();

		NodeList paramList = ((Element) varList.item(1)).getElementsByTagName("replaceable");
		if (paramList != null && paramList.getLength() > 0) {
			String params = paramList.item(0).getTextContent();
			qe.setParamString(params);
		}

		NodeList descList = ((Element) varList.item(1)).getElementsByTagName("programlisting");
		if (descList != null && descList.getLength() > 0) {
			String usage = ((Element) descList.item(0)).getTextContent();
			synops += "\n\nUsage: " + usage;
			qe.setDescription(synops);
		}
		// the icon for the item will be set directly in the load*() function
		return qe;
	}

	public static Image getIcon(ICONTYPE type) {
		return icons.get(type);
	}
}
