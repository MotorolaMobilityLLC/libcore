/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
/*
 * $Id: XMLErrorResources_es.java 468653 2006-10-28 07:07:05Z minchau $
 */
package org.apache.xml.res;


import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And you need
 * to enter key, value pair as part of the contents
 * array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */
public class XMLErrorResources_es extends ListResourceBundle
{

/*
 * This file contains error and warning messages related to Xalan Error
 * Handling.
 *
 *  General notes to translators:
 *
 *  1) Xalan (or more properly, Xalan-interpretive) and XSLTC are names of
 *     components.
 *     XSLT is an acronym for "XML Stylesheet Language: Transformations".
 *     XSLTC is an acronym for XSLT Compiler.
 *
 *  2) A stylesheet is a description of how to transform an input XML document
 *     into a resultant XML document (or HTML document or text).  The
 *     stylesheet itself is described in the form of an XML document.
 *
 *  3) A template is a component of a stylesheet that is used to match a
 *     particular portion of an input document and specifies the form of the
 *     corresponding portion of the output document.
 *
 *  4) An element is a mark-up tag in an XML document; an attribute is a
 *     modifier on the tag.  For example, in <elem attr='val' attr2='val2'>
 *     "elem" is an element name, "attr" and "attr2" are attribute names with
 *     the values "val" and "val2", respectively.
 *
 *  5) A namespace declaration is a special attribute that is used to associate
 *     a prefix with a URI (the namespace).  The meanings of element names and
 *     attribute names that use that prefix are defined with respect to that
 *     namespace.
 *
 *  6) "Translet" is an invented term that describes the class file that
 *     results from compiling an XML stylesheet into a Java class.
 *
 *  7) XPath is a specification that describes a notation for identifying
 *     nodes in a tree-structured representation of an XML document.  An
 *     instance of that notation is referred to as an XPath expression.
 *
 */

