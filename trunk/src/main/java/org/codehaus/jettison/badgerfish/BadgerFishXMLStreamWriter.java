/**
 * Copyright 2006 Envoi Solutions LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.jettison.badgerfish;

import java.io.IOException;
import java.io.Writer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.XsonNamespaceContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.util.FastStack;

public class BadgerFishXMLStreamWriter extends AbstractXMLStreamWriter {
    JSONObject root;
    JSONObject currentNode;
    Writer writer;
    FastStack nodes = new FastStack();
    String currentKey;
    int depth = 0;
    NamespaceContext ctx = new XsonNamespaceContext(nodes);
    
    public BadgerFishXMLStreamWriter(Writer writer) {
        super();
        currentNode = new JSONObject();
        root = currentNode;
        this.writer = writer;
    }

    public void close() throws XMLStreamException {
    }

    public void flush() throws XMLStreamException {

    }

    public NamespaceContext getNamespaceContext() {
        return ctx;
    }

    public String getPrefix(String ns) throws XMLStreamException {
        return getNamespaceContext().getPrefix(ns);
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        return null;
    }

    public void setDefaultNamespace(String arg0) throws XMLStreamException {
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.ctx = context;
    }

    public void setPrefix(String arg0, String arg1) throws XMLStreamException {
        
    }

    public void writeAttribute(String p, String ns, String local, String value) throws XMLStreamException {
        String key = createAttributeKey(p, ns, local);
        try {
            currentNode.put(key, value);
        } catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    private String createAttributeKey(String p, String ns, String local) {
        return "@" + createKey(p, ns, local);
    }
    
    private String createKey(String p, String ns, String local) {
        if (p == null || p.equals("")) {
            return local;
        }
        
        return p + ":" + local;
    }

    public void writeAttribute(String ns, String local, String value) throws XMLStreamException {
        writeAttribute(null, ns, local, value);
    }

    public void writeAttribute(String local, String value) throws XMLStreamException {
        writeAttribute(null, local, value);
    }

    public void writeCharacters(String text) throws XMLStreamException {
        try {
            Object o = currentNode.opt("$");
            if (o instanceof JSONArray) {
                ((JSONArray) o).put(text);
            } else if (o instanceof String) {
                JSONArray arr = new JSONArray();
                arr.put(o);
                arr.put(text);
                currentNode.put("$", arr);
            } else {
                currentNode.put("$", text);
            }
        } catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeDefaultNamespace(String ns) throws XMLStreamException {
        writeNamespace("", ns);
    }

    public void writeEndElement() throws XMLStreamException {
        if (nodes.size() > 1) {
            nodes.pop();
            currentNode = ((Node) nodes.peek()).getObject();
        }
        depth--;
    }

    public void writeEntityRef(String arg0) throws XMLStreamException {
        // TODO Auto-generated method stub

    }

    public void writeNamespace(String prefix, String ns) throws XMLStreamException {
        ((Node) nodes.peek()).setNamespace(prefix, ns);
        try {
            JSONObject nsObj = currentNode.optJSONObject("@xmlns");
            if (nsObj == null) {
                nsObj = new JSONObject();
                currentNode.put("@xmlns", nsObj);
            }
            if (prefix.equals("")) {
                prefix = "$";
            }
            nsObj.put(prefix, ns);
        } catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
        // TODO Auto-generated method stub

    }

    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
        // TODO Auto-generated method stub

    }

    public void writeStartDocument() throws XMLStreamException {
    }

    
    public void writeEndDocument() throws XMLStreamException {
        try {
            root.write(writer);
            writer.flush();
        } catch (JSONException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void writeStartElement(String prefix, String local, String ns) throws XMLStreamException {
        depth++;
        
        try {

            // TODO ns
            currentKey = createKey(prefix, ns, local);
            
            Object existing = currentNode.opt(currentKey);
            if (existing instanceof JSONObject) {
                JSONArray array = new JSONArray();
                array.put(existing);
                
                JSONObject newCurrent = new JSONObject();
                array.put(newCurrent);
                
                currentNode.put(currentKey, array);
                
                currentNode = newCurrent;
                Node node = new Node(currentNode);
                nodes.push(node);
            } else {
                JSONObject newCurrent = new JSONObject();
                
                if (existing instanceof JSONArray) {
                    ((JSONArray) existing).put(newCurrent);
                } else {
                    currentNode.put(currentKey, newCurrent);
                }
                
                currentNode = newCurrent;
                Node node = new Node(currentNode);
                nodes.push(node);
            }
        } catch (JSONException e) {
            throw new XMLStreamException("Could not write start element!", e);
        }
    }
}
