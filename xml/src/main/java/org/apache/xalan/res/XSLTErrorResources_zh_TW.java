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
 * $Id: XSLTErrorResources_zh_TW.java 468641 2006-10-28 06:54:42Z minchau $
 */
package org.apache.xalan.res;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And
 *  you need to enter key , value pair as part of contents
 * Array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */
public class XSLTErrorResources_zh_TW extends ListResourceBundle
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

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 201;

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 29;

  /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 55;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


  /*
   * Static variables
   */
  public static final String ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX =
        "ER_INVALID_SET_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX";

  public static final String ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX_FOR_DEFAULT =
        "ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX_FOR_DEFAULT";

  public static final String ER_NO_CURLYBRACE = "ER_NO_CURLYBRACE";
  public static final String ER_FUNCTION_NOT_SUPPORTED = "ER_FUNCTION_NOT_SUPPORTED";
  public static final String ER_ILLEGAL_ATTRIBUTE = "ER_ILLEGAL_ATTRIBUTE";
  public static final String ER_NULL_SOURCENODE_APPLYIMPORTS = "ER_NULL_SOURCENODE_APPLYIMPORTS";
  public static final String ER_CANNOT_ADD = "ER_CANNOT_ADD";
  public static final String ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES="ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES";
  public static final String ER_NO_NAME_ATTRIB = "ER_NO_NAME_ATTRIB";
  public static final String ER_TEMPLATE_NOT_FOUND = "ER_TEMPLATE_NOT_FOUND";
  public static final String ER_CANT_RESOLVE_NAME_AVT = "ER_CANT_RESOLVE_NAME_AVT";
  public static final String ER_REQUIRES_ATTRIB = "ER_REQUIRES_ATTRIB";
  public static final String ER_MUST_HAVE_TEST_ATTRIB = "ER_MUST_HAVE_TEST_ATTRIB";
  public static final String ER_BAD_VAL_ON_LEVEL_ATTRIB =
         "ER_BAD_VAL_ON_LEVEL_ATTRIB";
  public static final String ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML =
         "ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML";
  public static final String ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME =
         "ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME";
  public static final String ER_NEED_MATCH_ATTRIB = "ER_NEED_MATCH_ATTRIB";
  public static final String ER_NEED_NAME_OR_MATCH_ATTRIB =
         "ER_NEED_NAME_OR_MATCH_ATTRIB";
  public static final String ER_CANT_RESOLVE_NSPREFIX =
         "ER_CANT_RESOLVE_NSPREFIX";
  public static final String ER_ILLEGAL_VALUE = "ER_ILLEGAL_VALUE";
  public static final String ER_NO_OWNERDOC = "ER_NO_OWNERDOC";
  public static final String ER_ELEMTEMPLATEELEM_ERR ="ER_ELEMTEMPLATEELEM_ERR";
  public static final String ER_NULL_CHILD = "ER_NULL_CHILD";
  public static final String ER_NEED_SELECT_ATTRIB = "ER_NEED_SELECT_ATTRIB";
  public static final String ER_NEED_TEST_ATTRIB = "ER_NEED_TEST_ATTRIB";
  public static final String ER_NEED_NAME_ATTRIB = "ER_NEED_NAME_ATTRIB";
  public static final String ER_NO_CONTEXT_OWNERDOC = "ER_NO_CONTEXT_OWNERDOC";
  public static final String ER_COULD_NOT_CREATE_XML_PROC_LIAISON =
         "ER_COULD_NOT_CREATE_XML_PROC_LIAISON";
  public static final String ER_PROCESS_NOT_SUCCESSFUL =
         "ER_PROCESS_NOT_SUCCESSFUL";
  public static final String ER_NOT_SUCCESSFUL = "ER_NOT_SUCCESSFUL";
  public static final String ER_ENCODING_NOT_SUPPORTED =
         "ER_ENCODING_NOT_SUPPORTED";
  public static final String ER_COULD_NOT_CREATE_TRACELISTENER =
         "ER_COULD_NOT_CREATE_TRACELISTENER";
  public static final String ER_KEY_REQUIRES_NAME_ATTRIB =
         "ER_KEY_REQUIRES_NAME_ATTRIB";
  public static final String ER_KEY_REQUIRES_MATCH_ATTRIB =
         "ER_KEY_REQUIRES_MATCH_ATTRIB";
  public static final String ER_KEY_REQUIRES_USE_ATTRIB =
         "ER_KEY_REQUIRES_USE_ATTRIB";
  public static final String ER_REQUIRES_ELEMENTS_ATTRIB =
         "ER_REQUIRES_ELEMENTS_ATTRIB";
  public static final String ER_MISSING_PREFIX_ATTRIB =
         "ER_MISSING_PREFIX_ATTRIB";
  public static final String ER_BAD_STYLESHEET_URL = "ER_BAD_STYLESHEET_URL";
  public static final String ER_FILE_NOT_FOUND = "ER_FILE_NOT_FOUND";
  public static final String ER_IOEXCEPTION = "ER_IOEXCEPTION";
  public static final String ER_NO_HREF_ATTRIB = "ER_NO_HREF_ATTRIB";
  public static final String ER_STYLESHEET_INCLUDES_ITSELF =
         "ER_STYLESHEET_INCLUDES_ITSELF";
  public static final String ER_PROCESSINCLUDE_ERROR ="ER_PROCESSINCLUDE_ERROR";
  public static final String ER_MISSING_LANG_ATTRIB = "ER_MISSING_LANG_ATTRIB";
  public static final String ER_MISSING_CONTAINER_ELEMENT_COMPONENT =
         "ER_MISSING_CONTAINER_ELEMENT_COMPONENT";
  public static final String ER_CAN_ONLY_OUTPUT_TO_ELEMENT =
         "ER_CAN_ONLY_OUTPUT_TO_ELEMENT";
  public static final String ER_PROCESS_ERROR = "ER_PROCESS_ERROR";
  public static final String ER_UNIMPLNODE_ERROR = "ER_UNIMPLNODE_ERROR";
  public static final String ER_NO_SELECT_EXPRESSION ="ER_NO_SELECT_EXPRESSION";
  public static final String ER_CANNOT_SERIALIZE_XSLPROCESSOR =
         "ER_CANNOT_SERIALIZE_XSLPROCESSOR";
  public static final String ER_NO_INPUT_STYLESHEET = "ER_NO_INPUT_STYLESHEET";
  public static final String ER_FAILED_PROCESS_STYLESHEET =
         "ER_FAILED_PROCESS_STYLESHEET";
  public static final String ER_COULDNT_PARSE_DOC = "ER_COULDNT_PARSE_DOC";
  public static final String ER_COULDNT_FIND_FRAGMENT =
         "ER_COULDNT_FIND_FRAGMENT";
  public static final String ER_NODE_NOT_ELEMENT = "ER_NODE_NOT_ELEMENT";
  public static final String ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB =
         "ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB";
  public static final String ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB =
         "ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB";
  public static final String ER_NO_CLONE_OF_DOCUMENT_FRAG =
         "ER_NO_CLONE_OF_DOCUMENT_FRAG";
  public static final String ER_CANT_CREATE_ITEM = "ER_CANT_CREATE_ITEM";
  public static final String ER_XMLSPACE_ILLEGAL_VALUE =
         "ER_XMLSPACE_ILLEGAL_VALUE";
  public static final String ER_NO_XSLKEY_DECLARATION =
         "ER_NO_XSLKEY_DECLARATION";
  public static final String ER_CANT_CREATE_URL = "ER_CANT_CREATE_URL";
  public static final String ER_XSLFUNCTIONS_UNSUPPORTED =
         "ER_XSLFUNCTIONS_UNSUPPORTED";
  public static final String ER_PROCESSOR_ERROR = "ER_PROCESSOR_ERROR";
  public static final String ER_NOT_ALLOWED_INSIDE_STYLESHEET =
         "ER_NOT_ALLOWED_INSIDE_STYLESHEET";
  public static final String ER_RESULTNS_NOT_SUPPORTED =
         "ER_RESULTNS_NOT_SUPPORTED";
  public static final String ER_DEFAULTSPACE_NOT_SUPPORTED =
         "ER_DEFAULTSPACE_NOT_SUPPORTED";
  public static final String ER_INDENTRESULT_NOT_SUPPORTED =
         "ER_INDENTRESULT_NOT_SUPPORTED";
  public static final String ER_ILLEGAL_ATTRIB = "ER_ILLEGAL_ATTRIB";
  public static final String ER_UNKNOWN_XSL_ELEM = "ER_UNKNOWN_XSL_ELEM";
  public static final String ER_BAD_XSLSORT_USE = "ER_BAD_XSLSORT_USE";
  public static final String ER_MISPLACED_XSLWHEN = "ER_MISPLACED_XSLWHEN";
  public static final String ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE =
         "ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE";
  public static final String ER_MISPLACED_XSLOTHERWISE =
         "ER_MISPLACED_XSLOTHERWISE";
  public static final String ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE =
         "ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE";
  public static final String ER_NOT_ALLOWED_INSIDE_TEMPLATE =
         "ER_NOT_ALLOWED_INSIDE_TEMPLATE";
  public static final String ER_UNKNOWN_EXT_NS_PREFIX =
         "ER_UNKNOWN_EXT_NS_PREFIX";
  public static final String ER_IMPORTS_AS_FIRST_ELEM =
         "ER_IMPORTS_AS_FIRST_ELEM";
  public static final String ER_IMPORTING_ITSELF = "ER_IMPORTING_ITSELF";
  public static final String ER_XMLSPACE_ILLEGAL_VAL ="ER_XMLSPACE_ILLEGAL_VAL";
  public static final String ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL =
         "ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL";
  public static final String ER_SAX_EXCEPTION = "ER_SAX_EXCEPTION";
  public static final String ER_XSLT_ERROR = "ER_XSLT_ERROR";
  public static final String ER_CURRENCY_SIGN_ILLEGAL=
         "ER_CURRENCY_SIGN_ILLEGAL";
  public static final String ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM =
         "ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM";
  public static final String ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER =
         "ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER";
  public static final String ER_REDIRECT_COULDNT_GET_FILENAME =
         "ER_REDIRECT_COULDNT_GET_FILENAME";
  public static final String ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT =
         "ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT";
  public static final String ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX =
         "ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX";
  public static final String ER_MISSING_NS_URI = "ER_MISSING_NS_URI";
  public static final String ER_MISSING_ARG_FOR_OPTION =
         "ER_MISSING_ARG_FOR_OPTION";
  public static final String ER_INVALID_OPTION = "ER_INVALID_OPTION";
  public static final String ER_MALFORMED_FORMAT_STRING =
         "ER_MALFORMED_FORMAT_STRING";
  public static final String ER_STYLESHEET_REQUIRES_VERSION_ATTRIB =
         "ER_STYLESHEET_REQUIRES_VERSION_ATTRIB";
  public static final String ER_ILLEGAL_ATTRIBUTE_VALUE =
         "ER_ILLEGAL_ATTRIBUTE_VALUE";
  public static final String ER_CHOOSE_REQUIRES_WHEN ="ER_CHOOSE_REQUIRES_WHEN";
  public static final String ER_NO_APPLY_IMPORT_IN_FOR_EACH =
         "ER_NO_APPLY_IMPORT_IN_FOR_EACH";
  public static final String ER_CANT_USE_DTM_FOR_OUTPUT =
         "ER_CANT_USE_DTM_FOR_OUTPUT";
  public static final String ER_CANT_USE_DTM_FOR_INPUT =
         "ER_CANT_USE_DTM_FOR_INPUT";
  public static final String ER_CALL_TO_EXT_FAILED = "ER_CALL_TO_EXT_FAILED";
  public static final String ER_PREFIX_MUST_RESOLVE = "ER_PREFIX_MUST_RESOLVE";
  public static final String ER_INVALID_UTF16_SURROGATE =
         "ER_INVALID_UTF16_SURROGATE";
  public static final String ER_XSLATTRSET_USED_ITSELF =
         "ER_XSLATTRSET_USED_ITSELF";
  public static final String ER_CANNOT_MIX_XERCESDOM ="ER_CANNOT_MIX_XERCESDOM";
  public static final String ER_TOO_MANY_LISTENERS = "ER_TOO_MANY_LISTENERS";
  public static final String ER_IN_ELEMTEMPLATEELEM_READOBJECT =
         "ER_IN_ELEMTEMPLATEELEM_READOBJECT";
  public static final String ER_DUPLICATE_NAMED_TEMPLATE =
         "ER_DUPLICATE_NAMED_TEMPLATE";
  public static final String ER_INVALID_KEY_CALL = "ER_INVALID_KEY_CALL";
  public static final String ER_REFERENCING_ITSELF = "ER_REFERENCING_ITSELF";
  public static final String ER_ILLEGAL_DOMSOURCE_INPUT =
         "ER_ILLEGAL_DOMSOURCE_INPUT";
  public static final String ER_CLASS_NOT_FOUND_FOR_OPTION =
         "ER_CLASS_NOT_FOUND_FOR_OPTION";
  public static final String ER_REQUIRED_ELEM_NOT_FOUND =
         "ER_REQUIRED_ELEM_NOT_FOUND";
  public static final String ER_INPUT_CANNOT_BE_NULL ="ER_INPUT_CANNOT_BE_NULL";
  public static final String ER_URI_CANNOT_BE_NULL = "ER_URI_CANNOT_BE_NULL";
  public static final String ER_FILE_CANNOT_BE_NULL = "ER_FILE_CANNOT_BE_NULL";
  public static final String ER_SOURCE_CANNOT_BE_NULL =
         "ER_SOURCE_CANNOT_BE_NULL";
  public static final String ER_CANNOT_INIT_BSFMGR = "ER_CANNOT_INIT_BSFMGR";
  public static final String ER_CANNOT_CMPL_EXTENSN = "ER_CANNOT_CMPL_EXTENSN";
  public static final String ER_CANNOT_CREATE_EXTENSN =
         "ER_CANNOT_CREATE_EXTENSN";
  public static final String ER_INSTANCE_MTHD_CALL_REQUIRES =
         "ER_INSTANCE_MTHD_CALL_REQUIRES";
  public static final String ER_INVALID_ELEMENT_NAME ="ER_INVALID_ELEMENT_NAME";
  public static final String ER_ELEMENT_NAME_METHOD_STATIC =
         "ER_ELEMENT_NAME_METHOD_STATIC";
  public static final String ER_EXTENSION_FUNC_UNKNOWN =
         "ER_EXTENSION_FUNC_UNKNOWN";
  public static final String ER_MORE_MATCH_CONSTRUCTOR =
         "ER_MORE_MATCH_CONSTRUCTOR";
  public static final String ER_MORE_MATCH_METHOD = "ER_MORE_MATCH_METHOD";
  public static final String ER_MORE_MATCH_ELEMENT = "ER_MORE_MATCH_ELEMENT";
  public static final String ER_INVALID_CONTEXT_PASSED =
         "ER_INVALID_CONTEXT_PASSED";
  public static final String ER_POOL_EXISTS = "ER_POOL_EXISTS";
  public static final String ER_NO_DRIVER_NAME = "ER_NO_DRIVER_NAME";
  public static final String ER_NO_URL = "ER_NO_URL";
  public static final String ER_POOL_SIZE_LESSTHAN_ONE =
         "ER_POOL_SIZE_LESSTHAN_ONE";
  public static final String ER_INVALID_DRIVER = "ER_INVALID_DRIVER";
  public static final String ER_NO_STYLESHEETROOT = "ER_NO_STYLESHEETROOT";
  public static final String ER_ILLEGAL_XMLSPACE_VALUE =
         "ER_ILLEGAL_XMLSPACE_VALUE";
  public static final String ER_PROCESSFROMNODE_FAILED =
         "ER_PROCESSFROMNODE_FAILED";
  public static final String ER_RESOURCE_COULD_NOT_LOAD =
         "ER_RESOURCE_COULD_NOT_LOAD";
  public static final String ER_BUFFER_SIZE_LESSTHAN_ZERO =
         "ER_BUFFER_SIZE_LESSTHAN_ZERO";
  public static final String ER_UNKNOWN_ERROR_CALLING_EXTENSION =
         "ER_UNKNOWN_ERROR_CALLING_EXTENSION";
  public static final String ER_NO_NAMESPACE_DECL = "ER_NO_NAMESPACE_DECL";
  public static final String ER_ELEM_CONTENT_NOT_ALLOWED =
         "ER_ELEM_CONTENT_NOT_ALLOWED";
  public static final String ER_STYLESHEET_DIRECTED_TERMINATION =
         "ER_STYLESHEET_DIRECTED_TERMINATION";
  public static final String ER_ONE_OR_TWO = "ER_ONE_OR_TWO";
  public static final String ER_TWO_OR_THREE = "ER_TWO_OR_THREE";
  public static final String ER_COULD_NOT_LOAD_RESOURCE =
         "ER_COULD_NOT_LOAD_RESOURCE";
  public static final String ER_CANNOT_INIT_DEFAULT_TEMPLATES =
         "ER_CANNOT_INIT_DEFAULT_TEMPLATES";
  public static final String ER_RESULT_NULL = "ER_RESULT_NULL";
  public static final String ER_RESULT_COULD_NOT_BE_SET =
         "ER_RESULT_COULD_NOT_BE_SET";
  public static final String ER_NO_OUTPUT_SPECIFIED = "ER_NO_OUTPUT_SPECIFIED";
  public static final String ER_CANNOT_TRANSFORM_TO_RESULT_TYPE =
         "ER_CANNOT_TRANSFORM_TO_RESULT_TYPE";
  public static final String ER_CANNOT_TRANSFORM_SOURCE_TYPE =
         "ER_CANNOT_TRANSFORM_SOURCE_TYPE";
  public static final String ER_NULL_CONTENT_HANDLER ="ER_NULL_CONTENT_HANDLER";
  public static final String ER_NULL_ERROR_HANDLER = "ER_NULL_ERROR_HANDLER";
  public static final String ER_CANNOT_CALL_PARSE = "ER_CANNOT_CALL_PARSE";
  public static final String ER_NO_PARENT_FOR_FILTER ="ER_NO_PARENT_FOR_FILTER";
  public static final String ER_NO_STYLESHEET_IN_MEDIA =
         "ER_NO_STYLESHEET_IN_MEDIA";
  public static final String ER_NO_STYLESHEET_PI = "ER_NO_STYLESHEET_PI";
  public static final String ER_NOT_SUPPORTED = "ER_NOT_SUPPORTED";
  public static final String ER_PROPERTY_VALUE_BOOLEAN =
         "ER_PROPERTY_VALUE_BOOLEAN";
  public static final String ER_COULD_NOT_FIND_EXTERN_SCRIPT =
         "ER_COULD_NOT_FIND_EXTERN_SCRIPT";
  public static final String ER_RESOURCE_COULD_NOT_FIND =
         "ER_RESOURCE_COULD_NOT_FIND";
  public static final String ER_OUTPUT_PROPERTY_NOT_RECOGNIZED =
         "ER_OUTPUT_PROPERTY_NOT_RECOGNIZED";
  public static final String ER_FAILED_CREATING_ELEMLITRSLT =
         "ER_FAILED_CREATING_ELEMLITRSLT";
  public static final String ER_VALUE_SHOULD_BE_NUMBER =
         "ER_VALUE_SHOULD_BE_NUMBER";
  public static final String ER_VALUE_SHOULD_EQUAL = "ER_VALUE_SHOULD_EQUAL";
  public static final String ER_FAILED_CALLING_METHOD =
         "ER_FAILED_CALLING_METHOD";
  public static final String ER_FAILED_CREATING_ELEMTMPL =
         "ER_FAILED_CREATING_ELEMTMPL";
  public static final String ER_CHARS_NOT_ALLOWED = "ER_CHARS_NOT_ALLOWED";
  public static final String ER_ATTR_NOT_ALLOWED = "ER_ATTR_NOT_ALLOWED";
  public static final String ER_BAD_VALUE = "ER_BAD_VALUE";
  public static final String ER_ATTRIB_VALUE_NOT_FOUND =
         "ER_ATTRIB_VALUE_NOT_FOUND";
  public static final String ER_ATTRIB_VALUE_NOT_RECOGNIZED =
         "ER_ATTRIB_VALUE_NOT_RECOGNIZED";
  public static final String ER_NULL_URI_NAMESPACE = "ER_NULL_URI_NAMESPACE";
  public static final String ER_NUMBER_TOO_BIG = "ER_NUMBER_TOO_BIG";
  public static final String  ER_CANNOT_FIND_SAX1_DRIVER =
         "ER_CANNOT_FIND_SAX1_DRIVER";
  public static final String  ER_SAX1_DRIVER_NOT_LOADED =
         "ER_SAX1_DRIVER_NOT_LOADED";
  public static final String  ER_SAX1_DRIVER_NOT_INSTANTIATED =
         "ER_SAX1_DRIVER_NOT_INSTANTIATED" ;
  public static final String ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER =
         "ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER";
  public static final String  ER_PARSER_PROPERTY_NOT_SPECIFIED =
         "ER_PARSER_PROPERTY_NOT_SPECIFIED";
  public static final String  ER_PARSER_ARG_CANNOT_BE_NULL =
         "ER_PARSER_ARG_CANNOT_BE_NULL" ;
  public static final String  ER_FEATURE = "ER_FEATURE";
  public static final String ER_PROPERTY = "ER_PROPERTY" ;
  public static final String ER_NULL_ENTITY_RESOLVER ="ER_NULL_ENTITY_RESOLVER";
  public static final String  ER_NULL_DTD_HANDLER = "ER_NULL_DTD_HANDLER" ;
  public static final String ER_NO_DRIVER_NAME_SPECIFIED =
         "ER_NO_DRIVER_NAME_SPECIFIED";
  public static final String ER_NO_URL_SPECIFIED = "ER_NO_URL_SPECIFIED";
  public static final String ER_POOLSIZE_LESS_THAN_ONE =
         "ER_POOLSIZE_LESS_THAN_ONE";
  public static final String ER_INVALID_DRIVER_NAME = "ER_INVALID_DRIVER_NAME";
  public static final String ER_ERRORLISTENER = "ER_ERRORLISTENER";
  public static final String ER_ASSERT_NO_TEMPLATE_PARENT =
         "ER_ASSERT_NO_TEMPLATE_PARENT";
  public static final String ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR =
         "ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR";
  public static final String ER_NOT_ALLOWED_IN_POSITION =
         "ER_NOT_ALLOWED_IN_POSITION";
  public static final String ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION =
         "ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION";
  public static final String ER_NAMESPACE_CONTEXT_NULL_NAMESPACE =
         "ER_NAMESPACE_CONTEXT_NULL_NAMESPACE";
  public static final String ER_NAMESPACE_CONTEXT_NULL_PREFIX =
         "ER_NAMESPACE_CONTEXT_NULL_PREFIX";
  public static final String ER_XPATH_RESOLVER_NULL_QNAME =
         "ER_XPATH_RESOLVER_NULL_QNAME";
  public static final String ER_XPATH_RESOLVER_NEGATIVE_ARITY =
         "ER_XPATH_RESOLVER_NEGATIVE_ARITY";
  public static final String INVALID_TCHAR = "INVALID_TCHAR";
  public static final String INVALID_QNAME = "INVALID_QNAME";
  public static final String INVALID_ENUM = "INVALID_ENUM";
  public static final String INVALID_NMTOKEN = "INVALID_NMTOKEN";
  public static final String INVALID_NCNAME = "INVALID_NCNAME";
  public static final String INVALID_BOOLEAN = "INVALID_BOOLEAN";
  public static final String INVALID_NUMBER = "INVALID_NUMBER";
  public static final String ER_ARG_LITERAL = "ER_ARG_LITERAL";
  public static final String ER_DUPLICATE_GLOBAL_VAR ="ER_DUPLICATE_GLOBAL_VAR";
  public static final String ER_DUPLICATE_VAR = "ER_DUPLICATE_VAR";
  public static final String ER_TEMPLATE_NAME_MATCH = "ER_TEMPLATE_NAME_MATCH";
  public static final String ER_INVALID_PREFIX = "ER_INVALID_PREFIX";
  public static final String ER_NO_ATTRIB_SET = "ER_NO_ATTRIB_SET";
  public static final String ER_FUNCTION_NOT_FOUND =
         "ER_FUNCTION_NOT_FOUND";
  public static final String ER_CANT_HAVE_CONTENT_AND_SELECT =
     "ER_CANT_HAVE_CONTENT_AND_SELECT";
  public static final String ER_INVALID_SET_PARAM_VALUE = "ER_INVALID_SET_PARAM_VALUE";
  public static final String ER_SET_FEATURE_NULL_NAME =
        "ER_SET_FEATURE_NULL_NAME";
  public static final String ER_GET_FEATURE_NULL_NAME =
        "ER_GET_FEATURE_NULL_NAME";
  public static final String ER_UNSUPPORTED_FEATURE =
        "ER_UNSUPPORTED_FEATURE";
  public static final String ER_EXTENSION_ELEMENT_NOT_ALLOWED_IN_SECURE_PROCESSING =
        "ER_EXTENSION_ELEMENT_NOT_ALLOWED_IN_SECURE_PROCESSING";

  public static final String WG_FOUND_CURLYBRACE = "WG_FOUND_CURLYBRACE";
  public static final String WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR =
         "WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR";
  public static final String WG_EXPR_ATTRIB_CHANGED_TO_SELECT =
         "WG_EXPR_ATTRIB_CHANGED_TO_SELECT";
  public static final String WG_NO_LOCALE_IN_FORMATNUMBER =
         "WG_NO_LOCALE_IN_FORMATNUMBER";
  public static final String WG_LOCALE_NOT_FOUND = "WG_LOCALE_NOT_FOUND";
  public static final String WG_CANNOT_MAKE_URL_FROM ="WG_CANNOT_MAKE_URL_FROM";
  public static final String WG_CANNOT_LOAD_REQUESTED_DOC =
         "WG_CANNOT_LOAD_REQUESTED_DOC";
  public static final String WG_CANNOT_FIND_COLLATOR ="WG_CANNOT_FIND_COLLATOR";
  public static final String WG_FUNCTIONS_SHOULD_USE_URL =
         "WG_FUNCTIONS_SHOULD_USE_URL";
  public static final String WG_ENCODING_NOT_SUPPORTED_USING_UTF8 =
         "WG_ENCODING_NOT_SUPPORTED_USING_UTF8";
  public static final String WG_ENCODING_NOT_SUPPORTED_USING_JAVA =
         "WG_ENCODING_NOT_SUPPORTED_USING_JAVA";
  public static final String WG_SPECIFICITY_CONFLICTS =
         "WG_SPECIFICITY_CONFLICTS";
  public static final String WG_PARSING_AND_PREPARING =
         "WG_PARSING_AND_PREPARING";
  public static final String WG_ATTR_TEMPLATE = "WG_ATTR_TEMPLATE";
  public static final String WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = "WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESP";
  public static final String WG_ATTRIB_NOT_HANDLED = "WG_ATTRIB_NOT_HANDLED";
  public static final String WG_NO_DECIMALFORMAT_DECLARATION =
         "WG_NO_DECIMALFORMAT_DECLARATION";
  public static final String WG_OLD_XSLT_NS = "WG_OLD_XSLT_NS";
  public static final String WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED =
         "WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED";
  public static final String WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE =
         "WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE";
  public static final String WG_ILLEGAL_ATTRIBUTE = "WG_ILLEGAL_ATTRIBUTE";
  public static final String WG_COULD_NOT_RESOLVE_PREFIX =
         "WG_COULD_NOT_RESOLVE_PREFIX";
  public static final String WG_STYLESHEET_REQUIRES_VERSION_ATTRIB =
         "WG_STYLESHEET_REQUIRES_VERSION_ATTRIB";
  public static final String WG_ILLEGAL_ATTRIBUTE_NAME =
         "WG_ILLEGAL_ATTRIBUTE_NAME";
  public static final String WG_ILLEGAL_ATTRIBUTE_VALUE =
         "WG_ILLEGAL_ATTRIBUTE_VALUE";
  public static final String WG_EMPTY_SECOND_ARG = "WG_EMPTY_SECOND_ARG";
  public static final String WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML =
         "WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML";
  public static final String WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME =
         "WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME";
  public static final String WG_ILLEGAL_ATTRIBUTE_POSITION =
         "WG_ILLEGAL_ATTRIBUTE_POSITION";
  public static final String NO_MODIFICATION_ALLOWED_ERR =
         "NO_MODIFICATION_ALLOWED_ERR";

  /*
   * Now fill in the message text.
   * Then fill in the message text for that message code in the
   * array. Use the new error code as the index into the array.
   */

  // Error messages...

  /** Get the lookup table for error messages.
   *
   * @return The message lookup table.
   */
  public Object[][] getContents()
  {
    return new Object[][] {

  /** Error message ID that has a null message, but takes in a single object.    */
  {"ER0000" , "{0}" },


    { ER_NO_CURLYBRACE,
      "\u932f\u8aa4\uff1a\u8868\u793a\u5f0f\u5167\u4e0d\u80fd\u6709 '{'"},

    { ER_ILLEGAL_ATTRIBUTE ,
     "{0} \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u5c6c\u6027\uff1a{1}"},

  {ER_NULL_SOURCENODE_APPLYIMPORTS ,
      "\u5728 xsl:apply-imports \u4e2d\uff0csourceNode \u662f\u7a7a\u503c\uff01"},

  {ER_CANNOT_ADD,
      "\u4e0d\u80fd\u65b0\u589e {0} \u5230 {1}"},

    { ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES,
      "\u5728 handleApplyTemplatesInstruction \u4e2d\uff0csourceNode \u662f\u7a7a\u503c\uff01"},

    { ER_NO_NAME_ATTRIB,
     "{0} \u5fc5\u9808\u6709\u540d\u7a31\u5c6c\u6027\u3002"},

    {ER_TEMPLATE_NOT_FOUND,
     "\u627e\u4e0d\u5230\u6307\u540d\u70ba\uff1a{0} \u7684\u7bc4\u672c"},

    {ER_CANT_RESOLVE_NAME_AVT,
      "\u7121\u6cd5\u89e3\u6790 xsl:call-template \u4e2d\u7684\u540d\u7a31 AVT\u3002"},

    {ER_REQUIRES_ATTRIB,
     "{0} \u9700\u8981\u5c6c\u6027\uff1a{1}"},

    { ER_MUST_HAVE_TEST_ATTRIB,
      "{0} \u5fc5\u9808\u6709 ''test'' \u5c6c\u6027\u3002"},

    {ER_BAD_VAL_ON_LEVEL_ATTRIB,
      "\u5c64\u6b21\u5c6c\u6027\uff1a{0} \u5305\u542b\u4e0d\u6b63\u78ba\u7684\u503c"},

    {ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "processing-instruction \u540d\u7a31\u4e0d\u80fd\u662f 'xml'"},

    { ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "processing-instruction \u540d\u7a31\u5fc5\u9808\u662f\u6709\u6548\u7684 NCName\uff1a{0}"},

    { ER_NEED_MATCH_ATTRIB,
      "{0} \u5982\u679c\u6709\u6a21\u5f0f\uff0c\u5fc5\u9808\u6709\u7b26\u5408\u5c6c\u6027\u3002"},

    { ER_NEED_NAME_OR_MATCH_ATTRIB,
      "{0} \u9700\u8981\u540d\u7a31\u6216\u7b26\u5408\u5c6c\u6027\u3002"},

    {ER_CANT_RESOLVE_NSPREFIX,
      "\u7121\u6cd5\u89e3\u6790\u540d\u7a31\u7a7a\u9593\u5b57\u9996\uff1a{0}"},

    { ER_ILLEGAL_VALUE,
     "xml:space \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u503c\uff1a{0}"},

    { ER_NO_OWNERDOC,
      "\u5b50\u7bc0\u9ede\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01"},

    { ER_ELEMTEMPLATEELEM_ERR,
     "ElemTemplateElement \u932f\u8aa4\uff1a{0}"},

    { ER_NULL_CHILD,
     "\u5617\u8a66\u65b0\u589e\u7a7a\u503c\u5b50\u9805\u5143\u4ef6\uff01"},

    { ER_NEED_SELECT_ATTRIB,
     "{0} \u9700\u8981\u9078\u53d6\u5c6c\u6027\u3002"},

    { ER_NEED_TEST_ATTRIB ,
      "xsl:when \u5fc5\u9808\u6709 'test' \u5c6c\u6027\u3002"},

    { ER_NEED_NAME_ATTRIB,
      "xsl:with-param \u5fc5\u9808\u6709 'name' \u5c6c\u6027\u3002"},

    { ER_NO_CONTEXT_OWNERDOC,
      "\u74b0\u5883\u5b9a\u7fa9\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01"},

    {ER_COULD_NOT_CREATE_XML_PROC_LIAISON,
      "\u7121\u6cd5\u5efa\u7acb XML TransformerFactory Liaison\uff1a{0}"},

    {ER_PROCESS_NOT_SUCCESSFUL,
      "Xalan: \u7a0b\u5e8f\u6c92\u6709\u9806\u5229\u5b8c\u6210\u3002"},

    { ER_NOT_SUCCESSFUL,
     "Xalan: \u4e0d\u6210\u529f\u3002"},

    { ER_ENCODING_NOT_SUPPORTED,
     "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}"},

    {ER_COULD_NOT_CREATE_TRACELISTENER,
      "\u7121\u6cd5\u5efa\u7acb TraceListener\uff1a{0}"},

    {ER_KEY_REQUIRES_NAME_ATTRIB,
      "xsl:key \u9700\u8981 'name' \u5c6c\u6027\uff01"},

    { ER_KEY_REQUIRES_MATCH_ATTRIB,
      "xsl:key \u9700\u8981 'match' \u5c6c\u6027\uff01"},

    { ER_KEY_REQUIRES_USE_ATTRIB,
      "xsl:key \u9700\u8981 'use' \u5c6c\u6027\uff01"},

    { ER_REQUIRES_ELEMENTS_ATTRIB,
      "(StylesheetHandler) {0} \u9700\u8981 ''elements'' \u5c6c\u6027\uff01"},

    { ER_MISSING_PREFIX_ATTRIB,
      "(StylesheetHandler) {0} \u5c6c\u6027 ''prefix'' \u907a\u6f0f"},

    { ER_BAD_STYLESHEET_URL,
     "\u6a23\u5f0f\u8868 URL \u4e0d\u6b63\u78ba\uff1a{0}"},

    { ER_FILE_NOT_FOUND,
     "\u627e\u4e0d\u5230\u6a23\u5f0f\u8868\u6a94\u6848\uff1a{0}"},

    { ER_IOEXCEPTION,
      "\u6a23\u5f0f\u8868\u6a94\u6848\uff1a{0} \u767c\u751f IO \u7570\u5e38"},

    { ER_NO_HREF_ATTRIB,
      "(StylesheetHandler) \u627e\u4e0d\u5230 {0} \u7684 href \u5c6c\u6027"},

    { ER_STYLESHEET_INCLUDES_ITSELF,
      "(StylesheetHandler) {0} \u76f4\u63a5\u6216\u9593\u63a5\u5305\u542b\u81ea\u5df1\uff01"},

    { ER_PROCESSINCLUDE_ERROR,
      "StylesheetHandler.processInclude \u932f\u8aa4\uff0c{0}"},

    { ER_MISSING_LANG_ATTRIB,
      "(StylesheetHandler) {0} \u5c6c\u6027 ''lang'' \u907a\u6f0f"},

    { ER_MISSING_CONTAINER_ELEMENT_COMPONENT,
      "(StylesheetHandler) \u653e\u7f6e\u932f\u8aa4\u7684 {0} \u5143\u7d20\uff1f\uff1f\u907a\u6f0f\u5132\u5b58\u5668\u5143\u7d20 ''component''"},

    { ER_CAN_ONLY_OUTPUT_TO_ELEMENT,
      "\u53ea\u80fd\u8f38\u51fa\u81f3 Element\u3001DocumentFragment\u3001Document \u6216 PrintWriter\u3002"},

    { ER_PROCESS_ERROR,
     "StylesheetRoot.process \u932f\u8aa4"},

    { ER_UNIMPLNODE_ERROR,
     "UnImplNode \u932f\u8aa4\uff1a{0}"},

    { ER_NO_SELECT_EXPRESSION,
      "\u932f\u8aa4\uff01\u6c92\u6709\u627e\u5230 xpath select \u8868\u793a\u5f0f (-select)\u3002"},

    { ER_CANNOT_SERIALIZE_XSLPROCESSOR,
      "\u7121\u6cd5\u5e8f\u5217\u5316 XSLProcessor\uff01"},

    { ER_NO_INPUT_STYLESHEET,
      "\u6c92\u6709\u6307\u5b9a\u6a23\u5f0f\u8868\u8f38\u5165\uff01"},

    { ER_FAILED_PROCESS_STYLESHEET,
      "\u7121\u6cd5\u8655\u7406\u6a23\u5f0f\u8868\uff01"},

    { ER_COULDNT_PARSE_DOC,
     "\u7121\u6cd5\u5256\u6790 {0} \u6587\u4ef6\uff01"},

    { ER_COULDNT_FIND_FRAGMENT,
     "\u627e\u4e0d\u5230\u7247\u6bb5\uff1a{0}"},

    { ER_NODE_NOT_ELEMENT,
      "\u7247\u6bb5 ID \u6240\u6307\u5411\u7684\u7bc0\u9ede\u4e0d\u662f\u5143\u7d20\uff1a{0}"},

    { ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB,
      "for-each \u5fc5\u9808\u6709 match \u6216 name \u5c6c\u6027"},

    { ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB,
      "templates \u5fc5\u9808\u6709 match \u6216 name \u5c6c\u6027"},

    { ER_NO_CLONE_OF_DOCUMENT_FRAG,
      "\u6587\u4ef6\u7247\u6bb5\u6c92\u6709\u8907\u88fd\uff01"},

    { ER_CANT_CREATE_ITEM,
      "\u7121\u6cd5\u5728\u7d50\u679c\u6a39\uff1a{0} \u4e2d\u5efa\u7acb\u9805\u76ee"},

    { ER_XMLSPACE_ILLEGAL_VALUE,
      "\u539f\u59cb\u6a94 XML \u4e2d\u7684 xml:space \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u503c\uff1a{0}"},

    { ER_NO_XSLKEY_DECLARATION,
      "{0} \u6c92\u6709 xsl:key \u5ba3\u544a\uff01"},

    { ER_CANT_CREATE_URL,
     "\u932f\u8aa4\uff01\u7121\u6cd5\u91dd\u5c0d\uff1a{0} \u5efa\u7acb URL"},

    { ER_XSLFUNCTIONS_UNSUPPORTED,
     "xsl:functions \u4e0d\u53d7\u652f\u63f4"},

    { ER_PROCESSOR_ERROR,
     "XSLT TransformerFactory \u932f\u8aa4"},

    { ER_NOT_ALLOWED_INSIDE_STYLESHEET,
      "(StylesheetHandler) {0} \u4e0d\u5141\u8a31\u5728\u6a23\u5f0f\u8868\u5167\uff01"},

    { ER_RESULTNS_NOT_SUPPORTED,
      "result-ns \u4e0d\u518d\u53d7\u652f\u63f4\uff01\u8acb\u6539\u7528 xsl:output\u3002"},

    { ER_DEFAULTSPACE_NOT_SUPPORTED,
      "default-space \u4e0d\u518d\u53d7\u652f\u63f4\uff01\u8acb\u6539\u7528 xsl:strip-space \u6216 xsl:preserve-space\u3002"},

    { ER_INDENTRESULT_NOT_SUPPORTED,
      "indent-result \u4e0d\u518d\u53d7\u652f\u63f4\uff01\u8acb\u6539\u7528 xsl:output\u3002"},

    { ER_ILLEGAL_ATTRIB,
      "(StylesheetHandler) {0} \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u5c6c\u6027\uff1a{1}"},

    { ER_UNKNOWN_XSL_ELEM,
     "\u4e0d\u660e XSL \u5143\u7d20\uff1a{0}"},

    { ER_BAD_XSLSORT_USE,
      "(StylesheetHandler) xsl:sort \u53ea\u80fd\u548c xsl:apply-templates \u6216 xsl:for-each \u4e00\u8d77\u4f7f\u7528\u3002"},

    { ER_MISPLACED_XSLWHEN,
      "(StylesheetHandler) \u653e\u7f6e\u932f\u8aa4\u7684 xsl:when\uff01"},

    { ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:when \u7684\u6bcd\u9805\u4e0d\u662f xsl:choose\uff01"},

    { ER_MISPLACED_XSLOTHERWISE,
      "(StylesheetHandler) \u653e\u7f6e\u932f\u8aa4\u7684 xsl:otherwise\uff01"},

    { ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE,
      "(StylesheetHandler) xsl:otherwise \u7684\u6bcd\u9805\u4e0d\u662f xsl:choose\uff01"},

    { ER_NOT_ALLOWED_INSIDE_TEMPLATE,
      "\u5728\u7bc4\u672c\u5167\u4e0d\u5141\u8a31 (StylesheetHandler) {0}\uff01"},

    { ER_UNKNOWN_EXT_NS_PREFIX,
      "(StylesheetHandler) {0} \u5ef6\u4f38\u9805\u76ee\u540d\u7a31\u7a7a\u9593\u5b57\u9996 {1} \u4e0d\u660e"},

    { ER_IMPORTS_AS_FIRST_ELEM,
      "(StylesheetHandler) Imports \u53ea\u80fd\u51fa\u73fe\u5728\u6a23\u5f0f\u8868\u4e2d\u4f5c\u70ba\u7b2c\u4e00\u500b\u5143\u7d20\uff01"},

    { ER_IMPORTING_ITSELF,
      "(StylesheetHandler) {0} \u6b63\u5728\u76f4\u63a5\u6216\u9593\u63a5\u532f\u5165\u81ea\u5df1\uff01"},

    { ER_XMLSPACE_ILLEGAL_VAL,
      "(StylesheetHandler) xml:space \u6709\u4e0d\u5408\u6cd5\u7684\u503c\uff1a{0}"},

    { ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL,
      "processStylesheet \u4e0d\u6210\u529f\uff01"},

    { ER_SAX_EXCEPTION,
     "SAX \u7570\u5e38"},

//  add this message to fix bug 21478
    { ER_FUNCTION_NOT_SUPPORTED,
     "\u51fd\u6578\u4e0d\u53d7\u652f\u63f4\uff01"},


    { ER_XSLT_ERROR,
     "XSLT \u932f\u8aa4"},

    { ER_CURRENCY_SIGN_ILLEGAL,
      "\u5728\u683c\u5f0f\u578b\u6a23\u5b57\u4e32\u4e2d\u4e0d\u5141\u8a31\u8ca8\u5e63\u7b26\u865f"},

    { ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM,
      "\u5728\u6a23\u5f0f\u8868 DOM \u4e2d\u4e0d\u652f\u63f4\u6587\u4ef6\u51fd\u6578\uff01"},

    { ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER,
      "\u7121\u6cd5\u89e3\u6790\u975e\u5b57\u9996\u89e3\u6790\u5668\u7684\u5b57\u9996\uff01"},

    { ER_REDIRECT_COULDNT_GET_FILENAME,
      "\u91cd\u65b0\u5c0e\u5411\u5ef6\u4f38\u9805\u76ee\uff1a\u7121\u6cd5\u53d6\u5f97\u6a94\u6848\u540d\u7a31 - file \u6216 select \u5c6c\u6027\u5fc5\u9808\u50b3\u56de\u6709\u6548\u5b57\u4e32\u3002"},

    { ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT,
      "\u7121\u6cd5\u5728\u91cd\u65b0\u5c0e\u5411\u5ef6\u4f38\u9805\u76ee\u4e2d\u5efa\u7acb FormatterListener\uff01"},

    { ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX,
      "exclude-result-prefixes \u4e2d\u7684\u5b57\u9996\u7121\u6548\uff1a{0}"},

    { ER_MISSING_NS_URI,
      "\u907a\u6f0f\u6307\u5b9a\u7684\u5b57\u9996\u7684\u540d\u7a31\u7a7a\u9593 URI"},

    { ER_MISSING_ARG_FOR_OPTION,
      "\u907a\u6f0f\u9078\u9805\uff1a{0} \u7684\u5f15\u6578"},

    { ER_INVALID_OPTION,
     "\u9078\u9805\uff1a{0} \u7121\u6548"},

    { ER_MALFORMED_FORMAT_STRING,
     "\u4e0d\u6b63\u78ba\u7684\u683c\u5f0f\u5b57\u4e32\uff1a{0}"},

    { ER_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u9700\u8981 'version' \u5c6c\u6027\uff01"},

    { ER_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c6c\u6027\uff1a{0} \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u503c\uff1a{1}"},

    { ER_CHOOSE_REQUIRES_WHEN,
     "xsl:choose \u9700\u8981\u6709 xsl:when"},

    { ER_NO_APPLY_IMPORT_IN_FOR_EACH,
      "xsl:apply-imports \u4e0d\u5bb9\u8a31\u51fa\u73fe\u5728 xsl:for-each \u4e2d"},

    { ER_CANT_USE_DTM_FOR_OUTPUT,
      "\u7121\u6cd5\u4f7f\u7528\u8f38\u51fa DOM \u7bc0\u9ede\u7684 DTMLiaison ... \u6539\u50b3\u905e org.apache.xpath.DOM2Helper\uff01"},

    { ER_CANT_USE_DTM_FOR_INPUT,
      "\u7121\u6cd5\u4f7f\u7528\u8f38\u5165 DOM \u7bc0\u9ede\u7684 DTMLiaison ... \u6539\u50b3\u905e org.apache.xpath.DOM2Helper\uff01"},

    { ER_CALL_TO_EXT_FAILED,
      "\u547c\u53eb\u5ef6\u4f38\u9805\u76ee\u5143\u7d20\u5931\u6557\uff1a{0}"},

    { ER_PREFIX_MUST_RESOLVE,
      "\u5b57\u9996\u5fc5\u9808\u89e3\u6790\u70ba\u540d\u7a31\u7a7a\u9593\uff1a{0}"},

    { ER_INVALID_UTF16_SURROGATE,
      "\u5075\u6e2c\u5230\u7121\u6548\u7684 UTF-16 \u4ee3\u7406\uff1a{0}?"},

    { ER_XSLATTRSET_USED_ITSELF,
      "xsl:attribute-set {0} \u81ea\u6211\u4f7f\u7528\uff0c\u5c07\u9020\u6210\u7121\u9650\u8ff4\u5708\u3002"},

    { ER_CANNOT_MIX_XERCESDOM,
      "\u7121\u6cd5\u6df7\u5408\u975e Xerces-DOM \u8f38\u5165\u8207 Xerces-DOM \u8f38\u51fa\uff01"},

    { ER_TOO_MANY_LISTENERS,
      "addTraceListenersToStylesheet - TooManyListenersException"},

    { ER_IN_ELEMTEMPLATEELEM_READOBJECT,
      "\u4f4d\u65bc ElemTemplateElement.readObject\uff1a{0}"},

    { ER_DUPLICATE_NAMED_TEMPLATE,
      "\u627e\u5230\u4e0d\u6b62\u4e00\u500b\u540d\u7a31\u70ba\uff1a{0} \u7684\u7bc4\u672c"},

    { ER_INVALID_KEY_CALL,
      "\u7121\u6548\u7684\u51fd\u6578\u547c\u53eb\uff1a\u4e0d\u5141\u8a31 recursive key() \u547c\u53eb"},

    { ER_REFERENCING_ITSELF,
      "\u8b8a\u6578 {0} \u76f4\u63a5\u6216\u9593\u63a5\u53c3\u7167\u81ea\u5df1\uff01"},

    { ER_ILLEGAL_DOMSOURCE_INPUT,
      "\u5c0d newTemplates \u7684 DOMSource \u800c\u8a00\uff0c\u8f38\u5165\u7bc0\u9ede\u4e0d\u53ef\u70ba\u7a7a\u503c\uff01"},

    { ER_CLASS_NOT_FOUND_FOR_OPTION,
        "\u627e\u4e0d\u5230\u9078\u9805 {0} \u7684\u985e\u5225\u6a94\u6848"},

    { ER_REQUIRED_ELEM_NOT_FOUND,
        "\u627e\u4e0d\u5230\u5fc5\u8981\u7684\u5143\u7d20\uff1a{0}"},

    { ER_INPUT_CANNOT_BE_NULL,
        "InputStream \u4e0d\u53ef\u70ba\u7a7a\u503c"},

    { ER_URI_CANNOT_BE_NULL,
        "URI \u4e0d\u53ef\u70ba\u7a7a\u503c"},

    { ER_FILE_CANNOT_BE_NULL,
        "\u6a94\u6848\u4e0d\u53ef\u70ba\u7a7a\u503c"},

    { ER_SOURCE_CANNOT_BE_NULL,
                "InputSource \u4e0d\u53ef\u70ba\u7a7a\u503c"},

    { ER_CANNOT_INIT_BSFMGR,
                "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a BSF \u7ba1\u7406\u7a0b\u5f0f"},

    { ER_CANNOT_CMPL_EXTENSN,
                "\u7121\u6cd5\u7de8\u8b6f\u5ef6\u4f38\u9805\u76ee"},

    { ER_CANNOT_CREATE_EXTENSN,
      "\u7121\u6cd5\u5efa\u7acb\u5ef6\u4f38\u9805\u76ee\uff1a{0} \u56e0\u70ba\uff1a{1}"},

    { ER_INSTANCE_MTHD_CALL_REQUIRES,
      "\u547c\u53eb\u65b9\u6cd5 {0} \u7684\u5be6\u4f8b\u65b9\u6cd5\u9700\u8981\u7269\u4ef6\u5be6\u4f8b\u4f5c\u70ba\u7b2c\u4e00\u500b\u5f15\u6578"},

    { ER_INVALID_ELEMENT_NAME,
      "\u6307\u5b9a\u7121\u6548\u7684\u5143\u7d20\u540d\u7a31 {0}"},

    { ER_ELEMENT_NAME_METHOD_STATIC,
      "\u5143\u7d20\u540d\u7a31\u65b9\u6cd5\u5fc5\u9808\u662f\u975c\u614b {0}"},

    { ER_EXTENSION_FUNC_UNKNOWN,
             "\u5ef6\u4f38\u9805\u76ee\u51fd\u6578 {0} \uff1a {1} \u4e0d\u660e"},

    { ER_MORE_MATCH_CONSTRUCTOR,
             "{0} \u7684\u6700\u7b26\u5408\u5efa\u69cb\u5143\u4e0d\u6b62\u4e00\u500b"},

    { ER_MORE_MATCH_METHOD,
             "\u65b9\u6cd5 {0} \u7684\u6700\u7b26\u5408\u5efa\u69cb\u5143\u4e0d\u6b62\u4e00\u500b"},

    { ER_MORE_MATCH_ELEMENT,
             "\u5143\u7d20\u65b9\u6cd5 {0} \u7684\u6700\u7b26\u5408\u5efa\u69cb\u5143\u4e0d\u6b62\u4e00\u500b"},

    { ER_INVALID_CONTEXT_PASSED,
             "\u50b3\u905e\u5230\u8a55\u4f30 {0} \u7684\u74b0\u5883\u5b9a\u7fa9\u7121\u6548"},

    { ER_POOL_EXISTS,
             "\u5132\u5b58\u6c60\u5df2\u5b58\u5728"},

    { ER_NO_DRIVER_NAME,
             "\u672a\u6307\u5b9a\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31"},

    { ER_NO_URL,
             "\u672a\u6307\u5b9a URL"},

    { ER_POOL_SIZE_LESSTHAN_ONE,
             "\u5132\u5b58\u6c60\u5927\u5c0f\u5c0f\u65bc 1\uff01"},

    { ER_INVALID_DRIVER,
             "\u6307\u5b9a\u7684\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\u7121\u6548\uff01"},

    { ER_NO_STYLESHEETROOT,
             "\u627e\u4e0d\u5230\u6a23\u5f0f\u8868\u6839\u76ee\u9304\uff01"},

    { ER_ILLEGAL_XMLSPACE_VALUE,
         "xml:space \u7684\u503c\u4e0d\u5408\u6cd5"},

    { ER_PROCESSFROMNODE_FAILED,
         "processFromNode \u5931\u6557"},

    { ER_RESOURCE_COULD_NOT_LOAD,
        "\u7121\u6cd5\u8f09\u5165\u8cc7\u6e90 [ {0} ]\uff1a{1} \n {2} \t {3}"},

    { ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "\u7de9\u885d\u5340\u5927\u5c0f <=0"},

    { ER_UNKNOWN_ERROR_CALLING_EXTENSION,
        "\u547c\u53eb\u5ef6\u4f38\u9805\u76ee\u6642\u767c\u751f\u4e0d\u660e\u932f\u8aa4"},

    { ER_NO_NAMESPACE_DECL,
        "\u5b57\u9996 {0} \u6c92\u6709\u5c0d\u61c9\u7684\u540d\u7a31\u7a7a\u9593\u5ba3\u544a"},

    { ER_ELEM_CONTENT_NOT_ALLOWED,
        "lang=javaclass {0} \u4e0d\u5141\u8a31\u5143\u7d20\u5167\u5bb9"},

    { ER_STYLESHEET_DIRECTED_TERMINATION,
        "\u6a23\u5f0f\u8868\u5c0e\u5411\u7d42\u6b62"},

    { ER_ONE_OR_TWO,
        "1 \u6216 2"},

    { ER_TWO_OR_THREE,
        "2 \u6216 3"},

    { ER_COULD_NOT_LOAD_RESOURCE,
        "\u7121\u6cd5\u8f09\u5165 {0}\uff08\u6aa2\u67e5 CLASSPATH\uff09\uff0c\u73fe\u5728\u53ea\u4f7f\u7528\u9810\u8a2d\u503c"},

    { ER_CANNOT_INIT_DEFAULT_TEMPLATES,
        "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u9810\u8a2d\u7bc4\u672c"},

    { ER_RESULT_NULL,
        "\u7d50\u679c\u4e0d\u61c9\u70ba\u7a7a\u503c"},

    { ER_RESULT_COULD_NOT_BE_SET,
        "\u7121\u6cd5\u8a2d\u5b9a\u7d50\u679c"},

    { ER_NO_OUTPUT_SPECIFIED,
        "\u6c92\u6709\u6307\u5b9a\u8f38\u51fa"},

    { ER_CANNOT_TRANSFORM_TO_RESULT_TYPE,
        "\u7121\u6cd5\u8f49\u63db\u6210\u985e\u578b {0} \u7684\u7d50\u679c"},

    { ER_CANNOT_TRANSFORM_SOURCE_TYPE,
        "\u7121\u6cd5\u8f49\u63db\u985e\u578b {0} \u7684\u539f\u59cb\u6a94"},

    { ER_NULL_CONTENT_HANDLER,
        "\u7a7a\u503c\u5167\u5bb9\u8655\u7406\u7a0b\u5f0f"},

    { ER_NULL_ERROR_HANDLER,
        "\u7a7a\u503c\u932f\u8aa4\u8655\u7406\u7a0b\u5f0f"},

    { ER_CANNOT_CALL_PARSE,
        "\u5982\u679c\u672a\u8a2d\u5b9a ContentHandler \u5247\u7121\u6cd5\u547c\u53eb parse"},

    { ER_NO_PARENT_FOR_FILTER,
        "\u904e\u6ffe\u5668\u6c92\u6709\u6bcd\u9805"},

    { ER_NO_STYLESHEET_IN_MEDIA,
         "\u5728\uff1a{0}\uff0cmedia= {1} \u4e2d\u6c92\u6709\u6a23\u5f0f\u8868"},

    { ER_NO_STYLESHEET_PI,
         "\u5728\uff1a{0} \u4e2d\u627e\u4e0d\u5230 xml-stylesheet PI"},

    { ER_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\uff1a{0}"},

    { ER_PROPERTY_VALUE_BOOLEAN,
       "\u5167\u5bb9 {0} \u7684\u503c\u61c9\u70ba Boolean \u5be6\u4f8b"},

    { ER_COULD_NOT_FIND_EXTERN_SCRIPT,
         "\u7121\u6cd5\u5728 {0} \u53d6\u5f97\u5916\u90e8 Script"},

    { ER_RESOURCE_COULD_NOT_FIND,
        "\u627e\u4e0d\u5230\u8cc7\u6e90 [ {0} ]\u3002\n {1}"},

    { ER_OUTPUT_PROPERTY_NOT_RECOGNIZED,
        "\u672a\u80fd\u8fa8\u8b58\u8f38\u51fa\u5167\u5bb9\uff1a{0}"},

    { ER_FAILED_CREATING_ELEMLITRSLT,
        "\u5efa\u7acb ElemLiteralResult \u5be6\u4f8b\u5931\u6557"},

  //Earlier (JDK 1.4 XALAN 2.2-D11) at key code '204' the key name was ER_PRIORITY_NOT_PARSABLE
  // In latest Xalan code base key name is  ER_VALUE_SHOULD_BE_NUMBER. This should also be taken care
  //in locale specific files like XSLTErrorResources_de.java, XSLTErrorResources_fr.java etc.
  //NOTE: Not only the key name but message has also been changed.

    { ER_VALUE_SHOULD_BE_NUMBER,
        "{0} \u7684\u503c\u61c9\u8a72\u5305\u542b\u53ef\u5256\u6790\u7684\u6578\u5b57"},

    { ER_VALUE_SHOULD_EQUAL,
        "{0} \u7684\u503c\u61c9\u7b49\u65bc yes \u6216 no"},

    { ER_FAILED_CALLING_METHOD,
        "\u547c\u53eb {0} \u65b9\u6cd5\u5931\u6557"},

    { ER_FAILED_CREATING_ELEMTMPL,
        "\u5efa\u7acb ElemTemplateElement \u5be6\u4f8b\u5931\u6557"},

    { ER_CHARS_NOT_ALLOWED,
        "\u6587\u4ef6\u6b64\u9ede\u4e0d\u5141\u8a31\u5b57\u5143"},

    { ER_ATTR_NOT_ALLOWED,
        "\"{0}\" \u5c6c\u6027\u5728 {1} \u5143\u7d20\u4e0a\u4e0d\u5141\u8a31\uff01"},

    { ER_BAD_VALUE,
     "{0} \u4e0d\u6b63\u78ba\u7684\u503c {1}"},

    { ER_ATTRIB_VALUE_NOT_FOUND,
     "\u627e\u4e0d\u5230 {0} \u5c6c\u6027\u503c"},

    { ER_ATTRIB_VALUE_NOT_RECOGNIZED,
     "\u4e0d\u80fd\u8fa8\u8b58 {0} \u5c6c\u6027\u503c"},

    { ER_NULL_URI_NAMESPACE,
     "\u5617\u8a66\u7528\u7a7a\u503c URI \u7522\u751f\u540d\u7a31\u7a7a\u9593\u5b57\u9996"},

  //New ERROR keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    { ER_NUMBER_TOO_BIG,
     "\u5617\u8a66\u683c\u5f0f\u5316\u5927\u65bc\u6700\u5927\u9577\u6574\u6578 (Long integer) \u7684\u6578\u5b57"},

    { ER_CANNOT_FIND_SAX1_DRIVER,
     "\u627e\u4e0d\u5230 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}"},

    { ER_SAX1_DRIVER_NOT_LOADED,
     "\u627e\u5230 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}\uff0c\u4f46\u7121\u6cd5\u8f09\u5165"},

    { ER_SAX1_DRIVER_NOT_INSTANTIATED,
     "\u5df2\u8f09\u5165 SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0}\uff0c\u4f46\u7121\u6cd5\u5be6\u4f8b\u5316"},

    { ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER,
     "SAX1 \u9a45\u52d5\u7a0b\u5f0f\u985e\u5225 {0} \u4e0d\u80fd\u5728 org.xml.sax.Parser \u5be6\u4f5c"},

    { ER_PARSER_PROPERTY_NOT_SPECIFIED,
     "\u7121\u6cd5\u6307\u5b9a\u7cfb\u7d71\u5167\u5bb9 org.xml.sax.parser"},

    { ER_PARSER_ARG_CANNOT_BE_NULL,
     "\u5256\u6790\u5668\u5f15\u6578\u4e0d\u53ef\u70ba\u7a7a\u503c"},

    { ER_FEATURE,
     "\u529f\u80fd\uff1a{0}"},

    { ER_PROPERTY,
     "\u5167\u5bb9\uff1a{0}"},

    { ER_NULL_ENTITY_RESOLVER,
     "\u7a7a\u503c\u5be6\u9ad4\u89e3\u6790\u5668"},

    { ER_NULL_DTD_HANDLER,
     "\u7a7a\u503c DTD \u8655\u7406\u7a0b\u5f0f"},

    { ER_NO_DRIVER_NAME_SPECIFIED,
     "\u672a\u6307\u5b9a\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\uff01"},

    { ER_NO_URL_SPECIFIED,
     "\u672a\u6307\u5b9a URL\uff01"},

    { ER_POOLSIZE_LESS_THAN_ONE,
     "\u5132\u5b58\u6c60\u5c0f\u65bc 1\uff01"},

    { ER_INVALID_DRIVER_NAME,
     "\u6307\u5b9a\u7684\u9a45\u52d5\u7a0b\u5f0f\u540d\u7a31\u7121\u6548\uff01"},

    { ER_ERRORLISTENER,
     "ErrorListener"},


// Note to translators:  The following message should not normally be displayed
//   to users.  It describes a situation in which the processor has detected
//   an internal consistency problem in itself, and it provides this message
//   for the developer to help diagnose the problem.  The name
//   'ElemTemplateElement' is the name of a class, and should not be
//   translated.
    { ER_ASSERT_NO_TEMPLATE_PARENT,
     "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u7684\u932f\u8aa4\uff01\u8868\u793a\u5f0f\u6c92\u6709 ElemTemplateElement \u6bcd\u9805\uff01"},


// Note to translators:  The following message should not normally be displayed
//   to users.  It describes a situation in which the processor has detected
//   an internal consistency problem in itself, and it provides this message
//   for the developer to help diagnose the problem.  The substitution text
//   provides further information in order to diagnose the problem.  The name
//   'RedundentExprEliminator' is the name of a class, and should not be
//   translated.
    { ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR,
     "\u7a0b\u5f0f\u8a2d\u8a08\u5e2b\u5728 RedundentExprEliminator \u4e2d\u7684\u78ba\u8a8d\uff1a{0}"},

    { ER_NOT_ALLOWED_IN_POSITION,
     "\u5728\u6b64\u6a23\u5f0f\u8868\u4e2d\uff0c\u6b64\u4f4d\u7f6e\u4e0d\u53ef\u4ee5\u662f {0}\u3002"},

    { ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION,
     "\u5728\u6b64\u6a23\u5f0f\u8868\u4e2d\uff0c\u6b64\u4f4d\u7f6e\u4e0d\u53ef\u4ee5\u662f\u975e\u7a7a\u767d\u5b57\u5143\uff01"},

  // This code is shared with warning codes.
  // SystemId Unknown
    { INVALID_TCHAR,
     "CHAR \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5\u3002CHAR \u985e\u578b\u7684\u5c6c\u6027\u53ea\u80fd\u6709\u4e00\u500b\u5b57\u5143\uff01"},

    // Note to translators:  The following message is used if the value of
    // an attribute in a stylesheet is invalid.  "QNAME" is the XML data-type of
    // the attribute, and should not be translated.  The substitution text {1} is
    // the attribute value and {0} is the attribute name.
    //The following codes are shared with the warning codes...
    { INVALID_QNAME,
     "QNAME \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5"},

    // Note to translators:  The following message is used if the value of
    // an attribute in a stylesheet is invalid.  "ENUM" is the XML data-type of
    // the attribute, and should not be translated.  The substitution text {1} is
    // the attribute value, {0} is the attribute name, and {2} is a list of valid
    // values.
    { INVALID_ENUM,
     "ENUM \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5\u3002\u6709\u6548\u7684\u503c\u70ba\uff1a{2}\u3002"},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NMTOKEN" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
    { INVALID_NMTOKEN,
     "NMTOKEN \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5"},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "NCNAME" is the XML data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
    { INVALID_NCNAME,
     "NCNAME \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5"},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "boolean" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
    { INVALID_BOOLEAN,
     "Boolean \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5"},

// Note to translators:  The following message is used if the value of
// an attribute in a stylesheet is invalid.  "number" is the XSLT data-type
// of the attribute, and should not be translated.  The substitution text {1} is
// the attribute value and {0} is the attribute name.
     { INVALID_NUMBER,
     "Number \u5c6c\u6027\uff1a{0} \u4f7f\u7528\u7684\u503c\uff1a{1} \u4e0d\u5408\u6cd5"},


  // End of shared codes...

// Note to translators:  A "match pattern" is a special form of XPath expression
// that is used for matching patterns.  The substitution text is the name of
// a function.  The message indicates that when this function is referenced in
// a match pattern, its argument must be a string literal (or constant.)
// ER_ARG_LITERAL - new error message for bugzilla //5202
    { ER_ARG_LITERAL,
     "\u6bd4\u5c0d\u578b\u6a23\u4e2d\u7684 ''''{0}'''' \u7684\u5f15\u6578\u5fc5\u9808\u662f\u6587\u5b57\u3002"},

// Note to translators:  The following message indicates that two definitions of
// a variable.  A "global variable" is a variable that is accessible everywher
// in the stylesheet.
// ER_DUPLICATE_GLOBAL_VAR - new error message for bugzilla #790
    { ER_DUPLICATE_GLOBAL_VAR,
     "\u5ee3\u57df\u8b8a\u6578\u5ba3\u544a\u91cd\u8907\u3002"},


// Note to translators:  The following message indicates that two definitions of
// a variable were encountered.
// ER_DUPLICATE_VAR - new error message for bugzilla #790
    { ER_DUPLICATE_VAR,
     "\u8b8a\u6578\u5ba3\u544a\u91cd\u8907\u3002"},

    // Note to translators:  "xsl:template, "name" and "match" are XSLT keywords
    // which must not be translated.
    // ER_TEMPLATE_NAME_MATCH - new error message for bugzilla #789
    { ER_TEMPLATE_NAME_MATCH,
     "xsl:template \u5fc5\u9808\u6709\u540d\u7a31\u6216\u76f8\u7b26\u7684\u5c6c\u6027\uff08\u6216\u5169\u8005\uff09"},

    // Note to translators:  "exclude-result-prefixes" is an XSLT keyword which
    // should not be translated.  The message indicates that a namespace prefix
    // encountered as part of the value of the exclude-result-prefixes attribute
    // was in error.
    // ER_INVALID_PREFIX - new error message for bugzilla #788
    { ER_INVALID_PREFIX,
     "exclude-result-prefixes \u4e2d\u7684\u5b57\u9996\u7121\u6548\uff1a{0}"},

    // Note to translators:  An "attribute set" is a set of attributes that can
    // be added to an element in the output document as a group.  The message
    // indicates that there was a reference to an attribute set named {0} that
    // was never defined.
    // ER_NO_ATTRIB_SET - new error message for bugzilla #782
    { ER_NO_ATTRIB_SET,
     "attribute-set \u540d\u7a31 {0} \u4e0d\u5b58\u5728"},

    // Note to translators:  This message indicates that there was a reference
    // to a function named {0} for which no function definition could be found.
    { ER_FUNCTION_NOT_FOUND,
     "\u51fd\u6578\u540d\u70ba {0} \u4e0d\u5b58\u5728"},

    // Note to translators:  This message indicates that the XSLT instruction
    // that is named by the substitution text {0} must not contain other XSLT
    // instructions (content) or a "select" attribute.  The word "select" is
    // an XSLT keyword in this case and must not be translated.
    { ER_CANT_HAVE_CONTENT_AND_SELECT,
     "{0} \u5143\u7d20\u4e0d\u5f97\u540c\u6642\u6709\u5167\u5bb9\u548c select \u5c6c\u6027\u3002"},

    // Note to translators:  This message indicates that the value argument
    // of setParameter must be a valid Java Object.
    { ER_INVALID_SET_PARAM_VALUE,
     "\u53c3\u6578 {0} \u7684\u503c\u5fc5\u9808\u662f\u6709\u6548\u7684 Java \u7269\u4ef6"},

        { ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX_FOR_DEFAULT,
         "\u4e00\u500b xsl:namespace-alias \u5143\u7d20\u7684 result-prefix \u5c6c\u6027\u6709\u503c '#default'\uff0c\u4f46\u5728\u8a72\u5143\u7d20\u7684\u7bc4\u570d\u4e2d\u4e26\u6c92\u6709\u9810\u8a2d\u540d\u7a31\u7a7a\u9593\u7684\u5ba3\u544a"},

        { ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX,
         "\u4e00\u500b xsl:namespace-alias \u5143\u7d20\u7684 result-prefix \u5c6c\u6027\u6709\u503c ''{0}''\uff0c\u4f46\u5728\u8a72\u5143\u7d20\u7684\u7bc4\u570d\u4e2d\u4e26\u6c92\u6709\u5b57\u9996 ''{0}'' \u7684\u540d\u7a31\u7a7a\u9593\u5ba3\u544a\u3002"},

    { ER_SET_FEATURE_NULL_NAME,
      "\u7279\u6027\u540d\u7a31\u5728 TransformerFactory.setFeature(\u5b57\u4e32\u540d\u7a31\u3001boolean \u503c) \u4e2d\u4e0d\u53ef\u662f\u7a7a\u503c\u3002"},

    { ER_GET_FEATURE_NULL_NAME,
      "\u7279\u6027\u540d\u7a31\u5728 TransformerFactory.getFeature(\u5b57\u4e32\u540d\u7a31) \u4e2d\u4e0d\u53ef\u662f\u7a7a\u503c\u3002"},

    { ER_UNSUPPORTED_FEATURE,
      "\u7121\u6cd5\u5728\u9019\u500b TransformerFactory \u8a2d\u5b9a\u7279\u6027 ''{0}''\u3002"},

    { ER_EXTENSION_ELEMENT_NOT_ALLOWED_IN_SECURE_PROCESSING,
        "\u7576\u5b89\u5168\u8655\u7406\u7279\u6027\u8a2d\u70ba true \u6642\uff0c\u4e0d\u63a5\u53d7\u4f7f\u7528\u5ef6\u4f38\u5143\u7d20 ''{0}''\u3002"},

        { ER_NAMESPACE_CONTEXT_NULL_NAMESPACE,
          "\u7121\u6cd5\u53d6\u5f97\u7a7a\u503c\u540d\u7a31\u7a7a\u9593 uri \u7684\u5b57\u9996\u3002"},

        { ER_NAMESPACE_CONTEXT_NULL_PREFIX,
          "\u7121\u6cd5\u53d6\u5f97\u7a7a\u503c\u5b57\u9996\u7684\u540d\u7a31\u7a7a\u9593 uri\u3002"},

        { ER_XPATH_RESOLVER_NULL_QNAME,
          "\u51fd\u6578\u540d\u7a31\u4e0d\u53ef\u70ba\u7a7a\u503c\u3002"},

        { ER_XPATH_RESOLVER_NEGATIVE_ARITY,
          "Arity \u4e0d\u53ef\u70ba\u8ca0\u503c\u3002"},

  // Warnings...

    { WG_FOUND_CURLYBRACE,
      "\u627e\u5230 '}' \u4f46\u6c92\u6709\u958b\u555f\u5c6c\u6027\u7bc4\u672c\uff01"},

    { WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR,
      "\u8b66\u544a\uff1acount \u5c6c\u6027\u4e0d\u7b26\u5408 xsl:number \u4e2d\u7684\u88ab\u7e7c\u627f\u8005\uff01\u76ee\u6a19 = {0}"},

    { WG_EXPR_ATTRIB_CHANGED_TO_SELECT,
      "\u820a\u8a9e\u6cd5\uff1a'expr' \u5c6c\u6027\u7684\u540d\u7a31\u5df2\u8b8a\u66f4\u70ba 'select'\u3002"},

    { WG_NO_LOCALE_IN_FORMATNUMBER,
      "Xalan \u5c1a\u672a\u8655\u7406 format-number \u51fd\u6578\u4e2d\u7684\u8a9e\u8a00\u74b0\u5883\u540d\u7a31\u3002"},

    { WG_LOCALE_NOT_FOUND,
      "\u8b66\u544a\uff1a\u627e\u4e0d\u5230 xml:lang={0} \u7684\u8a9e\u8a00\u74b0\u5883"},

    { WG_CANNOT_MAKE_URL_FROM,
      "\u7121\u6cd5\u5f9e\uff1a{0} \u7522\u751f URL"},

    { WG_CANNOT_LOAD_REQUESTED_DOC,
      "\u7121\u6cd5\u8f09\u5165\u6240\u8981\u6c42\u7684\u6587\u4ef6\uff1a{0}"},

    { WG_CANNOT_FIND_COLLATOR,
      "\u627e\u4e0d\u5230 <sort xml:lang={0} \u7684\u7406\u5e8f\u5668"},

    { WG_FUNCTIONS_SHOULD_USE_URL,
      "\u820a\u8a9e\u6cd5\uff1a\u51fd\u6578\u6307\u4ee4\u61c9\u4f7f\u7528 {0} \u7684 URL"},

    { WG_ENCODING_NOT_SUPPORTED_USING_UTF8,
      "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}\uff0c\u8acb\u4f7f\u7528 UTF-8"},

    { WG_ENCODING_NOT_SUPPORTED_USING_JAVA,
      "\u4e0d\u652f\u63f4\u7de8\u78bc\uff1a{0}\uff0c\u8acb\u4f7f\u7528 Java {1}"},

    { WG_SPECIFICITY_CONFLICTS,
      "\u627e\u5230\u7279\u5b9a\u885d\u7a81\uff1a{0} \u5c07\u4f7f\u7528\u5728\u6a23\u5f0f\u8868\u4e2d\u627e\u5230\u7684\u6700\u5f8c\u4e00\u500b\u3002"},

    { WG_PARSING_AND_PREPARING,
      "========= \u5256\u6790\u8207\u6e96\u5099 {0} =========="},

    { WG_ATTR_TEMPLATE,
     "\u5c6c\u6027\u7bc4\u672c\uff0c{0}"},

    { WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE,
      "\u5728 xsl:strip-space \u548c xsl:preserve-space \u4e4b\u9593\u6709\u6bd4\u5c0d\u885d\u7a81"},

    { WG_ATTRIB_NOT_HANDLED,
      "Xalan \u5c1a\u672a\u8655\u7406 {0} \u5c6c\u6027\uff01"},

    { WG_NO_DECIMALFORMAT_DECLARATION,
      "\u627e\u4e0d\u5230\u5341\u9032\u4f4d\u683c\u5f0f\u7684\u5ba3\u544a\uff1a{0}"},

    { WG_OLD_XSLT_NS,
     "XSLT \u540d\u7a31\u7a7a\u9593\u907a\u6f0f\u6216\u4e0d\u6b63\u78ba\u3002"},

    { WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED,
      "\u50c5\u5141\u8a31\u4e00\u500b\u9810\u8a2d xsl:decimal-format \u5ba3\u544a\u3002"},

    { WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE,
      "xsl:decimal-format \u540d\u7a31\u5fc5\u9808\u662f\u552f\u4e00\u7684\u3002\u540d\u7a31 \"{0}\" \u5df2\u91cd\u8907\u3002"},

    { WG_ILLEGAL_ATTRIBUTE,
      "{0} \u542b\u6709\u4e0d\u5408\u6cd5\u7684\u5c6c\u6027\uff1a{1}"},

    { WG_COULD_NOT_RESOLVE_PREFIX,
      "\u7121\u6cd5\u89e3\u6790\u540d\u7a31\u7a7a\u9593\u5b57\u9996\uff1a{0}\u3002\u7bc0\u9ede\u5c07\u88ab\u5ffd\u7565\u3002"},

    { WG_STYLESHEET_REQUIRES_VERSION_ATTRIB,
      "xsl:stylesheet \u9700\u8981 'version' \u5c6c\u6027\uff01"},

    { WG_ILLEGAL_ATTRIBUTE_NAME,
      "\u4e0d\u5408\u6cd5\u5c6c\u6027\u540d\u7a31\uff1a{0}"},

    { WG_ILLEGAL_ATTRIBUTE_VALUE,
      "\u5c6c\u6027 {0} \u4f7f\u7528\u4e86\u4e0d\u5408\u6cd5\u503c\uff1a{1}"},

    { WG_EMPTY_SECOND_ARG,
      "\u5f9e\u6587\u4ef6\u51fd\u6578\u7b2c\u4e8c\u500b\u5f15\u6578\u7522\u751f\u7684\u7bc0\u9ede\u96c6\u662f\u7a7a\u503c\u3002\u50b3\u56de\u7a7a\u7684\u7bc0\u9ede\u96c6\u3002"},

  //Following are the new WARNING keys added in XALAN code base after Jdk 1.4 (Xalan 2.2-D11)

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.
    { WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML,
      "xsl:processing-instruction \u540d\u7a31\u7684 'name' \u5c6c\u6027\u503c\u4e0d\u53ef\u4ee5\u662f 'xml'"},

    // Note to translators:  "name" and "xsl:processing-instruction" are keywords
    // and must not be translated.  "NCName" is an XML data-type and must not be
    // translated.
    { WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME,
      "xsl:processing-instruction \u7684 ''name'' \u5c6c\u6027\u503c\u5fc5\u9808\u662f\u6709\u6548\u7684 NCName\uff1a{0}"},

    // Note to translators:  This message is reported if the stylesheet that is
    // being processed attempted to construct an XML document with an attribute in a
    // place other than on an element.  The substitution text specifies the name of
    // the attribute.
    { WG_ILLEGAL_ATTRIBUTE_POSITION,
      "\u5728\u7522\u751f\u5b50\u9805\u7bc0\u9ede\u4e4b\u5f8c\uff0c\u6216\u5728\u7522\u751f\u5143\u7d20\u4e4b\u524d\uff0c\u4e0d\u53ef\u65b0\u589e\u5c6c\u6027 {0}\u3002\u5c6c\u6027\u6703\u88ab\u5ffd\u7565\u3002"},

    { NO_MODIFICATION_ALLOWED_ERR,
      "\u5617\u8a66\u4fee\u6539\u4e0d\u63a5\u53d7\u4fee\u6539\u7684\u7269\u4ef6\u3002"
    },

    //Check: WHY THERE IS A GAP B/W NUMBERS in the XSLTErrorResources properties file?

  // Other miscellaneous text used inside the code...
  { "ui_language", "zh"},
  {  "help_language",  "zh" },
  {  "language",  "zh" },
  { "BAD_CODE", "createMessage \u7684\u53c3\u6578\u8d85\u51fa\u754c\u9650"},
  {  "FORMAT_FAILED", "\u5728 messageFormat \u547c\u53eb\u671f\u9593\u64f2\u51fa\u7570\u5e38"},
  {  "version", ">>>>>>> Xalan \u7248\u672c"},
  {  "version2",  "<<<<<<<"},
  {  "yes", "yes"},
  { "line", "\u884c\u865f"},
  { "column","\u6b04\u865f"},
  { "xsldone", "XSLProcessor\uff1a\u5b8c\u6210"},


  // Note to translators:  The following messages provide usage information
  // for the Xalan Process command line.  "Process" is the name of a Java class,
  // and should not be translated.
  { "xslProc_option", "Xalan-J \u6307\u4ee4\u884c Process \u985e\u5225\u9078\u9805\uff1a"},
  { "xslProc_option", "Xalan-J \u6307\u4ee4\u884c Process \u985e\u5225\u9078\u9805\u003a"},
  { "xslProc_invalid_xsltc_option", "XSLTC \u6a21\u5f0f\u4e0d\u652f\u63f4\u9078\u9805 {0}\u3002"},
  { "xslProc_invalid_xalan_option", "\u9078\u9805 {0} \u53ea\u80fd\u548c -XSLTC \u4e00\u8d77\u4f7f\u7528\u3002"},
  { "xslProc_no_input", "\u932f\u8aa4\uff1a\u672a\u6307\u5b9a\u6a23\u5f0f\u8868\u6216\u8f38\u5165 xml\u3002\u57f7\u884c\u6b64\u6307\u4ee4\u6642\u4e0d\u8981\u5305\u542b\u4efb\u4f55\u9078\u9805\uff0c\u5373\u53ef\u53d6\u5f97\u7528\u6cd5\u6307\u793a\u3002"},
  { "xslProc_common_options", "-\u4e00\u822c\u9078\u9805-"},
  { "xslProc_xalan_options", "-Xalan \u7684\u9078\u9805-"},
  { "xslProc_xsltc_options", "-XSLTC \u7684\u9078\u9805-"},
  { "xslProc_return_to_continue", "(\u6309 <return> \u7e7c\u7e8c)"},

   // Note to translators: The option name and the parameter name do not need to
   // be translated. Only translate the messages in parentheses.  Note also that
   // leading whitespace in the messages is used to indent the usage information
   // for each option in the English messages.
   // Do not translate the keywords: XSLTC, SAX, DOM and DTM.
  { "optionXSLTC", "[-XSLTC (\u4f7f\u7528 XSLTC \u9032\u884c\u8f49\u63db)]"},
  { "optionIN", "[-IN inputXMLURL]"},
  { "optionXSL", "[-XSL XSLTransformationURL]"},
  { "optionOUT",  "[-OUT outputFileName]"},
  { "optionLXCIN", "[-LXCIN compiledStylesheetFileNameIn]"},
  { "optionLXCOUT", "[-LXCOUT compiledStylesheetFileNameOutOut]"},
  { "optionPARSER", "[-PARSER fully qualified class name of parser liaison]"},
  {  "optionE", "   [-E\uff08\u4e0d\u5c55\u958b\u5be6\u9ad4\u53c3\u7167\uff09]"},
  {  "optionV",  "   [-E\uff08\u4e0d\u5c55\u958b\u5be6\u9ad4\u53c3\u7167\uff09]"},
  {  "optionQC", "   [-QC\uff08\u7121\u8072\u578b\u6a23\u885d\u7a81\u8b66\u544a\uff09]"},
  {  "optionQ", "   [-Q \uff08\u7121\u8072\u6a21\u5f0f\uff09]"},
  {  "optionLF", "   [-LF\uff08\u53ea\u5728\u8f38\u51fa\u4e0a\u4f7f\u7528\u8f38\u51fa {\u9810\u8a2d\u662f CR/LF}\uff09]"},
  {  "optionCR", "   [-LF\uff08\u53ea\u5728\u8f38\u51fa\u4e0a\u4f7f\u7528\u56de\u8eca {\u9810\u8a2d\u662f CR/LF}\uff09]"},
  { "optionESCAPE", "[-ESCAPE\uff08\u8981\u8df3\u51fa\u7684\u5b57\u5143 {\u9810\u8a2d\u662f <>&\"\'\\r\\n}]"},
  { "optionINDENT", "[-INDENT\uff08\u63a7\u5236\u8981\u5167\u7e2e\u7684\u7a7a\u683c\u6578 {\u9810\u8a2d\u662f 0}\uff09]"},
  { "optionTT", "   [-TT\uff08\u5728\u88ab\u547c\u53eb\u6642\u8ffd\u8e64\u7bc4\u672c\u3002\uff09]"},
  { "optionTG", "   [-TG\uff08\u8ffd\u8e64\u6bcf\u4e00\u500b\u7522\u751f\u4e8b\u4ef6\u3002\uff09]"},
  { "optionTS", "   [-TS\uff08\u8ffd\u8e64\u6bcf\u4e00\u500b\u9078\u53d6\u4e8b\u4ef6\u3002\uff09]"},
  {  "optionTTC", "   [-TTC\uff08\u5728\u88ab\u8655\u7406\u6642\u8ffd\u8e64\u7bc4\u672c\u5b50\u9805\u5143\u4ef6\u3002\uff09]"},
  { "optionTCLASS", "   [-TCLASS\uff08\u8ffd\u8e64\u5ef6\u4f38\u9805\u76ee\u7684 TraceListener \u985e\u5225\u3002\uff09]"},
  { "optionVALIDATE", "[-VALIDATE\uff08\u8a2d\u5b9a\u662f\u5426\u767c\u751f\u9a57\u8b49\u3002\u4f9d\u9810\u8a2d\u9a57\u8b49\u662f\u95dc\u9589\u7684\u3002\uff09]"},
  { "optionEDUMP", "[-EDUMP {\u9078\u7528\u7684\u6a94\u6848\u540d\u7a31}\uff08\u767c\u751f\u932f\u8aa4\u6642\u57f7\u884c stackdump\uff09]"},
  {  "optionXML", "   [-XML\uff08\u4f7f\u7528 XML \u683c\u5f0f\u88fd\u4f5c\u5668\u53ca\u65b0\u589e XML \u6a19\u982d\u3002\uff09]"},
  {  "optionTEXT", "   [-TEXT\uff08\u4f7f\u7528\u7c21\u6613\u6587\u5b57\u683c\u5f0f\u5316\u7a0b\u5f0f\u3002\uff09]"},
  {  "optionHTML", "   [-HTML\uff08\u4f7f\u7528 HTML \u683c\u5f0f\u88fd\u4f5c\u5668\u3002\uff09]"},
  {  "optionPARAM", "   [-PARAM \u540d\u7a31\u8868\u793a\u5f0f\uff08\u8a2d\u5b9a\u6a23\u5f0f\u8868\u53c3\u6578\uff09]"},
  {  "noParsermsg1", "XSL \u7a0b\u5e8f\u6c92\u6709\u9806\u5229\u5b8c\u6210\u3002"},
  {  "noParsermsg2", "** \u627e\u4e0d\u5230\u5256\u6790\u5668 **"},
  { "noParsermsg3",  "\u8acb\u6aa2\u67e5\u985e\u5225\u8def\u5f91\u3002"},
  { "noParsermsg4", "\u5982\u679c\u60a8\u6c92\u6709 IBM \u7684 XML Parser for Java\uff0c\u53ef\u81ea\u4ee5\u4e0b\u7db2\u5740\u4e0b\u8f09"},
  { "noParsermsg5", "IBM \u7684 AlphaWorks\uff1ahttp://www.alphaworks.ibm.com/formula/xml"},
  { "optionURIRESOLVER", "[-URIRESOLVER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31\uff08URIResolver \u7528\u4f86\u89e3\u6790 URI\uff09]"},
  { "optionENTITYRESOLVER",  "[-ENTITYRESOLVER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31\uff08EntityResolver \u7528\u4f86\u89e3\u6790\u5be6\u9ad4\uff09]"},
  { "optionCONTENTHANDLER",  "[-CONTENTHANDLER \u5b8c\u6574\u7684\u985e\u5225\u540d\u7a31\uff08ContentHandler \u7528\u4f86\u5e8f\u5217\u5316\u8f38\u51fa\uff09]"},
  {  "optionLINENUMBERS",  "[-L \u4f7f\u7528\u539f\u59cb\u6587\u4ef6\u7684\u884c\u865f]"},
  { "optionSECUREPROCESSING", "   [-SECURE (\u5c07\u5b89\u5168\u8655\u7406\u7279\u6027\u8a2d\u70ba true\u3002)]"},

    // Following are the new options added in XSLTErrorResources.properties files after Jdk 1.4 (Xalan 2.2-D11)


  {  "optionMEDIA",  "   [-MEDIA mediaType\uff08\u4f7f\u7528\u5a92\u9ad4\u5c6c\u6027\u5c0b\u627e\u8207\u6587\u4ef6\u76f8\u95dc\u806f\u7684\u6a23\u5f0f\u8868\u3002\uff09]"},
  {  "optionFLAVOR",  "   [-FLAVOR flavorName\uff08\u660e\u78ba\u4f7f\u7528 s2s=SAX \u6216 d2d=DOM \u4f86\u57f7\u884c\u8f49\u63db\u3002\uff09] "}, // Added by sboag/scurcuru; experimental
  { "optionDIAG", "   [-DIAG (\u5217\u5370\u8f49\u63db\u82b1\u8cbb\u7684\u6beb\u79d2\u6578\u3002\uff09]"},
  { "optionINCREMENTAL",  "   [-INCREMENTAL\uff08\u8a2d\u5b9a http://xml.apache.org/xalan/features/incremental \u70ba true\uff0c\u8981\u6c42\u6f38\u9032\u5f0f DTM \u5efa\u69cb\u3002\uff09]"},
  {  "optionNOOPTIMIMIZE",  "   [-NOOPTIMIMIZE\uff08\u8a2d\u5b9a http://xml.apache.org/xalan/features/optimize \u70ba false\uff0c\u8981\u6c42\u4e0d\u9032\u884c\u6a23\u5f0f\u8868\u6700\u4f73\u5316\u8655\u7406\u7a0b\u5e8f\u3002)]"},
  { "optionRL",  "   [-RL recursionlimit\uff08\u4e3b\u5f35\u5c0d\u6a23\u5f0f\u8868\u905e\u8ff4\u6df1\u5ea6\u5be6\u65bd\u6578\u503c\u9650\u5236\u3002\uff09]"},
  {   "optionXO",  "[-XO [transletName] (\u6307\u5b9a\u540d\u7a31\u7d66\u7522\u751f\u7684 translet)]"},
  {  "optionXD", "[-XD destinationDirectory (\u6307\u5b9a translet \u7684\u76ee\u6a19\u76ee\u9304)]"},
  {  "optionXJ",  "[-XJ jarfile (\u5c07 translet \u985e\u5225\u5c01\u88dd\u5728\u6a94\u540d\u70ba <jarfile> \u7684 jar \u6a94\u6848\u4e2d)]"},
  {   "optionXP",  "[-XP package (\u6307\u5b9a\u6240\u7522\u751f\u7684\u6240\u6709 translet \u985e\u5225\u4e4b\u5957\u4ef6\u540d\u7a31\u5b57\u9996)]"},

  //AddITIONAL  STRINGS that need L10n
  // Note to translators:  The following message describes usage of a particular
  // command-line option that is used to enable the "template inlining"
  // optimization.  The optimization involves making a copy of the code
  // generated for a template in another template that refers to it.
  { "optionXN",  "[-XN (\u555f\u7528\u7bc4\u672c\u5217\u5165)]" },
  { "optionXX",  "[-XX (\u958b\u555f\u984d\u5916\u7684\u9664\u932f\u8a0a\u606f\u8f38\u51fa)]"},
  { "optionXT" , "[-XT (\u53ef\u80fd\u7684\u8a71\uff0c\u4f7f\u7528 translet \u9032\u884c\u8f49\u63db)]"},
  { "diagTiming","--------- \u900f\u904e {1} \u8017\u8cbb {2} \u6beb\u79d2\u8f49\u63db {0}" },
  { "recursionTooDeep","\u7bc4\u672c\u5de2\u72c0\u7d50\u69cb\u592a\u6df1\u3002\u5de2\u72c0 = {0}\uff0c\u7bc4\u672c {1} {2}" },
  { "nameIs", "\u540d\u7a31\u70ba" },
  { "matchPatternIs", "\u6bd4\u5c0d\u578b\u6a23\u70ba" }

  };
  }
  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "\u932f\u8aa4\uff1a";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "\u8b66\u544a\uff1a";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "PATTERN ";


  /**
   *   Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   *   of ResourceBundle.getBundle().
   *
   *   @param className the name of the class that implements the resource bundle.
   *   @return the ResourceBundle
   *   @throws MissingResourceException
   */
  public static final XSLTErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return (XSLTErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XSLTErrorResources) ResourceBundle.getBundle(className,
                new Locale("zh", "TW"));
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
