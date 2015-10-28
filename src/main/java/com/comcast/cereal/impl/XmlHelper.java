/**
 * Copyright 2012 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.cereal.impl;

import java.io.InputStream;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.comcast.cereal.CerealException;
import com.comcast.cereal.engines.XmlCerealEngine;

/**
 * Helper class for converting the cereal-supported set of objects to and from XML. The exact rules
 * for conversion are documented on {@link XmlCerealEngine}.
 * 
 * @see XmlCerealEngine
 * 
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class XmlHelper {

    private static final String TYPE_KEY = "_t_";
    private static final String TYPE_STRING = "s";
    private static final String TYPE_LIST = "l";

    private final boolean prettyPrint;
    private final String rootElementName;

    /**
     * Create a new XmlHelper object and turn on or off the pretty print.
     * 
     * @param prettyPrint
     *            If <code>true</code>, this will output all XML messages with nice indentations. If
     *            <code>false</code>, whitespace will be minimized.
     * @param rootElementName
     *            the name of the root element
     */
    public XmlHelper(boolean prettyPrint, String rootElementName) {
        this.prettyPrint = prettyPrint;
        this.rootElementName = rootElementName;
    }

    /**
     * Write the cereal-compatible object to the given writer following all XML encoding rules.
     * 
     * @param cereal
     *            the cereal-compatible object
     * @param writer
     *            the writer to write to
     * 
     * @throws Exception
     *             if there was a problem writing or encoding the object(s)
     */
    public void write(Object cereal, Writer writer) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().newDocument();

        Node root = createNode(rootElementName, cereal, document);
        document.appendChild(root);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, prettyPrint ? "yes" : "no");

        DOMSource dom = new DOMSource(document);
        Result result = new StreamResult(writer);
        transformer.transform(dom, result);
    }

    /**
     * Recursive method for creating XML {@link Node}s for each cereal-compatible object.
     * 
     * @param name
     *            the name of the object (used as the tag)
     * @param cereal
     *            the cereal-compatible object
     * @param document
     *            the document the {@link Node} should be created in
     * 
     * @return the created {@link Node}
     */
    @SuppressWarnings("unchecked")
    private Node createNode(String name, Object cereal, Document document) {
        if (name.equals("--class")) {
            // making the name a valid xml name
            name = "__class";
        }
        Element node = document.createElement(name);

        if (cereal == null) {
            Text text = document.createTextNode("null");
            node.appendChild(text);
        } else if (cereal instanceof String) {
            node.setAttribute(TYPE_KEY, TYPE_STRING); // for strings made up of numbers
            Text text = document.createTextNode((String) cereal);
            node.appendChild(text);
        } else if (cereal instanceof Number) {
            Text text = document.createTextNode(String.valueOf(cereal));
            node.appendChild(text);
        } else if (cereal instanceof Boolean) {
            Text text = document.createTextNode(((Boolean) cereal).toString());
            node.appendChild(text);
        } else if (cereal instanceof List) {
            List<Object> list = (List<Object>) cereal;
            node.setAttribute(TYPE_KEY, TYPE_LIST); // to differentiate between a list and an object

            for (Object value : list) {
                node.appendChild(createNode("value", value, document));
            }
        } else if (cereal instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) cereal;

            for (String key : map.keySet()) {
                Object value = map.get(key);
                node.appendChild(createNode(key, value, document));
            }
        }

        return node;
    }

    /**
     * Read cereal-compatible object(s) from the given input stream.
     * 
     * @param inputStream
     *            the stream to read from
     * 
     * @return the cereal-compatible object
     * 
     * @throws Exception
     *             if there was a problem reading from the given stream or if the contents were not
     *             properly formatted
     */
    public Object read(InputStream inputStream) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.parse(inputStream);
        Element root = document.getDocumentElement();

        return readNode(root);
    }

    /**
     * Recursive method for reading each {@link Element} (node) within the XML document structure.
     * 
     * @param element
     *            the element to read
     * @return the cereal-compatible object for that element (and possibly it's child elements)
     * 
     * @throws Exception
     *             if there was a problem traversing the DOM or the XML is not properly formatted
     */
    private Object readNode(Element element) throws Exception {
        List<Node> children = getChildNodes(element);
        if (children.size() == 0 && TYPE_STRING.equals(element.getAttribute(TYPE_KEY))) {
            return "";
        } else if (children.size() == 1 && children.get(0).getNodeType() == Node.TEXT_NODE) {
            String text = ((Text) children.get(0)).getTextContent();
            String lcText = text.toLowerCase();

            if (TYPE_STRING.equals(element.getAttribute(TYPE_KEY))) {
                return text;
            } else if (lcText.equals("null")) {
                return null;
            } else if (lcText.equals("false")) {
                return false;
            } else if (lcText.equals("true")) {
                return true;
            } else {
                try {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    return numberFormat.parse(text);
                } catch (ParseException pex) {
                    return text;
                }
            }
        } else if (isList(element, children)) {
            List<Object> list = new ArrayList<Object>(children.size());
            for (Node child : children) {
                list.add(readNode((Element) child));
            }
            return list;
        } else {
            Map<String, Object> map = new HashMap<String, Object>(children.size());
            for (Node child : children) {
                Element e = (Element) child;
                String name = e.getNodeName();
                if (name.equals("__class")) {
                    name = "--class";
                }
                Object value = readNode(e);

                map.put(name, value);
            }
            return map;
        }

    }

    /**
     * Helper method to get a list of only {@link Text} and {@link Element} typed {@link Node}s.
     * This is partially to workaround the difficulty of working with the {@link NodeList} object.
     * 
     * @param node
     *            the node whose children to get
     * 
     * @return the filtered list of child nodes
     */
    private static List<Node> getChildNodes(Node node) {
        List<Node> children = new ArrayList<Node>();
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            short type = child.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                children.add(child);
            } else if (type == Node.TEXT_NODE) {
                String text = ((Text) child).getTextContent().trim();
                if (text.length() > 0) {
                    children.add(child);
                }
            }
        }
        return children;
    }

    /**
     * Deteremines if a given element is a list or object based upon the current XML structure. This
     * follows the rules defined on the {@link XmlCerealEngine}.
     * 
     * @param element
     *            the element to determine if it is a list or not
     * @param children
     *            the children of that element
     * 
     * @return <code>true</code> if it is a list or <code>false</code> if it is an object
     * 
     * @throws CerealException
     *             if there is a {@link Text} node in the list of children. This would indicate that
     *             this has a "mixed" type definition which is not allowed.
     */
    private static boolean isList(Element element, List<Node> children) throws CerealException {
        /* First verify that everything is an element */
        for (Node child : children) {
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                throw new CerealException(
                        "Failed to parse because a object or list contained mixed types (not only elements) "
                                + child.getNodeName());
            }
        }

        if (TYPE_LIST.equals(element.getAttribute(TYPE_KEY))) {
            return true;
        } else if (children.size() < 2) {
            // assume object
            return false;
        }

        // if the first two tagged names are the same, it is a list
        String firstName = ((Element) children.get(0)).getTagName();
        String secondName = ((Element) children.get(1)).getTagName();
        return firstName.equals(secondName);
    }
}