  /*
   * Message keys
   */
  public static final String ER_FUNCTION_NOT_SUPPORTED = "ER_FUNCTION_NOT_SUPPORTED";
  public static final String ER_CANNOT_OVERWRITE_CAUSE = "ER_CANNOT_OVERWRITE_CAUSE";
  public static final String ER_NO_DEFAULT_IMPL = "ER_NO_DEFAULT_IMPL";
  public static final String ER_CHUNKEDINTARRAY_NOT_SUPPORTED = "ER_CHUNKEDINTARRAY_NOT_SUPPORTED";
  public static final String ER_OFFSET_BIGGER_THAN_SLOT = "ER_OFFSET_BIGGER_THAN_SLOT";
  public static final String ER_COROUTINE_NOT_AVAIL = "ER_COROUTINE_NOT_AVAIL";
  public static final String ER_COROUTINE_CO_EXIT = "ER_COROUTINE_CO_EXIT";
  public static final String ER_COJOINROUTINESET_FAILED = "ER_COJOINROUTINESET_FAILED";
  public static final String ER_COROUTINE_PARAM = "ER_COROUTINE_PARAM";
  public static final String ER_PARSER_DOTERMINATE_ANSWERS = "ER_PARSER_DOTERMINATE_ANSWERS";
  public static final String ER_NO_PARSE_CALL_WHILE_PARSING = "ER_NO_PARSE_CALL_WHILE_PARSING";
  public static final String ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = "ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED";
  public static final String ER_ITERATOR_AXIS_NOT_IMPLEMENTED = "ER_ITERATOR_AXIS_NOT_IMPLEMENTED";
  public static final String ER_ITERATOR_CLONE_NOT_SUPPORTED = "ER_ITERATOR_CLONE_NOT_SUPPORTED";
  public static final String ER_UNKNOWN_AXIS_TYPE = "ER_UNKNOWN_AXIS_TYPE";
  public static final String ER_AXIS_NOT_SUPPORTED = "ER_AXIS_NOT_SUPPORTED";
  public static final String ER_NO_DTMIDS_AVAIL = "ER_NO_DTMIDS_AVAIL";
  public static final String ER_NOT_SUPPORTED = "ER_NOT_SUPPORTED";
  public static final String ER_NODE_NON_NULL = "ER_NODE_NON_NULL";
  public static final String ER_COULD_NOT_RESOLVE_NODE = "ER_COULD_NOT_RESOLVE_NODE";
  public static final String ER_STARTPARSE_WHILE_PARSING = "ER_STARTPARSE_WHILE_PARSING";
  public static final String ER_STARTPARSE_NEEDS_SAXPARSER = "ER_STARTPARSE_NEEDS_SAXPARSER";
  public static final String ER_COULD_NOT_INIT_PARSER = "ER_COULD_NOT_INIT_PARSER";
  public static final String ER_EXCEPTION_CREATING_POOL = "ER_EXCEPTION_CREATING_POOL";
  public static final String ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE";
  public static final String ER_SCHEME_REQUIRED = "ER_SCHEME_REQUIRED";
  public static final String ER_NO_SCHEME_IN_URI = "ER_NO_SCHEME_IN_URI";
  public static final String ER_NO_SCHEME_INURI = "ER_NO_SCHEME_INURI";
  public static final String ER_PATH_INVALID_CHAR = "ER_PATH_INVALID_CHAR";
  public static final String ER_SCHEME_FROM_NULL_STRING = "ER_SCHEME_FROM_NULL_STRING";
  public static final String ER_SCHEME_NOT_CONFORMANT = "ER_SCHEME_NOT_CONFORMANT";
  public static final String ER_HOST_ADDRESS_NOT_WELLFORMED = "ER_HOST_ADDRESS_NOT_WELLFORMED";
  public static final String ER_PORT_WHEN_HOST_NULL = "ER_PORT_WHEN_HOST_NULL";
  public static final String ER_INVALID_PORT = "ER_INVALID_PORT";
  public static final String ER_FRAG_FOR_GENERIC_URI ="ER_FRAG_FOR_GENERIC_URI";
  public static final String ER_FRAG_WHEN_PATH_NULL = "ER_FRAG_WHEN_PATH_NULL";
  public static final String ER_FRAG_INVALID_CHAR = "ER_FRAG_INVALID_CHAR";
  public static final String ER_PARSER_IN_USE = "ER_PARSER_IN_USE";
  public static final String ER_CANNOT_CHANGE_WHILE_PARSING = "ER_CANNOT_CHANGE_WHILE_PARSING";
  public static final String ER_SELF_CAUSATION_NOT_PERMITTED = "ER_SELF_CAUSATION_NOT_PERMITTED";
  public static final String ER_NO_USERINFO_IF_NO_HOST = "ER_NO_USERINFO_IF_NO_HOST";
  public static final String ER_NO_PORT_IF_NO_HOST = "ER_NO_PORT_IF_NO_HOST";
  public static final String ER_NO_QUERY_STRING_IN_PATH = "ER_NO_QUERY_STRING_IN_PATH";
  public static final String ER_NO_FRAGMENT_STRING_IN_PATH = "ER_NO_FRAGMENT_STRING_IN_PATH";
  public static final String ER_CANNOT_INIT_URI_EMPTY_PARMS = "ER_CANNOT_INIT_URI_EMPTY_PARMS";
  public static final String ER_METHOD_NOT_SUPPORTED ="ER_METHOD_NOT_SUPPORTED";
  public static final String ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = "ER_INCRSAXSRCFILTER_NOT_RESTARTABLE";
  public static final String ER_XMLRDR_NOT_BEFORE_STARTPARSE = "ER_XMLRDR_NOT_BEFORE_STARTPARSE";
  public static final String ER_AXIS_TRAVERSER_NOT_SUPPORTED = "ER_AXIS_TRAVERSER_NOT_SUPPORTED";
  public static final String ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = "ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER";
  public static final String ER_SYSTEMID_UNKNOWN = "ER_SYSTEMID_UNKNOWN";
  public static final String ER_LOCATION_UNKNOWN = "ER_LOCATION_UNKNOWN";
  public static final String ER_PREFIX_MUST_RESOLVE = "ER_PREFIX_MUST_RESOLVE";
  public static final String ER_CREATEDOCUMENT_NOT_SUPPORTED = "ER_CREATEDOCUMENT_NOT_SUPPORTED";
  public static final String ER_CHILD_HAS_NO_OWNER_DOCUMENT = "ER_CHILD_HAS_NO_OWNER_DOCUMENT";
  public static final String ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = "ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT";
  public static final String ER_CANT_OUTPUT_TEXT_BEFORE_DOC = "ER_CANT_OUTPUT_TEXT_BEFORE_DOC";
  public static final String ER_CANT_HAVE_MORE_THAN_ONE_ROOT = "ER_CANT_HAVE_MORE_THAN_ONE_ROOT";
  public static final String ER_ARG_LOCALNAME_NULL = "ER_ARG_LOCALNAME_NULL";
  public static final String ER_ARG_LOCALNAME_INVALID = "ER_ARG_LOCALNAME_INVALID";
  public static final String ER_ARG_PREFIX_INVALID = "ER_ARG_PREFIX_INVALID";
  public static final String ER_NAME_CANT_START_WITH_COLON = "ER_NAME_CANT_START_WITH_COLON";

