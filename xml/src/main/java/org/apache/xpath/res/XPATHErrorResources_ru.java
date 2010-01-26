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
 * $Id: XPATHErrorResources_ru.java 468655 2006-10-28 07:12:06Z minchau $
 */
package org.apache.xpath.res;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a Static string constant for the
 * Key and update the contents array with Key, Value pair
  * Also you need to  update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING) [ Information purpose only]
 * @xsl.usage advanced
 */
public class XPATHErrorResources_ru extends ListResourceBundle
{

/*
 * General notes to translators:
 *
 * This file contains error and warning messages related to XPath Error
 * Handling.
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
 *  8) The context node is the node in the document with respect to which an
 *     XPath expression is being evaluated.
 *
 *  9) An iterator is an object that traverses nodes in the tree, one at a time.
 *
 *  10) NCName is an XML term used to describe a name that does not contain a
 *     colon (a "no-colon name").
 *
 *  11) QName is an XML term meaning "qualified name".
 */

  /*
   * static variables
   */
  public static final String ERROR0000 = "ERROR0000";
  public static final String ER_CURRENT_NOT_ALLOWED_IN_MATCH =
         "ER_CURRENT_NOT_ALLOWED_IN_MATCH";
  public static final String ER_CURRENT_TAKES_NO_ARGS =
         "ER_CURRENT_TAKES_NO_ARGS";
  public static final String ER_DOCUMENT_REPLACED = "ER_DOCUMENT_REPLACED";
  public static final String ER_CONTEXT_HAS_NO_OWNERDOC =
         "ER_CONTEXT_HAS_NO_OWNERDOC";
  public static final String ER_LOCALNAME_HAS_TOO_MANY_ARGS =
         "ER_LOCALNAME_HAS_TOO_MANY_ARGS";
  public static final String ER_NAMESPACEURI_HAS_TOO_MANY_ARGS =
         "ER_NAMESPACEURI_HAS_TOO_MANY_ARGS";
  public static final String ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS =
         "ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS";
  public static final String ER_NUMBER_HAS_TOO_MANY_ARGS =
         "ER_NUMBER_HAS_TOO_MANY_ARGS";
  public static final String ER_NAME_HAS_TOO_MANY_ARGS =
         "ER_NAME_HAS_TOO_MANY_ARGS";
  public static final String ER_STRING_HAS_TOO_MANY_ARGS =
         "ER_STRING_HAS_TOO_MANY_ARGS";
  public static final String ER_STRINGLENGTH_HAS_TOO_MANY_ARGS =
         "ER_STRINGLENGTH_HAS_TOO_MANY_ARGS";
  public static final String ER_TRANSLATE_TAKES_3_ARGS =
         "ER_TRANSLATE_TAKES_3_ARGS";
  public static final String ER_UNPARSEDENTITYURI_TAKES_1_ARG =
         "ER_UNPARSEDENTITYURI_TAKES_1_ARG";
  public static final String ER_NAMESPACEAXIS_NOT_IMPLEMENTED =
         "ER_NAMESPACEAXIS_NOT_IMPLEMENTED";
  public static final String ER_UNKNOWN_AXIS = "ER_UNKNOWN_AXIS";
  public static final String ER_UNKNOWN_MATCH_OPERATION =
         "ER_UNKNOWN_MATCH_OPERATION";
  public static final String ER_INCORRECT_ARG_LENGTH ="ER_INCORRECT_ARG_LENGTH";
  public static final String ER_CANT_CONVERT_TO_NUMBER =
         "ER_CANT_CONVERT_TO_NUMBER";
  public static final String ER_CANT_CONVERT_XPATHRESULTTYPE_TO_NUMBER =
           "ER_CANT_CONVERT_XPATHRESULTTYPE_TO_NUMBER";
  public static final String ER_CANT_CONVERT_TO_NODELIST =
         "ER_CANT_CONVERT_TO_NODELIST";
  public static final String ER_CANT_CONVERT_TO_MUTABLENODELIST =
         "ER_CANT_CONVERT_TO_MUTABLENODELIST";
  public static final String ER_CANT_CONVERT_TO_TYPE ="ER_CANT_CONVERT_TO_TYPE";
  public static final String ER_EXPECTED_MATCH_PATTERN =
         "ER_EXPECTED_MATCH_PATTERN";
  public static final String ER_COULDNOT_GET_VAR_NAMED =
         "ER_COULDNOT_GET_VAR_NAMED";
  public static final String ER_UNKNOWN_OPCODE = "ER_UNKNOWN_OPCODE";
  public static final String ER_EXTRA_ILLEGAL_TOKENS ="ER_EXTRA_ILLEGAL_TOKENS";
  public static final String ER_EXPECTED_DOUBLE_QUOTE =
         "ER_EXPECTED_DOUBLE_QUOTE";
  public static final String ER_EXPECTED_SINGLE_QUOTE =
         "ER_EXPECTED_SINGLE_QUOTE";
  public static final String ER_EMPTY_EXPRESSION = "ER_EMPTY_EXPRESSION";
  public static final String ER_EXPECTED_BUT_FOUND = "ER_EXPECTED_BUT_FOUND";
  public static final String ER_INCORRECT_PROGRAMMER_ASSERTION =
         "ER_INCORRECT_PROGRAMMER_ASSERTION";
  public static final String ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL =
         "ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL";
  public static final String ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG =
         "ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG";
  public static final String ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG =
         "ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG";
  public static final String ER_PREDICATE_ILLEGAL_SYNTAX =
         "ER_PREDICATE_ILLEGAL_SYNTAX";
  public static final String ER_ILLEGAL_AXIS_NAME = "ER_ILLEGAL_AXIS_NAME";
  public static final String ER_UNKNOWN_NODETYPE = "ER_UNKNOWN_NODETYPE";
  public static final String ER_PATTERN_LITERAL_NEEDS_BE_QUOTED =
         "ER_PATTERN_LITERAL_NEEDS_BE_QUOTED";
  public static final String ER_COULDNOT_BE_FORMATTED_TO_NUMBER =
         "ER_COULDNOT_BE_FORMATTED_TO_NUMBER";
  public static final String ER_COULDNOT_CREATE_XMLPROCESSORLIAISON =
         "ER_COULDNOT_CREATE_XMLPROCESSORLIAISON";
  public static final String ER_DIDNOT_FIND_XPATH_SELECT_EXP =
         "ER_DIDNOT_FIND_XPATH_SELECT_EXP";
  public static final String ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH =
         "ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH";
  public static final String ER_ERROR_OCCURED = "ER_ERROR_OCCURED";
  public static final String ER_ILLEGAL_VARIABLE_REFERENCE =
         "ER_ILLEGAL_VARIABLE_REFERENCE";
  public static final String ER_AXES_NOT_ALLOWED = "ER_AXES_NOT_ALLOWED";
  public static final String ER_KEY_HAS_TOO_MANY_ARGS =
         "ER_KEY_HAS_TOO_MANY_ARGS";
  public static final String ER_COUNT_TAKES_1_ARG = "ER_COUNT_TAKES_1_ARG";
  public static final String ER_COULDNOT_FIND_FUNCTION =
         "ER_COULDNOT_FIND_FUNCTION";
  public static final String ER_UNSUPPORTED_ENCODING ="ER_UNSUPPORTED_ENCODING";
  public static final String ER_PROBLEM_IN_DTM_NEXTSIBLING =
         "ER_PROBLEM_IN_DTM_NEXTSIBLING";
  public static final String ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL =
         "ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL";
  public static final String ER_SETDOMFACTORY_NOT_SUPPORTED =
         "ER_SETDOMFACTORY_NOT_SUPPORTED";
  public static final String ER_PREFIX_MUST_RESOLVE = "ER_PREFIX_MUST_RESOLVE";
  public static final String ER_PARSE_NOT_SUPPORTED = "ER_PARSE_NOT_SUPPORTED";
  public static final String ER_SAX_API_NOT_HANDLED = "ER_SAX_API_NOT_HANDLED";
public static final String ER_IGNORABLE_WHITESPACE_NOT_HANDLED =
         "ER_IGNORABLE_WHITESPACE_NOT_HANDLED";
  public static final String ER_DTM_CANNOT_HANDLE_NODES =
         "ER_DTM_CANNOT_HANDLE_NODES";
  public static final String ER_XERCES_CANNOT_HANDLE_NODES =
         "ER_XERCES_CANNOT_HANDLE_NODES";
  public static final String ER_XERCES_PARSE_ERROR_DETAILS =
         "ER_XERCES_PARSE_ERROR_DETAILS";
  public static final String ER_XERCES_PARSE_ERROR = "ER_XERCES_PARSE_ERROR";
  public static final String ER_INVALID_UTF16_SURROGATE =
         "ER_INVALID_UTF16_SURROGATE";
  public static final String ER_OIERROR = "ER_OIERROR";
  public static final String ER_CANNOT_CREATE_URL = "ER_CANNOT_CREATE_URL";
  public static final String ER_XPATH_READOBJECT = "ER_XPATH_READOBJECT";
 public static final String ER_FUNCTION_TOKEN_NOT_FOUND =
         "ER_FUNCTION_TOKEN_NOT_FOUND";
  public static final String ER_CANNOT_DEAL_XPATH_TYPE =
         "ER_CANNOT_DEAL_XPATH_TYPE";
  public static final String ER_NODESET_NOT_MUTABLE = "ER_NODESET_NOT_MUTABLE";
  public static final String ER_NODESETDTM_NOT_MUTABLE =
         "ER_NODESETDTM_NOT_MUTABLE";
   /**  Variable not resolvable:   */
  public static final String ER_VAR_NOT_RESOLVABLE = "ER_VAR_NOT_RESOLVABLE";
   /** Null error handler  */
 public static final String ER_NULL_ERROR_HANDLER = "ER_NULL_ERROR_HANDLER";
   /**  Programmer's assertion: unknown opcode  */
  public static final String ER_PROG_ASSERT_UNKNOWN_OPCODE =
         "ER_PROG_ASSERT_UNKNOWN_OPCODE";
   /**  0 or 1   */
  public static final String ER_ZERO_OR_ONE = "ER_ZERO_OR_ONE";
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER =
         "ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = "ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";
   /**  fsb() not supported for XStringForChars   */
  public static final String ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS =
         "ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS";
   /**  Could not find variable with the name of   */
 public static final String ER_COULD_NOT_FIND_VAR = "ER_COULD_NOT_FIND_VAR";
   /**  XStringForChars can not take a string for an argument   */
 public static final String ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING =
         "ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING";
   /**  The FastStringBuffer argument can not be null   */
 public static final String ER_FASTSTRINGBUFFER_CANNOT_BE_NULL =
         "ER_FASTSTRINGBUFFER_CANNOT_BE_NULL";
   /**  2 or 3   */
  public static final String ER_TWO_OR_THREE = "ER_TWO_OR_THREE";
   /** Variable accessed before it is bound! */
  public static final String ER_VARIABLE_ACCESSED_BEFORE_BIND =
         "ER_VARIABLE_ACCESSED_BEFORE_BIND";
   /** XStringForFSB can not take a string for an argument! */
 public static final String ER_FSB_CANNOT_TAKE_STRING =
         "ER_FSB_CANNOT_TAKE_STRING";
   /** Error! Setting the root of a walker to null! */
  public static final String ER_SETTING_WALKER_ROOT_TO_NULL =
         "ER_SETTING_WALKER_ROOT_TO_NULL";
   /** This NodeSetDTM can not iterate to a previous node! */
  public static final String ER_NODESETDTM_CANNOT_ITERATE =
         "ER_NODESETDTM_CANNOT_ITERATE";
  /** This NodeSet can not iterate to a previous node! */
 public static final String ER_NODESET_CANNOT_ITERATE =
         "ER_NODESET_CANNOT_ITERATE";
  /** This NodeSetDTM can not do indexing or counting functions! */
  public static final String ER_NODESETDTM_CANNOT_INDEX =
         "ER_NODESETDTM_CANNOT_INDEX";
  /** This NodeSet can not do indexing or counting functions! */
  public static final String ER_NODESET_CANNOT_INDEX =
         "ER_NODESET_CANNOT_INDEX";
  /** Can not call setShouldCacheNodes after nextNode has been called! */
  public static final String ER_CANNOT_CALL_SETSHOULDCACHENODE =
         "ER_CANNOT_CALL_SETSHOULDCACHENODE";
  /** {0} only allows {1} arguments */
 public static final String ER_ONLY_ALLOWS = "ER_ONLY_ALLOWS";
  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  public static final String ER_UNKNOWN_STEP = "ER_UNKNOWN_STEP";
  /** Problem with RelativeLocationPath */
  public static final String ER_EXPECTED_REL_LOC_PATH =
         "ER_EXPECTED_REL_LOC_PATH";
  /** Problem with LocationPath */
  public static final String ER_EXPECTED_LOC_PATH = "ER_EXPECTED_LOC_PATH";
  public static final String ER_EXPECTED_LOC_PATH_AT_END_EXPR =
                                        "ER_EXPECTED_LOC_PATH_AT_END_EXPR";
  /** Problem with Step */
  public static final String ER_EXPECTED_LOC_STEP = "ER_EXPECTED_LOC_STEP";
  /** Problem with NodeTest */
  public static final String ER_EXPECTED_NODE_TEST = "ER_EXPECTED_NODE_TEST";
  /** Expected step pattern */
  public static final String ER_EXPECTED_STEP_PATTERN =
        "ER_EXPECTED_STEP_PATTERN";
  /** Expected relative path pattern */
  public static final String ER_EXPECTED_REL_PATH_PATTERN =
         "ER_EXPECTED_REL_PATH_PATTERN";
  /** ER_CANT_CONVERT_XPATHRESULTTYPE_TO_BOOLEAN          */
  public static final String ER_CANT_CONVERT_TO_BOOLEAN =
         "ER_CANT_CONVERT_TO_BOOLEAN";
  /** Field ER_CANT_CONVERT_TO_SINGLENODE       */
  public static final String ER_CANT_CONVERT_TO_SINGLENODE =
         "ER_CANT_CONVERT_TO_SINGLENODE";
  /** Field ER_CANT_GET_SNAPSHOT_LENGTH         */
  public static final String ER_CANT_GET_SNAPSHOT_LENGTH =
         "ER_CANT_GET_SNAPSHOT_LENGTH";
  /** Field ER_NON_ITERATOR_TYPE                */
  public static final String ER_NON_ITERATOR_TYPE = "ER_NON_ITERATOR_TYPE";
  /** Field ER_DOC_MUTATED                      */
  public static final String ER_DOC_MUTATED = "ER_DOC_MUTATED";
  public static final String ER_INVALID_XPATH_TYPE = "ER_INVALID_XPATH_TYPE";
  public static final String ER_EMPTY_XPATH_RESULT = "ER_EMPTY_XPATH_RESULT";
  public static final String ER_INCOMPATIBLE_TYPES = "ER_INCOMPATIBLE_TYPES";
  public static final String ER_NULL_RESOLVER = "ER_NULL_RESOLVER";
  public static final String ER_CANT_CONVERT_TO_STRING =
         "ER_CANT_CONVERT_TO_STRING";
  public static final String ER_NON_SNAPSHOT_TYPE = "ER_NON_SNAPSHOT_TYPE";
  public static final String ER_WRONG_DOCUMENT = "ER_WRONG_DOCUMENT";
  /* Note to translators:  The XPath expression cannot be evaluated with respect
   * to this type of node.
   */
  /** Field ER_WRONG_NODETYPE                    */
  public static final String ER_WRONG_NODETYPE = "ER_WRONG_NODETYPE";
  public static final String ER_XPATH_ERROR = "ER_XPATH_ERROR";

