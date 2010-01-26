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
 * $Id: XMLErrorResources_pt_BR.java 468653 2006-10-28 07:07:05Z minchau $
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
public class XMLErrorResources_pt_BR extends ListResourceBundle
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
      "Fun\u00e7\u00e3o n\u00e3o suportada!"},

    { ER_CANNOT_OVERWRITE_CAUSE,
      "Imposs\u00edvel sobrepor causa"},

    { ER_NO_DEFAULT_IMPL,
      "Nenhuma implementa\u00e7\u00e3o padr\u00e3o encontrada"},

    { ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
      "ChunkedIntArray({0}) n\u00e3o suportado atualmente"},

    { ER_OFFSET_BIGGER_THAN_SLOT,
      "Deslocamento maior que slot"},

    { ER_COROUTINE_NOT_AVAIL,
      "Co-rotina n\u00e3o dispon\u00edvel, id={0}"},

    { ER_COROUTINE_CO_EXIT,
      "CoroutineManager recebido para pedido co_exit()"},

    { ER_COJOINROUTINESET_FAILED,
      "Falha de co_joinCoroutineSet()"},

    { ER_COROUTINE_PARAM,
      "Erro de par\u00e2metro coroutine ({0})"},

    { ER_PARSER_DOTERMINATE_ANSWERS,
      "\nINESPERADO: doTerminate do analisador respondeu {0}"},

    { ER_NO_PARSE_CALL_WHILE_PARSING,
      "parse n\u00e3o pode ser chamado durante an\u00e1lise"},

    { ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Erro: digitado repetidor para eixo {0} n\u00e3o implementado"},

    { ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
      "Erro: repetidor para eixo {0} n\u00e3o implementado"},

    { ER_ITERATOR_CLONE_NOT_SUPPORTED,
      "Clone de repetidor n\u00e3o suportado"},

    { ER_UNKNOWN_AXIS_TYPE,
      "Tipo de passagem de eixo desconhecida: {0}"},

    { ER_AXIS_NOT_SUPPORTED,
      "Atravessador de eixo n\u00e3o suportado: {0}"},

    { ER_NO_DTMIDS_AVAIL,
      "N\u00e3o existem mais IDs de DTM dispon\u00edveis"},

    { ER_NOT_SUPPORTED,
      "N\u00e3o suportado: {0}"},

    { ER_NODE_NON_NULL,
      "O n\u00f3 n\u00e3o deve ser nulo para getDTMHandleFromNode"},

    { ER_COULD_NOT_RESOLVE_NODE,
      "N\u00e3o foi poss\u00edvel resolver o n\u00f3 para um identificador"},

    { ER_STARTPARSE_WHILE_PARSING,
       "startParse n\u00e3o pode ser chamado durante an\u00e1lise"},

    { ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse precisa de um SAXParser n\u00e3o-nulo"},

    { ER_COULD_NOT_INIT_PARSER,
       "n\u00e3o foi poss\u00edvel inicializar analisador com"},

    { ER_EXCEPTION_CREATING_POOL,
       "exce\u00e7\u00e3o ao criar nova inst\u00e2ncia para o conjunto"},

    { ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "O caminho cont\u00e9m seq\u00fc\u00eancia de escape inv\u00e1lida"},

    { ER_SCHEME_REQUIRED,
       "O esquema \u00e9 obrigat\u00f3rio!"},

    { ER_NO_SCHEME_IN_URI,
       "Nenhum esquema encontrado no URI: {0}"},

    { ER_NO_SCHEME_INURI,
       "Nenhum esquema encontrado no URI"},

    { ER_PATH_INVALID_CHAR,
       "O caminho cont\u00e9m caractere inv\u00e1lido: {0}"},

    { ER_SCHEME_FROM_NULL_STRING,
       "Imposs\u00edvel definir esquema a partir da cadeia nula"},

    { ER_SCHEME_NOT_CONFORMANT,
       "O esquema n\u00e3o est\u00e1 em conformidade."},

    { ER_HOST_ADDRESS_NOT_WELLFORMED,
       "O host n\u00e3o \u00e9 um endere\u00e7o formado corretamente"},

    { ER_PORT_WHEN_HOST_NULL,
       "A porta n\u00e3o pode ser definida quando o host \u00e9 nulo"},

    { ER_INVALID_PORT,
       "N\u00famero de porta inv\u00e1lido"},

    { ER_FRAG_FOR_GENERIC_URI,
       "O fragmento s\u00f3 pode ser definido para um URI gen\u00e9rico"},

    { ER_FRAG_WHEN_PATH_NULL,
       "O fragmento n\u00e3o pode ser definido quando o caminho \u00e9 nulo"},

    { ER_FRAG_INVALID_CHAR,
       "O fragmento cont\u00e9m caractere inv\u00e1lido"},

    { ER_PARSER_IN_USE,
      "O analisador j\u00e1 est\u00e1 sendo utilizado"},

    { ER_CANNOT_CHANGE_WHILE_PARSING,
      "Imposs\u00edvel alterar {0} {1} durante an\u00e1lise"},

    { ER_SELF_CAUSATION_NOT_PERMITTED,
      "Auto-causa\u00e7\u00e3o n\u00e3o permitida"},

    { ER_NO_USERINFO_IF_NO_HOST,
      "Userinfo n\u00e3o pode ser especificado se host n\u00e3o for especificado"},

    { ER_NO_PORT_IF_NO_HOST,
      "Port n\u00e3o pode ser especificado se host n\u00e3o for especificado"},

    { ER_NO_QUERY_STRING_IN_PATH,
      "A cadeia de consulta n\u00e3o pode ser especificada na cadeia de consulta e caminho"},

    { ER_NO_FRAGMENT_STRING_IN_PATH,
      "O fragmento n\u00e3o pode ser especificado no caminho e fragmento"},

    { ER_CANNOT_INIT_URI_EMPTY_PARMS,
      "Imposs\u00edvel inicializar URI com par\u00e2metros vazios"},

    { ER_METHOD_NOT_SUPPORTED,
      "M\u00e9todo ainda n\u00e3o suportado"},

    { ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
      "IncrementalSAXSource_Filter atualmente n\u00e3o reinicializ\u00e1vel"},

    { ER_XMLRDR_NOT_BEFORE_STARTPARSE,
      "XMLReader n\u00e3o antes do pedido startParse"},

    { ER_AXIS_TRAVERSER_NOT_SUPPORTED,
      "Atravessador de eixo n\u00e3o suportado: {0}"},

    { ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
      "ListingErrorHandler criado com nulo PrintWriter!"},

    { ER_SYSTEMID_UNKNOWN,
      "SystemId Desconhecido"},

    { ER_LOCATION_UNKNOWN,
      "Localiza\u00e7\u00e3o de erro desconhecido"},

    { ER_PREFIX_MUST_RESOLVE,
      "O prefixo deve ser resolvido para um espa\u00e7o de nomes: {0}"},

    { ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() n\u00e3o suportado em XPathContext!"},

    { ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "O atributo child n\u00e3o possui um documento do propriet\u00e1rio!"},

    { ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "O atributo child n\u00e3o possui um elemento de documento do propriet\u00e1rio!"},

    { ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "Aviso: imposs\u00edvel emitir texto antes do elemento document! Ignorando..."},

    { ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "Imposs\u00edvel ter mais de uma raiz em um DOM!"},

    { ER_ARG_LOCALNAME_NULL,
       "O argumento 'localName' \u00e9 nulo"},

    // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
    // The localname is the portion after the optional colon; the message indicates
    // that there is a problem with that part of the QNAME.
    { ER_ARG_LOCALNAME_INVALID,
       "Localname em QNAME deve ser um NCName v\u00e1lido"},

    // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
    // The prefix is the portion before the optional colon; the message indicates
    // that there is a problem with that part of the QNAME.
    { ER_ARG_PREFIX_INVALID,
       "O prefixo em QNAME deve ser um NCName v\u00e1lido"},

    { ER_NAME_CANT_START_WITH_COLON,
      "O nome n\u00e3o pode come\u00e7ar com um caractere de dois pontos (:)"},

    { "BAD_CODE", "O par\u00e2metro para createMessage estava fora dos limites"},
    { "FORMAT_FAILED", "Exce\u00e7\u00e3o emitida durante chamada messageFormat"},
    { "line", "Linha n\u00b0"},
    { "column","Coluna n\u00b0"}


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
                new Locale("pt", "BR"));
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
