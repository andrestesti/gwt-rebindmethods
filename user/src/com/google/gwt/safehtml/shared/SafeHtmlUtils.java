/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.safehtml.shared;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Utility class containing static methods for escaping and sanitizing strings.
 */
public final class SafeHtmlUtils {

  private static final String HTML_ENTITY_REGEX =
      "[a-z]+|#[0-9]+|#x[0-9a-fA-F]+";

  public static final SafeHtml EMPTY_SAFE_HTML = new SafeHtmlString("");

  private static final RegExp AMP_RE = RegExp.compile("&", "g");
  private static final RegExp GT_RE = RegExp.compile(">", "g");
  private static final RegExp LT_RE = RegExp.compile("<", "g");
  private static final RegExp SQUOT_RE = RegExp.compile("\'", "g");
  private static final RegExp QUOT_RE = RegExp.compile("\"", "g");

  /**
   * Returns a SafeHtml constructed from a safe string, i.e. without escaping
   * the string.
   *
   * <p>
   * <b>Important</b>: For this class to be able to honor its contract as
   * required by {@link SafeHtml}, all uses of this method must satisfy the
   * following requirements:
   *
   * <ul>
   *
   * <li>The argument expression must be fully determined and known to be safe
   * at compile time.
   *
   * <li>The value of the argument must not contain incomplete HTML tags.
   *
   * </ul>
   */
  public static SafeHtml fromSafeConstant(String s) {
    // TODO(pdr): (hosted-mode only) assert that html satisfies the second
    // constraint.
    return new SafeHtmlString(s);
  }

  /**
   * Returns a SafeHtml containing the escaped string.
   */
  public static SafeHtml fromString(String s) {
    return new SafeHtmlString(htmlEscape(s));
  }

  /**
   * Returns a SafeHtml constructed from a trusted string, i.e. without escaping
   * the string. No checks are performed. The calling code should be carefully
   * reviewed to ensure the argument meets the SafeHtml contract.
   */
  public static SafeHtml fromTrustedString(String s) {
    return new SafeHtmlString(s);
  }

  /**
   * HTML-escapes a string.
   *
   *  Note: The following variants of this function were profiled on FF36,
   * Chrome6, IE8:
   * #1) for each case, check indexOf, then use s.replace(regex, string)
   * #2) for each case, check indexOf, then use s.replaceAll()
   * #3) check if any metachar is present using a regex, then use #1
   * #4) for each case, use s.replace(regex, string)
   *
   * #1 was found to be the fastest, and is used below.
   *
   * @param s the string to be escaped
   * @return the input string, with all occurrences of HTML meta-characters
   *         replaced with their corresponding HTML Entity References
   */
  public static String htmlEscape(String s) {
    if (s.indexOf("&") != -1) {
      s = AMP_RE.replace(s, "&amp;");
    }
    if (s.indexOf("<") != -1) {
      s = LT_RE.replace(s, "&lt;");
    }
    if (s.indexOf(">") != -1) {
      s = GT_RE.replace(s, "&gt;");
    }
    if (s.indexOf("\"") != -1) {
      s = QUOT_RE.replace(s, "&quot;");
    }
    if (s.indexOf("\'") != -1) {
      s = SQUOT_RE.replace(s, "&#39;");
    }
    return s;
  }

  /**
   * HTML-escapes a string, but does not double-escape HTML-entities already
   * present in the string.
   *
   * @param text the string to be escaped
   * @return the input string, with all occurrences of HTML meta-characters
   *         replaced with their corresponding HTML Entity References, with the
   *         exception that ampersand characters are not double-escaped if they
   *         form the start of an HTML Entity Reference
   */
  public static String htmlEscapeAllowEntities(String text) {
    StringBuilder escaped = new StringBuilder();

    boolean firstSegment = true;
    for (String segment : text.split("&", -1)) {
      if (firstSegment) {
        /*
         * The first segment is never part of an entity reference, so we always
         * escape it.
         * Note that if the input starts with an ampersand, we will get an empty
         * segment before that.
         */
        firstSegment = false;
        escaped.append(htmlEscape(segment));
        continue;
      }

      int entityEnd = segment.indexOf(';');
      if (entityEnd > 0
          && segment.substring(0, entityEnd).matches(HTML_ENTITY_REGEX)) {
        // Append the entity without escaping.
        escaped.append("&").append(segment.substring(0, entityEnd + 1));

        // Append the rest of the segment, escaped.
        escaped.append(htmlEscape(segment.substring(entityEnd + 1)));
      } else {
        // The segment did not start with an entity reference, so escape the
        // whole segment.
        escaped.append("&amp;").append(htmlEscape(segment));
      }
    }

    return escaped.toString();
  }

  // prevent instantiation
  private SafeHtmlUtils() {
  }
}