  //BEGIN: Keys needed for exception messages of  JAXP 1.3 XPath API implementation
  public static final String ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED = "ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED";
  public static final String ER_RESOLVE_VARIABLE_RETURNS_NULL = "ER_RESOLVE_VARIABLE_RETURNS_NULL";
  public static final String ER_UNSUPPORTED_RETURN_TYPE = "ER_UNSUPPORTED_RETURN_TYPE";
  public static final String ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL = "ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL";
  public static final String ER_ARG_CANNOT_BE_NULL = "ER_ARG_CANNOT_BE_NULL";

  public static final String ER_OBJECT_MODEL_NULL = "ER_OBJECT_MODEL_NULL";
  public static final String ER_OBJECT_MODEL_EMPTY = "ER_OBJECT_MODEL_EMPTY";
  public static final String ER_FEATURE_NAME_NULL = "ER_FEATURE_NAME_NULL";
  public static final String ER_FEATURE_UNKNOWN = "ER_FEATURE_UNKNOWN";
  public static final String ER_GETTING_NULL_FEATURE = "ER_GETTING_NULL_FEATURE";
  public static final String ER_GETTING_UNKNOWN_FEATURE = "ER_GETTING_UNKNOWN_FEATURE";
  public static final String ER_NULL_XPATH_FUNCTION_RESOLVER = "ER_NULL_XPATH_FUNCTION_RESOLVER";
  public static final String ER_NULL_XPATH_VARIABLE_RESOLVER = "ER_NULL_XPATH_VARIABLE_RESOLVER";
  //END: Keys needed for exception messages of  JAXP 1.3 XPath API implementation

