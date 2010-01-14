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
package org.codehaus.jettison.mapped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {
    /* Were there a constants class, this key would live there. */
    private static final String JETTISON_TYPE_CONVERTER_CLASS_KEY = "jettison.mapped.typeconverter.class";

    /* Mostly exists to wrap exception handling for reflective invocations. */
    private static class ConverterFactory {
        TypeConverter newDefaultConverterInstance() {
            return new DefaultConverter();
        }
    }

    private static final ConverterFactory converterFactory;

    static {
        ConverterFactory cf = null;
		String userSpecifiedClass = System.getProperty( JETTISON_TYPE_CONVERTER_CLASS_KEY );
		if( userSpecifiedClass != null && userSpecifiedClass.length() > 0 ) {
			try {
				final Class<? extends TypeConverter> tc =
                        Class.forName( userSpecifiedClass ).asSubclass( TypeConverter.class  );
                tc.newInstance(); /* Blow up as soon as possible. */
                cf = new ConverterFactory() {
                    public TypeConverter newDefaultConverterInstance() {
                        try {
                            return tc.newInstance();
                        }
                        catch ( Exception e ){
                            // Implementer of custom class would have to try pretty hard to make this happen,
                            // since we already created one instance.
                            throw new ExceptionInInitializerError( e );
                        }
                    }
                };
			}
			catch ( Exception e ){
                throw new ExceptionInInitializerError( e );
            }
        }
		if( cf == null ){
			cf = new ConverterFactory();
		}
        converterFactory = cf;
	}




    private Map xmlToJsonNamespaces;
    private List attributesAsElements;
    private List ignoredElements;
    private boolean supressAtAttributes; 
    private String attributeKey = "@";
    private boolean implicitCollections = false;
    private boolean ignoreNamespaces;
    
    private TypeConverter typeConverter = converterFactory.newDefaultConverterInstance();

    public Configuration() {
        super();
        this.xmlToJsonNamespaces = new HashMap();
    }
    
    public Configuration(Map xmlToJsonNamespaces) {
        super();
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }

    public Configuration(Map xmlToJsonNamespaces, List attributesAsElements, List ignoredElements) {
        super();
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
        this.attributesAsElements = attributesAsElements;
        this.ignoredElements = ignoredElements;
    }

    public boolean isIgnoreNamespaces() {
        return ignoreNamespaces;
   }   
   public void setIgnoreNamespaces( boolean ignoreNamespaces ) {    
       this.ignoreNamespaces = ignoreNamespaces;
   }
    public List getAttributesAsElements() {
        return attributesAsElements;
    }
    public void setAttributesAsElements(List attributesAsElements) {
        this.attributesAsElements = attributesAsElements;
    }
    public List getIgnoredElements() {
        return ignoredElements;
    }
    public void setIgnoredElements(List ignoredElements) {
        this.ignoredElements = ignoredElements;
    }
    public Map getXmlToJsonNamespaces() {
        return xmlToJsonNamespaces;
    }
    public void setXmlToJsonNamespaces(Map xmlToJsonNamespaces) {
        this.xmlToJsonNamespaces = xmlToJsonNamespaces;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

	public boolean isSupressAtAttributes() {
		return this.supressAtAttributes;
	}

	public void setSupressAtAttributes(boolean supressAtAttributes) {
		this.supressAtAttributes = supressAtAttributes;
	}
	
   public String getAttributeKey() {
        return this.attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

	public boolean isImplicitCollections() {
		return implicitCollections;
	}

	public void setImplicitCollections(boolean implicitCollections) {
		this.implicitCollections = implicitCollections;
	}

    static TypeConverter newDefaultConverterInstance() {
        return converterFactory.newDefaultConverterInstance();
    }

}
