/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package org.xmlpull.v1;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create implementations of XML Pull Parser defined in XMPULL V1 API.
 *
 * @see XmlPullParser
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 * @author Stefan Haustein
 */

public class XmlPullParserFactory {
    // TODO: Deprecate or remove these fields. They're currently unused
    // but are public APIs.

    /** Currently unused. */
    public static final String PROPERTY_NAME =
        "org.xmlpull.v1.XmlPullParserFactory";
    /** Currently unused */
    protected String classNamesLocation = null;
    /** Currently unused */
    protected ArrayList parserClasses = null;
    /** Currently unused */
    protected ArrayList serializerClasses = null;

    // features are kept there
    // TODO: This can't be made final because it's a public API.
    protected HashMap<String, Boolean> features = new HashMap<String, Boolean>();

    /**
     * Protected constructor to be called by factory implementations.
     */

    protected XmlPullParserFactory() {
    }



    /**
     * Set the features to be set when XML Pull Parser is created by this factory.
     * <p><b>NOTE:</b> factory features are not used for XML Serializer.
     *
     * @param name string with URI identifying feature
     * @param state if true feature will be set; if false will be ignored
     */

    public void setFeature(String name, boolean state) throws XmlPullParserException {
        features.put(name, state);
    }


    /**
     * Return the current value of the feature with given name.
     * <p><b>NOTE:</b> factory features are not used for XML Serializer.
     *
     * @param name The name of feature to be retrieved.
     * @return The value of named feature.
     *     Unknown features are <string>always</strong> returned as false
     */

    public boolean getFeature (String name) {
        Boolean value = features.get(name);
        return value != null ? value.booleanValue() : false;
    }

    /**
     * Specifies that the parser produced by this factory will provide
     * support for XML namespaces.
     * By default the value of this is set to false.
     *
     * @param awareness true if the parser produced by this code
     *    will provide support for XML namespaces;  false otherwise.
     */

    public void setNamespaceAware(boolean awareness) {
        features.put (XmlPullParser.FEATURE_PROCESS_NAMESPACES, awareness);
    }

    /**
     * Indicates whether or not the factory is configured to produce
     * parsers which are namespace aware
     * (it simply set feature XmlPullParser.FEATURE_PROCESS_NAMESPACES to true or false).
     *
     * @return  true if the factory is configured to produce parsers
     *    which are namespace aware; false otherwise.
     */

    public boolean isNamespaceAware() {
        return getFeature (XmlPullParser.FEATURE_PROCESS_NAMESPACES);
    }


    /**
     * Specifies that the parser produced by this factory will be validating
     * (it simply set feature XmlPullParser.FEATURE_VALIDATION to true or false).
     *
     * By default the value of this is set to false.
     *
     * @param validating - if true the parsers created by this factory  must be validating.
     */

    public void setValidating(boolean validating) {
        features.put (XmlPullParser.FEATURE_VALIDATION, validating);
    }

    /**
     * Indicates whether or not the factory is configured to produce parsers
     * which validate the XML content during parse.
     *
     * @return   true if the factory is configured to produce parsers
     * which validate the XML content during parse; false otherwise.
     */

    public boolean isValidating() {
        return getFeature (XmlPullParser.FEATURE_VALIDATION);
    }

    /**
     * Creates a new instance of a XML Pull Parser
     * using the currently configured factory features.
     *
     * @return A new instance of a XML Pull Parser.
     */

    public XmlPullParser newPullParser() throws XmlPullParserException {
        final XmlPullParser pp = new KXmlParser();
        for (Map.Entry<String, Boolean> entry : features.entrySet()) {
            pp.setFeature(entry.getKey(), entry.getValue());
        }

        return pp;
    }


    /**
     * Creates a new instance of a XML Serializer.
     *
     * <p><b>NOTE:</b> factory features are not used for XML Serializer.
     *
     * @return A new instance of a XML Serializer.
     * @throws XmlPullParserException if a parser cannot be created which satisfies the
     * requested configuration.
     */

    public XmlSerializer newSerializer() throws XmlPullParserException {
        return new KXmlSerializer();
    }

    /**
     * Creates a new instance of a PullParserFactory that can be used
     * to create XML pull parsers. The factory will always return instances
     * of {@link KXmlParser} and {@link KXmlSerializer}.
     */
    public static XmlPullParserFactory newInstance () throws XmlPullParserException {
        return new XmlPullParserFactory();
    }

    /**
     * Creates a factory that always returns instances of of {@link KXmlParser} and
     * {@link KXmlSerializer}. This <b>does not</b> support factories capable of
     * creating arbitrary parser and serializer implementations. Both arguments to this
     * method are unused.
     */
    public static XmlPullParserFactory newInstance (String unused, Class unused2)
        throws XmlPullParserException {
        return newInstance();
    }
}