  public static final String WG_LOCALE_NAME_NOT_HANDLED =
         "WG_LOCALE_NAME_NOT_HANDLED";
  public static final String WG_PROPERTY_NOT_SUPPORTED =
         "WG_PROPERTY_NOT_SUPPORTED";
  public static final String WG_DONT_DO_ANYTHING_WITH_NS =
         "WG_DONT_DO_ANYTHING_WITH_NS";
  public static final String WG_SECURITY_EXCEPTION = "WG_SECURITY_EXCEPTION";
  public static final String WG_QUO_NO_LONGER_DEFINED =
         "WG_QUO_NO_LONGER_DEFINED";
  public static final String WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST =
         "WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST";
  public static final String WG_FUNCTION_TOKEN_NOT_FOUND =
         "WG_FUNCTION_TOKEN_NOT_FOUND";
  public static final String WG_COULDNOT_FIND_FUNCTION =
         "WG_COULDNOT_FIND_FUNCTION";
  public static final String WG_CANNOT_MAKE_URL_FROM ="WG_CANNOT_MAKE_URL_FROM";
  public static final String WG_EXPAND_ENTITIES_NOT_SUPPORTED =
         "WG_EXPAND_ENTITIES_NOT_SUPPORTED";
  public static final String WG_ILLEGAL_VARIABLE_REFERENCE =
         "WG_ILLEGAL_VARIABLE_REFERENCE";
  public static final String WG_UNSUPPORTED_ENCODING ="WG_UNSUPPORTED_ENCODING";

  /**  detach() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER =
         "ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";
  /**  num() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER =
         "ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";
  /**  xstr() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER =
         "ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";
  /**  str() not supported by XRTreeFragSelectWrapper   */
  public static final String ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER =
         "ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER";

