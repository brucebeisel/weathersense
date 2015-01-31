/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.util;

import java.io.Writer;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class XMLUtils
{
    public static Node findTextNode(Element e)
    {
	//
	// As far as I can tell. A regular TEXT node will be an only child. A CDATA node
	// will have two siblings that contain no data. Since we don't know if we are looking
	// for a regular TEXT or a CDATA node we have to check for both.
	//
	NodeList nodes = e.getChildNodes();

	//
	// Single child node. This indicates it is not a CDATA node
	//
	if (nodes.getLength() == 1)
	    return nodes.item(0);


	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);

	    if (node.getNodeType() == Node.CDATA_SECTION_NODE)
		return node;
	}

	System.err.println("Could not figure out where the data for a TEXT node is");

	return null;
    }

    /**
     * Print the XML document
     *
     * @param doc  The XML document
     * @param ps  The writer to where the XML document is written
     */
    public static void printDocument(Document doc, Writer ps)
    {
	try {
	    String lineSeparator = System.getProperty("line.separator");

	    ps.write("<?xml version=\"1.0\"?>");
	    ps.write(lineSeparator);
	    ps.write(lineSeparator);

	    Element node = doc.getDocumentElement();

	    printTree(ps, node, "", lineSeparator);
	}
	catch (IOException e) {
	}
    }

    /**
     * Print the tree of a node. This method is recursive.
     *
     * @param ps  The writer to where the XML document is written
     * @param node The node with which to start the printing
     * @param indent A string that is printed on each line before any XML
     * @param lineSeparator The string written at the end of each line
     */
    private static void printTree(Writer ps, Node node, String indent, String lineSeparator) throws IOException
    {
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:

                NodeList nodes = node.getChildNodes();

                if (nodes != null) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        printTree(ps, nodes.item(i), "", lineSeparator);
		    }
                }
                break;

            case Node.ELEMENT_NODE:
	        //
                // Print element and atributes
		//
                String name = node.getNodeName();
                ps.write(indent);
		ps.write("<");
		ps.write(name);
                
		//
                // Print attributes
		//
                NamedNodeMap attributes = node.getAttributes();

                for (int i = 0; i < attributes.getLength(); i++) {
                    Node current = attributes.item(i);
                    ps.write(" ");
		    ps.write(current.getNodeName());
		    ps.write("=\"");
		    ps.write(current.getNodeValue());
		    ps.write("\"");
                }
                
		//
                // Recurse on each child
		//
                NodeList children = node.getChildNodes();

                if (children != null) {
		    ps.write(">");
		    ps.write(lineSeparator);

                    for (int i = 0; i < children.getLength(); i++)
                        printTree(ps, children.item(i), indent + "  ", lineSeparator);

		    ps.write(indent);
		    ps.write("</");
		    ps.write(name);
		    ps.write(">");
		    ps.write(lineSeparator);
                }
		else {
		    ps.write("/>");
    		    ps.write(lineSeparator);
		}
                
                
                break;
                
            case Node.CDATA_SECTION_NODE:
		ps.write(indent);
    		ps.write("<![CDATA[");
		ps.write(node.getNodeValue());
		ps.write("]]>");
		ps.write(lineSeparator);
		break;

            case Node.TEXT_NODE:
	        String text = node.getNodeValue().trim();

		if (text.length() > 0) {
		    ps.write(indent);
		    ps.write(text);
		    ps.write(lineSeparator);
		}
                break;
                

            case Node.PROCESSING_INSTRUCTION_NODE:
                break;
                
            case Node.ENTITY_REFERENCE_NODE:
                break;
                
            case Node.DOCUMENT_TYPE_NODE:
                break;
                
            default:
                break;
        }
    } 
}
