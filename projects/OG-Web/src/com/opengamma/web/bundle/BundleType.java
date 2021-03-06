/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.bundle;

import org.apache.commons.lang.StringUtils;

/**
 * BundleType representation
 */
public enum BundleType {
  /**
   * Javascript Type
   */
  JS("js"),
  /**
   * CSS Type
   */
  CSS("css");

  private final String _suffix;

  BundleType(String suffix) {
    _suffix = suffix;
  }

  public String getSuffix() {
    return _suffix;
  }

  public static BundleType getType(String fileName) {
    if (!StringUtils.isBlank(fileName)) {
      return (fileName.endsWith("." + JS.getSuffix())) ? JS : (fileName.endsWith("." + CSS.getSuffix())) ? CSS : null;
    }
    return null;
  }
}