  /*
   * Now fill in the message text.
   * Then fill in the message text for that message code in the
   * array. Use the new error code as the index into the array.
   */

  // Error messages...

  /**
   * Get the lookup table for error messages
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return new Object[][] {

  /** Error message ID that has a null message, but takes in a single object.    */
    {"ER0000" , "{0}" },

    { ER_FUNCTION_NOT_SUPPORTED,
      "\u00a1Funci\u00f3n no soportada!"},

    { ER_CANNOT_OVERWRITE_CAUSE,
      "No se puede escribir encima de la causa"},

    { ER_NO_DEFAULT_IMPL,
      "No se ha encontrado una implementaci\u00f3n predeterminada "},

    { ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
      "ChunkedIntArray({0}) no soportada actualmente"},

    { ER_OFFSET_BIGGER_THAN_SLOT,
      "El desplazamiento es mayor que el espacio"},

    { ER_COROUTINE_NOT_AVAIL,
      "Corrutina no disponible, id={0}"},

    { ER_COROUTINE_CO_EXIT,
      "CoroutineManager ha recibido una petici\u00f3n co_exit()"},

    { ER_COJOINROUTINESET_FAILED,
      "Anomal\u00eda de co_joinCoroutineSet()"},

    { ER_COROUTINE_PARAM,
      "Error del par\u00e1metro de corrutina ({0})"},

    { ER_PARSER_DOTERMINATE_ANSWERS,
      "\nINESPERADO: Respuestas doTerminate del analizador {0}"},

    { ER_NO_PARSE_CALL_WHILE_PARSING,
      "No se puede llamar a parse mientras se est\u00e1 analizando"},

    { ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Error: El iterador escrito para el eje {0} no est\u00e1 implementado"},

    { ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Error: El iterador para el eje {0} no est\u00e1 implementado "},

    { ER_ITERATOR_CLONE_NOT_SUPPORTED,
      "La r\u00e9plica del iterador no est\u00e1 soportada"},

    { ER_UNKNOWN_AXIS_TYPE,
      "Tipo de cruce de eje desconocido: {0}"},

    { ER_AXIS_NOT_SUPPORTED,
      "Cruzador de eje no soportado: {0}"},

    { ER_NO_DTMIDS_AVAIL,
      "No hay m\u00e1s ID de DTM disponibles"},

    { ER_NOT_SUPPORTED,
      "No soportado: {0}"},

    { ER_NODE_NON_NULL,
      "El nodo no debe ser nulo para getDTMHandleFromNode"},

    { ER_COULD_NOT_RESOLVE_NODE,
      "No se puede resolver el nodo como un manejador"},

    { ER_STARTPARSE_WHILE_PARSING,
       "No se puede llamar a startParse mientras se est\u00e1 analizando"},

    { ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse necesita un SAXParser no nulo"},

    { ER_COULD_NOT_INIT_PARSER,
       "No se ha podido inicializar el analizador con"},

    { ER_EXCEPTION_CREATING_POOL,
       "Se ha producido una excepci\u00f3n al crear la nueva instancia de la agrupaci\u00f3n"},

    { ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "La v\u00eda de acceso contiene una secuencia de escape no v\u00e1lida"},

    { ER_SCHEME_REQUIRED,
       "\u00a1Se necesita un esquema!"},

    { ER_NO_SCHEME_IN_URI,
       "No se ha encontrado un esquema en el URI: {0}"},

    { ER_NO_SCHEME_INURI,
       "No se ha encontrado un esquema en el URI"},

    { ER_PATH_INVALID_CHAR,
       "La v\u00eda de acceso contiene un car\u00e1cter no v\u00e1lido: {0}"},

    { ER_SCHEME_FROM_NULL_STRING,
       "No se puede establecer un esquema de una serie nula"},

    { ER_SCHEME_NOT_CONFORMANT,
       "El esquema no es compatible."},

    { ER_HOST_ADDRESS_NOT_WELLFORMED,
       "El sistema principal no es una direcci\u00f3n bien formada"},

    { ER_PORT_WHEN_HOST_NULL,
       "No se puede establecer el puerto si el sistema principal es nulo"},

    { ER_INVALID_PORT,
       "N\u00famero de puerto no v\u00e1lido"},

    { ER_FRAG_FOR_GENERIC_URI,
       "S\u00f3lo se puede establecer el fragmento para un URI gen\u00e9rico"},

    { ER_FRAG_WHEN_PATH_NULL,
       "No se puede establecer el fragmento si la v\u00eda de acceso es nula"},

    { ER_FRAG_INVALID_CHAR,
       "El fragmento contiene un car\u00e1cter no v\u00e1lido"},

    { ER_PARSER_IN_USE,
      "El analizador ya est\u00e1 en uso"},

    { ER_CANNOT_CHANGE_WHILE_PARSING,
      "No se puede cambiar {0} {1} mientras se analiza"},

    { ER_SELF_CAUSATION_NOT_PERMITTED,
      "Autocausalidad no permitida"},

    { ER_NO_USERINFO_IF_NO_HOST,
      "No se puede especificar la informaci\u00f3n de usuario si no se ha especificado el sistema principal"},

    { ER_NO_PORT_IF_NO_HOST,
      "No se puede especificar el puerto si no se ha especificado el sistema principal"},

    { ER_NO_QUERY_STRING_IN_PATH,
      "No se puede especificar la serie de consulta en la v\u00eda de acceso y en la serie de consulta"},

    { ER_NO_FRAGMENT_STRING_IN_PATH,
      "No se puede especificar el fragmento en la v\u00eda de acceso y en el fragmento"},

    { ER_CANNOT_INIT_URI_EMPTY_PARMS,
      "No se puede inicializar el URI con par\u00e1metros vac\u00edos"},

    { ER_METHOD_NOT_SUPPORTED,
      "El m\u00e9todo no est\u00e1 a\u00fan soportado "},

    { ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
      "IncrementalSAXSource_Filter no es actualmente reiniciable"},

    { ER_XMLRDR_NOT_BEFORE_STARTPARSE,
      "XMLReader no debe ir antes que la petici\u00f3n startParse"},

    { ER_AXIS_TRAVERSER_NOT_SUPPORTED,
      "Cruzador de eje no soportado: {0}"},

    { ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
      "\u00a1Se ha creado ListingErrorHandler con PrintWriter nulo!"},

    { ER_SYSTEMID_UNKNOWN,
      "SystemId desconocido"},

    { ER_LOCATION_UNKNOWN,
      "Ubicaci\u00f3n del error desconocida"},

    { ER_PREFIX_MUST_RESOLVE,
      "El prefijo debe resolverse como un espacio de nombres: {0}"},

    { ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "\u00a1createDocument() no soportada en XPathContext!"},

    { ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u00a1El hijo atributo no tiene un documento propietario!"},

    { ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u00a1El hijo atributo no tiene un elemento documento propietario!"},

    { ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u00a1Aviso: no puede haber salida de texto antes del elemento documento!  Ignorando..."},

    { ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "\u00a1No puede haber m\u00e1s de una ra\u00edz en DOM!"},

    { ER_ARG_LOCALNAME_NULL,
       "El argumento 'localName' es nulo"},

    // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
    // The localname is the portion after the optional colon; the message indicates
    // that there is a problem with that part of the QNAME.
    { ER_ARG_LOCALNAME_INVALID,
       "Localname en QNAME debe ser un NCName v\u00e1lido"},

    // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
    // The prefix is the portion before the optional colon; the message indicates
    // that there is a problem with that part of the QNAME.
    { ER_ARG_PREFIX_INVALID,
       "El prefijo en QNAME debe ser un NCName v\u00e1lido"},

    { ER_NAME_CANT_START_WITH_COLON,
      "El nombre no puede empezar con dos puntos"},

    { "BAD_CODE", "El par\u00e1metro para createMessage estaba fuera de los l\u00edmites"},
    { "FORMAT_FAILED", "Se ha generado una excepci\u00f3n durante la llamada messageFormat"},
    { "line", "L\u00ednea n\u00fam."},
    { "column","Columna n\u00fam."}


  };
  }

  /**
   *   Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   *   of ResourceBundle.getBundle().
   *
   *   @param className the name of the class that implements the resource bundle.
   *   @return the ResourceBundle
   *   @throws MissingResourceException
   */
  public static final XMLErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return (XMLErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XMLErrorResources) ResourceBundle.getBundle(className,
                new Locale("es", "ES"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
      }
    }
  }

  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */
  private static final String getResourceSuffix(Locale locale)
  {

    String suffix = "_" + locale.getLanguage();
    String country = locale.getCountry();

    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }

}