  // Error messages...


  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return new Object[][]{

  { "ERROR0000" , "{0}" },

  { ER_CURRENT_NOT_ALLOWED_IN_MATCH, "\u0424\u0443\u043d\u043a\u0446\u0438\u044f current() \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u0430 \u0432 \u0448\u0430\u0431\u043b\u043e\u043d\u0435 \u0434\u043b\u044f \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u044f!" },

  { ER_CURRENT_TAKES_NO_ARGS, "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 current() \u043d\u0435\u0442 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432!" },

  { ER_DOCUMENT_REPLACED,
      "\u0420\u0435\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 document() \u0437\u0430\u043c\u0435\u043d\u0435\u043d\u0430 \u043d\u0430 org.apache.xalan.xslt.FuncDocument!"},

  { ER_CONTEXT_HAS_NO_OWNERDOC,
      "\u0412 \u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442\u0435 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0434\u043e\u043a\u0443\u043c\u0435\u043d\u0442-\u0432\u043b\u0430\u0434\u0435\u043b\u0435\u0446!"},

  { ER_LOCALNAME_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 local-name() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_NAMESPACEURI_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 namespace-uri() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 normalize-space() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_NUMBER_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 number() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_NAME_HAS_TOO_MANY_ARGS,
     "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 name() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_STRING_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 string() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_STRINGLENGTH_HAS_TOO_MANY_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 string-length() \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u043c\u043d\u043e\u0433\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_TRANSLATE_TAKES_3_ARGS,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 translate() \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0442\u0440\u0438 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u0430!"},

  { ER_UNPARSEDENTITYURI_TAKES_1_ARG,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 unparsed-entity-uri \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u043e\u0434\u0438\u043d \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442!"},

  { ER_NAMESPACEAXIS_NOT_IMPLEMENTED,
      "\u041e\u0441\u044c \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u0430 \u0438\u043c\u0435\u043d \u0435\u0449\u0435 \u043d\u0435 \u0440\u0435\u0430\u043b\u0438\u0437\u043e\u0432\u0430\u043d\u0430!"},

  { ER_UNKNOWN_AXIS,
     "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u0441\u044c: {0}"},

  { ER_UNKNOWN_MATCH_OPERATION,
     "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u044f \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u044f!"},

  { ER_INCORRECT_ARG_LENGTH,
      "\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u0430\u044f \u0434\u043b\u0438\u043d\u0430 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 \u043f\u0440\u0438 \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u0438 \u0443\u0437\u043b\u0430 processing-instruction()!"},

  { ER_CANT_CONVERT_TO_NUMBER,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c {0} \u0432 \u0447\u0438\u0441\u043b\u043e"},

  { ER_CANT_CONVERT_TO_NODELIST,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c {0} \u0432 NodeList!"},

  { ER_CANT_CONVERT_TO_MUTABLENODELIST,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c {0} \u0432 NodeSetDTM!"},

  { ER_CANT_CONVERT_TO_TYPE,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c {0} \u0432 \u0442\u0438\u043f#{1}"},

  { ER_EXPECTED_MATCH_PATTERN,
      "\u0412 getMatchScore \u043e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0448\u0430\u0431\u043b\u043e\u043d \u0434\u043b\u044f \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u044f!"},

  { ER_COULDNOT_GET_VAR_NAMED,
      "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u0443\u044e {0}"},

  { ER_UNKNOWN_OPCODE,
     "\u041e\u0448\u0438\u0431\u043a\u0430! \u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u043a\u043e\u0434 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438: {0}"},

  { ER_EXTRA_ILLEGAL_TOKENS,
     "\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0435 \u043c\u0430\u0440\u043a\u0435\u0440\u044b: {0}"},


  { ER_EXPECTED_DOUBLE_QUOTE,
      "\u041b\u0438\u0442\u0435\u0440\u0430\u043b \u043d\u0435 \u0437\u0430\u043a\u043b\u044e\u0447\u0435\u043d \u0432 \u043a\u0430\u0432\u044b\u0447\u043a\u0438... \u041e\u0436\u0438\u0434\u0430\u043b\u0438\u0441\u044c \u0434\u0432\u043e\u0439\u043d\u044b\u0435 \u043a\u0430\u0432\u044b\u0447\u043a\u0438!"},

  { ER_EXPECTED_SINGLE_QUOTE,
      "\u041b\u0438\u0442\u0435\u0440\u0430\u043b \u043d\u0435 \u0437\u0430\u043a\u043b\u044e\u0447\u0435\u043d \u0432 \u043a\u0430\u0432\u044b\u0447\u043a\u0438... \u041e\u0436\u0438\u0434\u0430\u043b\u0438\u0441\u044c \u043e\u0434\u0438\u043d\u043e\u0447\u043d\u044b\u0435 \u043a\u0430\u0432\u044b\u0447\u043a\u0438!"},

  { ER_EMPTY_EXPRESSION,
     "\u041f\u0443\u0441\u0442\u043e\u0435 \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u0435!"},

  { ER_EXPECTED_BUT_FOUND,
     "\u041e\u0436\u0438\u0434\u0430\u043b\u043e\u0441\u044c {0}, \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u043e: {1}"},

  { ER_INCORRECT_PROGRAMMER_ASSERTION,
      "\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u043d\u043e\u0435 \u043f\u0440\u0435\u0434\u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435! - {0}"},

  { ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
      "\u0412 19990709 XPath \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442 boolean(...) \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u043d\u0435\u043e\u0431\u044f\u0437\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u043c."},

  { ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG,
      "\u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0430 \u0437\u0430\u043f\u044f\u0442\u0430\u044f ',' \u043d\u043e \u043f\u0435\u0440\u0435\u0434 \u043d\u0435\u0439 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442!"},

  { ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG,
      "\u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u0430 \u0437\u0430\u043f\u044f\u0442\u0430\u044f ',' \u043d\u043e \u043f\u043e\u0441\u043b\u0435 \u043d\u0435\u0435 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442!"},

  { ER_PREDICATE_ILLEGAL_SYNTAX,
      "\u0421\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 '..[\u043f\u0440\u0435\u0434\u0438\u043a\u0430\u0442]' \u0438\u043b\u0438 '.[\u043f\u0440\u0435\u0434\u0438\u043a\u0430\u0442]' \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c.  \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 'self::node()[\u043f\u0440\u0435\u0434\u0438\u043a\u0430\u0442]'."},

  { ER_ILLEGAL_AXIS_NAME,
     "\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u0438\u043c\u044f \u043e\u0441\u0438: {0}"},

  { ER_UNKNOWN_NODETYPE,
     "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0442\u0438\u043f \u0443\u0437\u043b\u0430: {0}"},

  { ER_PATTERN_LITERAL_NEEDS_BE_QUOTED,
      "\u0412 \u0448\u0430\u0431\u043b\u043e\u043d\u0435 \u043b\u0438\u0442\u0435\u0440\u0430\u043b ({0}) \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u0437\u0430\u043a\u043b\u044e\u0447\u0435\u043d \u0432 \u043a\u0430\u0432\u044b\u0447\u043a\u0438!"},

  { ER_COULDNOT_BE_FORMATTED_TO_NUMBER,
      "{0} \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043e\u0442\u0444\u043e\u0440\u043c\u0430\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043a\u0430\u043a \u0447\u0438\u0441\u043b\u043e!"},

  { ER_COULDNOT_CREATE_XMLPROCESSORLIAISON,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c XML TransformerFactory Liaison: {0}"},

  { ER_DIDNOT_FIND_XPATH_SELECT_EXP,
      "\u041e\u0448\u0438\u0431\u043a\u0430! \u041d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u0432\u044b\u0431\u043e\u0440\u0430 xpath (-select)."},

  { ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH,
      "\u041e\u0448\u0438\u0431\u043a\u0430! \u041f\u043e\u0441\u043b\u0435 OP_LOCATIONPATH \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 ENDOP"},

  { ER_ERROR_OCCURED,
     "\u041e\u0448\u0438\u0431\u043a\u0430!"},

  { ER_ILLEGAL_VARIABLE_REFERENCE,
      "VariableReference \u0434\u043b\u044f \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u043e\u0439 \u0437\u0430\u0434\u0430\u043d \u0432\u043d\u0435 \u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442\u0430 \u0438\u043b\u0438 \u0431\u0435\u0437 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u0438\u044f!  \u0418\u043c\u044f = {0}"},

  { ER_AXES_NOT_ALLOWED,
      "\u0412 \u0448\u0430\u0431\u043b\u043e\u043d\u0430\u0445 \u0441\u043e\u043e\u0442\u0432\u0435\u0442\u0441\u0442\u0432\u0438\u044f \u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0441\u0438 child:: \u0438 attribute::!  \u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0435 \u043e\u0441\u0438 = {0}"},

  { ER_KEY_HAS_TOO_MANY_ARGS,
      "\u0412 key() \u0443\u043a\u0430\u0437\u0430\u043d\u043e \u043d\u0435\u0432\u0435\u0440\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432."},

  { ER_COUNT_TAKES_1_ARG,
      "\u0423 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 count \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u043e\u0434\u0438\u043d \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442!"},

  { ER_COULDNOT_FIND_FUNCTION,
     "\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430: {0}"},

  { ER_UNSUPPORTED_ENCODING,
     "\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u0430\u044f \u043a\u043e\u0434\u0438\u0440\u043e\u0432\u043a\u0430: {0}"},

  { ER_PROBLEM_IN_DTM_NEXTSIBLING,
      "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 DTM \u0432 getNextSibling... \u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u044f"},

  { ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL,
      "\u041f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u043d\u0430\u044f \u043e\u0448\u0438\u0431\u043a\u0430: \u0437\u0430\u043f\u0438\u0441\u044c \u0432 EmptyNodeList \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u0430."},

  { ER_SETDOMFACTORY_NOT_SUPPORTED,
      "setDOMFactory \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f XPathContext!"},

  { ER_PREFIX_MUST_RESOLVE,
      "\u041f\u0440\u0435\u0444\u0438\u043a\u0441 \u0434\u043e\u043b\u0436\u0435\u043d \u043e\u0431\u0435\u0441\u043f\u0435\u0447\u0438\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u0432 \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u043e \u0438\u043c\u0435\u043d: {0}"},

  { ER_PARSE_NOT_SUPPORTED,
      "\u0410\u043d\u0430\u043b\u0438\u0437 \u0441 (InputSource \u0438\u0441\u0442\u043e\u0447\u043d\u0438\u043a) \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 XPathContext! \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c {0}"},

  { ER_SAX_API_NOT_HANDLED,
      "SAX API characters(char ch[]... \u043d\u0435 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u043d DTM!"},

  { ER_IGNORABLE_WHITESPACE_NOT_HANDLED,
      "ignorableWhitespace(char ch[]... \u043d\u0435 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u043d DTM!"},

  { ER_DTM_CANNOT_HANDLE_NODES,
      "DTMLiaison \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u043e\u0431\u0440\u0430\u0431\u0430\u0442\u044b\u0432\u0430\u0442\u044c \u0443\u0437\u043b\u044b \u0442\u0438\u043f\u0430 {0}"},

  { ER_XERCES_CANNOT_HANDLE_NODES,
      "DOM2Helper \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u043e\u0431\u0440\u0430\u0431\u0430\u0442\u044b\u0432\u0430\u0442\u044c \u0443\u0437\u043b\u044b \u0442\u0438\u043f\u0430 {0}"},

  { ER_XERCES_PARSE_ERROR_DETAILS,
      "\u041e\u0448\u0438\u0431\u043a\u0430 DOM2Helper.parse: SystemID - {0} \u0441\u0442\u0440\u043e\u043a\u0430 - {1}"},

  { ER_XERCES_PARSE_ERROR,
     "\u041e\u0448\u0438\u0431\u043a\u0430 DOM2Helper.parse"},

  { ER_INVALID_UTF16_SURROGATE,
      "\u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u043e \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 UTF-16: {0} ?"},

  { ER_OIERROR,
     "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0432\u043e\u0434\u0430-\u0432\u044b\u0432\u043e\u0434\u0430"},

  { ER_CANNOT_CREATE_URL,
     "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c URL \u0434\u043b\u044f {0}"},

  { ER_XPATH_READOBJECT,
     "\u0412 XPath.readObject: {0}"},

  { ER_FUNCTION_TOKEN_NOT_FOUND,
      "\u041c\u0430\u0440\u043a\u0435\u0440 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d."},

  { ER_CANNOT_DEAL_XPATH_TYPE,
       "\u0420\u0430\u0431\u043e\u0442\u0430 \u0441 \u0442\u0438\u043f\u043e\u043c XPath \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u0430: {0}"},

  { ER_NODESET_NOT_MUTABLE,
       "\u0414\u0430\u043d\u043d\u044b\u0439 \u043d\u0430\u0431\u043e\u0440 NodeSet \u043d\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0434\u0432\u0443\u0441\u0442\u043e\u0440\u043e\u043d\u043d\u0438\u043c"},

  { ER_NODESETDTM_NOT_MUTABLE,
       "\u0414\u0430\u043d\u043d\u044b\u0439 \u043d\u0430\u0431\u043e\u0440 NodeSetDTM \u043d\u0435 \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0434\u0432\u0443\u0441\u0442\u043e\u0440\u043e\u043d\u043d\u0438\u043c"},

  { ER_VAR_NOT_RESOLVABLE,
        "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u0443\u044e: {0}"},

  { ER_NULL_ERROR_HANDLER,
        "\u041f\u0443\u0441\u0442\u043e\u0439 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0447\u0438\u043a \u043e\u0448\u0438\u0431\u043a\u0438"},

  { ER_PROG_ASSERT_UNKNOWN_OPCODE,
       "\u0417\u0430\u043f\u0438\u0441\u044c \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0438\u0441\u0442\u0430: \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u043a\u043e\u0434 \u043e\u043f\u0446\u0438\u0438: {0}"},

  { ER_ZERO_OR_ONE,
       "0 \u0438\u043b\u0438 1"},

  { ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "rtf() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f XRTreeFragSelectWrapper"},

  { ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
       "asNodeIterator() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f XRTreeFragSelectWrapper"},

   { ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
                "detach() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 XRTreeFragSelectWrapper"},

   { ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
                "num() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 XRTreeFragSelectWrapper"},

   { ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
                "xstr() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 XRTreeFragSelectWrapper"},

   { ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
                "str() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0432 XRTreeFragSelectWrapper"},

  { ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS,
       "fsb() \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f XStringForChars"},

  { ER_COULD_NOT_FIND_VAR,
      "\u041f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u0430\u044f {0} \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430"},

  { ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING,
      "\u0410\u0440\u0433\u0443\u043c\u0435\u043d\u0442 XStringForChars \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u043e\u0439"},

  { ER_FASTSTRINGBUFFER_CANNOT_BE_NULL,
      "\u0410\u0440\u0433\u0443\u043c\u0435\u043d\u0442 FastStringBuffer \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0443\u0441\u0442\u044b\u043c"},

  { ER_TWO_OR_THREE,
       "2 \u0438\u043b\u0438 3"},

  { ER_VARIABLE_ACCESSED_BEFORE_BIND,
       "\u041e\u0431\u0440\u0430\u0449\u0435\u043d\u0438\u0435 \u043a \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u043e\u0439 \u0434\u043e \u0435\u0435 \u0441\u0432\u044f\u0437\u044b\u0432\u0430\u043d\u0438\u044f!"},

  { ER_FSB_CANNOT_TAKE_STRING,
       "\u0410\u0440\u0433\u0443\u043c\u0435\u043d\u0442 XStringForFSB \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u043e\u0439!"},

  { ER_SETTING_WALKER_ROOT_TO_NULL,
       "\n !!!! \u041e\u0448\u0438\u0431\u043a\u0430! \u041a\u043e\u0440\u043d\u0435\u0432\u043e\u043c\u0443 \u043a\u0430\u0442\u0430\u043b\u043e\u0433\u0443 walker \u043f\u0440\u0438\u0441\u0432\u043e\u0435\u043d\u043e \u043f\u0443\u0441\u0442\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435!!!"},

  { ER_NODESETDTM_CANNOT_ITERATE,
       "\u0414\u0430\u043d\u043d\u044b\u0439 NodeSetDTM \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0440\u0430\u0431\u043e\u0442\u0430\u0442\u044c \u0441 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u043c \u0443\u0437\u043b\u043e\u043c!"},

  { ER_NODESET_CANNOT_ITERATE,
       "\u0414\u0430\u043d\u043d\u044b\u0439 NodeSet \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0440\u0430\u0431\u043e\u0442\u0430\u0442\u044c \u0441 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u043c \u0443\u0437\u043b\u043e\u043c!"},

  { ER_NODESETDTM_CANNOT_INDEX,
       "\u0414\u0430\u043d\u043d\u044b\u0439 NodeSetDTM \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u0442\u044c \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u0438\u043d\u0434\u0435\u043a\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f \u0438 \u043f\u043e\u0434\u0441\u0447\u0435\u0442\u0430!"},

  { ER_NODESET_CANNOT_INDEX,
       "\u0414\u0430\u043d\u043d\u044b\u0439 NodeSet \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u0442\u044c \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u0438\u043d\u0434\u0435\u043a\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f \u0438 \u043f\u043e\u0434\u0441\u0447\u0435\u0442\u0430!"},

  { ER_CANNOT_CALL_SETSHOULDCACHENODE,
       "\u041d\u0435\u043b\u044c\u0437\u044f \u0432\u044b\u0437\u044b\u0432\u0430\u0442\u044c setShouldCacheNodes \u043f\u043e\u0441\u043b\u0435 \u0432\u044b\u0437\u043e\u0432\u0430 nextNode!"},

  { ER_ONLY_ALLOWS,
       "\u041c\u0430\u043a\u0441\u0438\u043c\u0430\u043b\u044c\u043d\u043e\u0435 \u0447\u0438\u0441\u043b\u043e \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u043e\u0432 {0} \u0440\u0430\u0432\u043d\u043e {1}"},

  { ER_UNKNOWN_STEP,
       "\u0417\u0430\u043f\u0438\u0441\u044c \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u043c\u0438\u0441\u0442\u0430 \u0432 getNextStepPos: \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 stepType: {0}"},

  //Note to translators:  A relative location path is a form of XPath expression.
  // The message indicates that such an expression was expected following the
  // characters '/' or '//', but was not found.
  { ER_EXPECTED_REL_LOC_PATH,
      "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u043e\u0442\u043d\u043e\u0441\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0439 \u043f\u0443\u0442\u044c, \u043f\u043e\u0441\u043b\u0435 \u043a\u043e\u0442\u043e\u0440\u043e\u0433\u043e \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u043b \u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u044c \u043c\u0430\u0440\u043a\u0435\u0440 '/' \u0438\u043b\u0438 '//'."},

  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such an expression was expected,but
  // the characters specified by the substitution text were encountered instead.
  { ER_EXPECTED_LOC_PATH,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u043f\u0443\u0442\u044c, \u043e\u0434\u043d\u0430\u043a\u043e \u0431\u044b\u043b \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u043c\u0430\u0440\u043a\u0435\u0440\u003a  {0}"},

  // Note to translators:  A location path is a form of XPath expression.
  // The message indicates that syntactically such a subexpression was expected,
  // but no more characters were found in the expression.
  { ER_EXPECTED_LOC_PATH_AT_END_EXPR,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u043f\u0443\u0442\u044c \u043a \u0440\u0430\u0441\u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u044e, \u043d\u043e \u0432\u043c\u0435\u0441\u0442\u043e \u044d\u0442\u043e\u0433\u043e \u0431\u044b\u043b \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u043a\u043e\u043d\u0435\u0446 \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath. "},

  // Note to translators:  A location step is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected
  // following the specified characters.
  { ER_EXPECTED_LOC_STEP,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0448\u0430\u0433 \u0440\u0430\u0441\u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u044f, \u043f\u043e\u0441\u043b\u0435 \u043a\u043e\u0442\u043e\u0440\u043e\u0433\u043e \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u043b \u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u044c \u043c\u0430\u0440\u043a\u0435\u0440 '/' \u0438\u043b\u0438 '//'."},

  // Note to translators:  A node test is part of an XPath expression that is
  // used to test for particular kinds of nodes.  In this case, a node test that
  // consists of an NCName followed by a colon and an asterisk or that consists
  // of a QName was expected, but was not found.
  { ER_EXPECTED_NODE_TEST,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0442\u0435\u0441\u0442 \u0443\u0437\u043b\u0430, \u0441\u043e\u0432\u043f\u0430\u0434\u0430\u044e\u0449\u0435\u0433\u043e \u0441 NCName:* \u0438\u043b\u0438 QName. "},

  // Note to translators:  A step pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but the specified character was found in the expression instead.
  { ER_EXPECTED_STEP_PATTERN,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0448\u0430\u0431\u043b\u043e\u043d \u0448\u0430\u0433\u0430, \u043e\u0434\u043d\u0430\u043a\u043e \u0431\u044b\u043b \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d '/'."},

  // Note to translators: A relative path pattern is part of an XPath expression.
  // The message indicates that syntactically such an expression was expected,
  // but was not found.
  { ER_EXPECTED_REL_PATH_PATTERN,
       "\u041e\u0436\u0438\u0434\u0430\u043b\u0441\u044f \u0448\u0430\u0431\u043b\u043e\u043d \u043e\u0442\u043d\u043e\u0441\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0433\u043e \u043f\u0443\u0442\u0438."},

  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type boolean.
  { ER_CANT_CONVERT_TO_BOOLEAN,
       "\u0412 XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'' \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 XPathResultType \u0440\u0430\u0432\u043d\u043e {1}, \u0447\u0442\u043e \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u0432 \u0431\u0443\u043b\u0435\u0432\u0441\u043a\u043e\u0435  \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435. "},

  // Note to translators: Do not translate ANY_UNORDERED_NODE_TYPE and
  // FIRST_ORDERED_NODE_TYPE.
  { ER_CANT_CONVERT_TO_SINGLENODE,
       "\u0412 XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'' \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 XPathResultType \u0440\u0430\u0432\u043d\u043e {1}, \u0447\u0442\u043e \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u0432 \u0443\u0437\u0435\u043b. \u041c\u0435\u0442\u043e\u0434 getSingleNodeValue \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u043c \u0442\u043e\u043b\u044c\u043a\u043e \u043a \u0442\u0438\u043f\u0430\u043c ANY_UNORDERED_NODE_TYPE \u0438 FIRST_ORDERED_NODE_TYPE. "},

  // Note to translators: Do not translate UNORDERED_NODE_SNAPSHOT_TYPE and
  // ORDERED_NODE_SNAPSHOT_TYPE.
  { ER_CANT_GET_SNAPSHOT_LENGTH,
       "\u041c\u0435\u0442\u043e\u0434 getSnapshotLength \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c \u0434\u043b\u044f XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'', \u0442\u0430\u043a \u043a\u0430\u043a \u0435\u0433\u043e XPathResultType \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f {1}. \u042d\u0442\u043e\u0442 \u043c\u0435\u0442\u043e\u0434 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u043c \u0442\u043e\u043b\u044c\u043a\u043e \u043a \u0442\u0438\u043f\u0430\u043c UNORDERED_NODE_SNAPSHOT_TYPE \u0438 ORDERED_NODE_SNAPSHOT_TYPE. "},

  { ER_NON_ITERATOR_TYPE,
       "\u041c\u0435\u0442\u043e\u0434 iterateNext \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c \u0434\u043b\u044f XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'', \u0442\u0430\u043a \u043a\u0430\u043a \u0435\u0433\u043e XPathResultType \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f {1}. \u042d\u0442\u043e\u0442 \u043c\u0435\u0442\u043e\u0434 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u043c \u0442\u043e\u043b\u044c\u043a\u043e \u043a \u0442\u0438\u043f\u0430\u043c UNORDERED_NODE_ITERATOR_TYPE \u0438 ORDERED_NODE_ITERATOR_TYPE. "},

  // Note to translators: This message indicates that the document being operated
  // upon changed, so the iterator object that was being used to traverse the
  // document has now become invalid.
  { ER_DOC_MUTATED,
       "\u0421 \u043c\u043e\u043c\u0435\u043d\u0442\u0430 \u043f\u043e\u043b\u0443\u0447\u0435\u043d\u0438\u044f \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u0430 \u0434\u043e\u043a\u0443\u043c\u0435\u043d\u0442 \u0431\u044b\u043b \u0438\u0437\u043c\u0435\u043d\u0435\u043d. \u0418\u0442\u0435\u0440\u0430\u0442\u043e\u0440 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c."},

  { ER_INVALID_XPATH_TYPE,
       "\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0439 \u0442\u0438\u043f \u0430\u0440\u0433\u0443\u043c\u0435\u043d\u0442\u0430 XPath: {0}"},

  { ER_EMPTY_XPATH_RESULT,
       "\u041f\u0443\u0441\u0442\u043e\u0439 \u043e\u0431\u044a\u0435\u043a\u0442 \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u0430 XPath"},

  { ER_INCOMPATIBLE_TYPES,
       "XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'' \u0438\u043c\u0435\u0435\u0442 XPathResultType {1}, \u043a\u043e\u0442\u043e\u0440\u043e\u0435 \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u0432 \u0443\u043a\u0430\u0437\u0430\u043d\u043d\u044b\u0439 XPathResultType {2}. "},

  { ER_NULL_RESOLVER,
       "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0444\u0438\u043a\u0441 \u0441 \u043f\u043e\u043c\u043e\u0449\u044c\u044e \u043f\u0443\u0441\u0442\u043e\u0433\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u0435\u043b\u044f."},

  // Note to translators:  The substitution text is the name of a data type.  The
  // message indicates that a value of a particular type could not be converted
  // to a value of type string.
  { ER_CANT_CONVERT_TO_STRING,
       "\u0412 XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'' \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 XPathResultType \u0440\u0430\u0432\u043d\u043e {1}, \u0447\u0442\u043e \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u0432 \u0441\u0442\u0440\u043e\u043a\u043e\u0432\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435. "},

  // Note to translators: Do not translate snapshotItem,
  // UNORDERED_NODE_SNAPSHOT_TYPE and ORDERED_NODE_SNAPSHOT_TYPE.
  { ER_NON_SNAPSHOT_TYPE,
       "\u041c\u0435\u0442\u043e\u0434 snapshotItem \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c \u0434\u043b\u044f XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'', \u0442\u0430\u043a \u043a\u0430\u043a \u0435\u0433\u043e XPathResultType \u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f {1}. \u042d\u0442\u043e\u0442 \u043c\u0435\u0442\u043e\u0434 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u043c \u0442\u043e\u043b\u044c\u043a\u043e \u043a \u0442\u0438\u043f\u0430\u043c UNORDERED_NODE_SNAPSHOT_TYPE \u0438 ORDERED_NODE_SNAPSHOT_TYPE. "},

  // Note to translators:  XPathEvaluator is a Java interface name.  An
  // XPathEvaluator is created with respect to a particular XML document, and in
  // this case the expression represented by this object was being evaluated with
  // respect to a context node from a different document.
  { ER_WRONG_DOCUMENT,
       "\u0423\u0437\u0435\u043b \u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442\u0430 \u043d\u0435 \u043e\u0442\u043d\u043e\u0441\u0438\u0442\u0441\u044f \u043a \u0434\u043e\u043a\u0443\u043c\u0435\u043d\u0442\u0443, \u0441\u0432\u044f\u0437\u0430\u043d\u043d\u043e\u043c\u0443 \u0441 \u0434\u0430\u043d\u043d\u044b\u043c XPathEvaluator."},

  // Note to translators:  The XPath expression cannot be evaluated with respect
  // to this type of node.
  { ER_WRONG_NODETYPE,
       "\u0422\u0438\u043f \u0443\u0437\u043b\u0430 \u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442\u0430 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f."},

  { ER_XPATH_ERROR,
       "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u043e\u0448\u0438\u0431\u043a\u0430 \u0432 XPath."},

        { ER_CANT_CONVERT_XPATHRESULTTYPE_TO_NUMBER,
                "\u0412 XPathResult \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u044f XPath ''{0}'' \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 XPathResultType \u0440\u0430\u0432\u043d\u043e {1}, \u0447\u0442\u043e \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u043f\u0440\u0435\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u0442\u044c \u0432 \u0447\u0438\u0441\u043b\u043e\u0432\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435. "},

  //BEGIN:  Definitions of error keys used  in exception messages of  JAXP 1.3 XPath API implementation

  /** Field ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED                       */

  { ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,
       "\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u0440\u0430\u0441\u0448\u0438\u0440\u0435\u043d\u0438\u044f: \u043d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c ''{0}'', \u043a\u043e\u0433\u0434\u0430 \u0434\u043b\u044f \u0444\u0443\u043d\u043a\u0446\u0438\u0438 XMLConstants.FEATURE_SECURE_PROCESSING \u0437\u0430\u0434\u0430\u043d\u043e \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 true. "},

  /** Field ER_RESOLVE_VARIABLE_RETURNS_NULL                       */

  { ER_RESOLVE_VARIABLE_RETURNS_NULL,
       "resolveVariable \u0434\u043b\u044f \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u043e\u0439 {0} \u0432\u0435\u0440\u043d\u0443\u043b\u0430 \u043f\u0443\u0441\u0442\u043e\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435. "},

  /** Field ER_UNSUPPORTED_RETURN_TYPE                       */

  { ER_UNSUPPORTED_RETURN_TYPE,
       "\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u044b\u0439 \u0442\u0438\u043f \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u0430: {0}"},

  /** Field ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL                       */

  { ER_SOURCE_RETURN_TYPE_CANNOT_BE_NULL,
       "\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a \u0438/\u0438\u043b\u0438 \u0442\u0438\u043f \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u0430 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0443\u0441\u0442\u044b\u043c"},

  /** Field ER_ARG_CANNOT_BE_NULL                       */

  { ER_ARG_CANNOT_BE_NULL,
       "\u0410\u0440\u0433\u0443\u043c\u0435\u043d\u0442 {0} \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043f\u0443\u0441\u0442\u044b\u043c"},

  /** Field ER_OBJECT_MODEL_NULL                       */

  { ER_OBJECT_MODEL_NULL,
       "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c {0}#isObjectModelSupported( \u0441\u0442\u0440\u043e\u043a\u0430 objectModel ) \u043f\u0440\u0438 objectModel == null"},

  /** Field ER_OBJECT_MODEL_EMPTY                       */

  { ER_OBJECT_MODEL_EMPTY,
       "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u044b\u0437\u0432\u0430\u0442\u044c {0}#isObjectModelSupported( \u0441\u0442\u0440\u043e\u043a\u0430 objectModel ) \u043f\u0440\u0438 objectModel == \"\""},

  /** Field ER_OBJECT_MODEL_EMPTY                       */

  { ER_FEATURE_NAME_NULL,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u0442\u044c \u0444\u0443\u043d\u043a\u0446\u0438\u044e \u0441 \u043f\u0443\u0441\u0442\u044b\u043c \u0438\u043c\u0435\u043d\u0435\u043c: {0}#setFeature( null, {1})"},

  /** Field ER_FEATURE_UNKNOWN                       */

  { ER_FEATURE_UNKNOWN,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u0442\u044c \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0443\u044e \u0444\u0443\u043d\u043a\u0446\u0438\u044e \"{0}\":{1}#setFeature({0},{2})"},

  /** Field ER_GETTING_NULL_FEATURE                       */

  { ER_GETTING_NULL_FEATURE,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u0442\u044c \u0444\u0443\u043d\u043a\u0446\u0438\u044e \u0441 \u043f\u0443\u0441\u0442\u044b\u043c \u0438\u043c\u0435\u043d\u0435\u043c: {0}#getFeature(null)"},

  /** Field ER_GETTING_NULL_FEATURE                       */

  { ER_GETTING_UNKNOWN_FEATURE,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u043d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0443\u044e \u0444\u0443\u043d\u043a\u0446\u0438\u044e \"{0}\":{1}#getFeature({0})"},

  /** Field ER_NULL_XPATH_FUNCTION_RESOLVER                       */

  { ER_NULL_XPATH_FUNCTION_RESOLVER,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u0442\u044c \u043f\u0443\u0441\u0442\u043e\u0439 XPathFunctionResolver:{0}#setXPathFunctionResolver(null)"},

  /** Field ER_NULL_XPATH_VARIABLE_RESOLVER                       */

  { ER_NULL_XPATH_VARIABLE_RESOLVER,
       "\u041f\u043e\u043f\u044b\u0442\u043a\u0430 \u0437\u0430\u0434\u0430\u0442\u044c \u043f\u0443\u0441\u0442\u043e\u0439 XPathVariableResolver:{0}#setXPathVariableResolver(null)"},

  //END:  Definitions of error keys used  in exception messages of  JAXP 1.3 XPath API implementation

  // Warnings...

  { WG_LOCALE_NAME_NOT_HANDLED,
      "\u041b\u043e\u043a\u0430\u043b\u044c\u043d\u043e\u0435 \u0438\u043c\u044f \u0432 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 format-number \u0435\u0449\u0435 \u043d\u0435 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u043d\u043e!"},

  { WG_PROPERTY_NOT_SUPPORTED,
      "\u0421\u0432\u043e\u0439\u0441\u0442\u0432\u043e XSL \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f: {0}"},

  { WG_DONT_DO_ANYTHING_WITH_NS,
      "\u041d\u0435 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u0439\u0442\u0435 \u043d\u0438\u043a\u0430\u043a\u0438\u0445 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0439 \u0441 \u043f\u0440\u043e\u0441\u0442\u0440\u0430\u043d\u0441\u0442\u0432\u043e\u043c \u0438\u043c\u0435\u043d {0} \u0432 \u0441\u0432\u043e\u0439\u0441\u0442\u0432\u0435: {1}"},

  { WG_SECURITY_EXCEPTION,
      "SecurityException \u043f\u0440\u0438 \u043f\u043e\u043f\u044b\u0442\u043a\u0435 \u043e\u0431\u0440\u0430\u0449\u0435\u043d\u0438\u044f \u043a \u0441\u0438\u0441\u0442\u0435\u043c\u043d\u043e\u043c\u0443 \u0441\u0432\u043e\u0439\u0441\u0442\u0432\u0443 XSL: {0}"},

  { WG_QUO_NO_LONGER_DEFINED,
      "\u0421\u0442\u0430\u0440\u044b\u0439 \u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441: quo(...) \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0435 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d \u0432 XPath."},

  { WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST,
      "\u0414\u043b\u044f \u0440\u0435\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u0438 nodeTest \u0432 XPath \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u043d\u044b\u0439 \u043e\u0431\u044a\u0435\u043a\u0442!"},

  { WG_FUNCTION_TOKEN_NOT_FOUND,
      "\u041c\u0430\u0440\u043a\u0435\u0440 \u0444\u0443\u043d\u043a\u0446\u0438\u0438 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d."},

  { WG_COULDNOT_FIND_FUNCTION,
      "\u0424\u0443\u043d\u043a\u0446\u0438\u044f \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430: {0}"},

  { WG_CANNOT_MAKE_URL_FROM,
      "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u0442\u044c URL \u0438\u0437: {0}"},

  { WG_EXPAND_ENTITIES_NOT_SUPPORTED,
      "\u041e\u043f\u0446\u0438\u044f -E \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0430\u043d\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440\u043e\u043c DTM"},

  { WG_ILLEGAL_VARIABLE_REFERENCE,
      "VariableReference \u0434\u043b\u044f \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u043e\u0439 \u0437\u0430\u0434\u0430\u043d \u0432\u043d\u0435 \u043a\u043e\u043d\u0442\u0435\u043a\u0441\u0442\u0430 \u0438\u043b\u0438 \u0431\u0435\u0437 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u0438\u044f!  \u0418\u043c\u044f = {0}"},

  { WG_UNSUPPORTED_ENCODING,
     "\u041d\u0435\u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u043c\u0430\u044f \u043a\u043e\u0434\u0438\u0440\u043e\u0432\u043a\u0430: {0}"},



  // Other miscellaneous text used inside the code...
  { "ui_language", "en"},
  { "help_language", "en"},
  { "language", "en"},
  { "BAD_CODE", "\u041f\u0430\u0440\u0430\u043c\u0435\u0442\u0440 createMessage \u043b\u0435\u0436\u0438\u0442 \u0432\u043d\u0435 \u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0433\u043e \u0434\u0438\u0430\u043f\u0430\u0437\u043e\u043d\u0430"},
  { "FORMAT_FAILED", "\u0418\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u0441\u0438\u0442\u0443\u0430\u0446\u0438\u044f \u043f\u0440\u0438 \u0432\u044b\u0437\u043e\u0432\u0435 messageFormat"},
  { "version", ">>>>>>> \u0412\u0435\u0440\u0441\u0438\u044f Xalan "},
  { "version2", "<<<<<<<"},
  { "yes", "\u0434\u0430"},
  { "line", "\u041d\u043e\u043c\u0435\u0440 \u0441\u0442\u0440\u043e\u043a\u0438 "},
  { "column", "\u041d\u043e\u043c\u0435\u0440 \u0441\u0442\u043e\u043b\u0431\u0446\u0430 "},
  { "xsldone", "XSLProcessor: \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u043e"},
  { "xpath_option", "\u041e\u043f\u0446\u0438\u0438 xpath: "},
  { "optionIN", "   [-in inputXMLURL]"},
  { "optionSelect", "   [-select \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u0435 xpath]"},
  { "optionMatch", "   [-match \u0448\u0430\u0431\u043b\u043e\u043d \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u044f (\u0434\u043b\u044f \u0434\u0438\u0430\u0433\u043d\u043e\u0441\u0442\u0438\u043a\u0438)]"},
  { "optionAnyExpr", "\u0418\u043b\u0438 \u043f\u0440\u043e\u0441\u0442\u043e \u0443\u043a\u0430\u0436\u0438\u0442\u0435 \u0432\u044b\u0440\u0430\u0436\u0435\u043d\u0438\u0435 xpath \u0434\u043b\u044f \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f \u0434\u0438\u0430\u0433\u043d\u043e\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u0434\u0430\u043c\u043f\u0430"},
  { "noParsermsg1", "\u0412 \u043f\u0440\u043e\u0446\u0435\u0441\u0441\u0435 XSL \u043e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d\u044b \u043e\u0448\u0438\u0431\u043a\u0438."},
  { "noParsermsg2", "** \u0410\u043d\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d **"},
  { "noParsermsg3", "\u041f\u0440\u043e\u0432\u0435\u0440\u044c\u0442\u0435 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 classpath."},
  { "noParsermsg4", "\u0415\u0441\u043b\u0438 \u0443 \u0432\u0430\u0441 \u043d\u0435\u0442 \u0430\u043d\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440\u0430 XML Parser for Java \u0444\u0438\u0440\u043c\u044b IBM, \u0432\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0435\u0433\u043e \u0441 \u0441\u0430\u0439\u0442\u0430"},
  { "noParsermsg5", "IBM AlphaWorks: http://www.alphaworks.ibm.com/formula/xml"},
  { "gtone", ">1" },
  { "zero", "0" },
  { "one", "1" },
  { "two" , "2" },
  { "three", "3" }

  };
  }


  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE          */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED          */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** Field ERROR_RESOURCES          */
  public static final String ERROR_RESOURCES =
    "org.apache.xpath.res.XPATHErrorResources";

  /** Field ERROR_STRING          */
  public static final String ERROR_STRING = "\u041e\u0448\u0438\u0431\u043a\u0430";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "\u041e\u0448\u0438\u0431\u043a\u0430: ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "\u041f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0435\u043d\u0438\u0435: ";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "PATTERN ";


  /**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle().
   *
   * @param className Name of local-specific subclass.
   * @return the ResourceBundle
   * @throws MissingResourceException
   */
  public static final XPATHErrorResources loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return (XPATHErrorResources) ResourceBundle.getBundle(className
              + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (XPATHErrorResources) ResourceBundle.getBundle(className,
                new Locale("en", "US"));
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
