/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.yield;

import com.opengamma.util.ArgumentChecker;

/**
 * A simple yield convention.
 */
public class SimpleYieldConvention implements YieldConvention {
  // TODO: should be an enum?

  /**
   * The US street yield convention.
   */
  public static final YieldConvention US_STREET = new SimpleYieldConvention("US street");
  /**
   * The US treasury equivalent yield convention. 
   */
  public static final YieldConvention US_TREASURY_EQUIVILANT = new SimpleYieldConvention("US Treasury equivalent");
  /**
   * The money market yield convention.
   */
  public static final YieldConvention MONEY_MARKET = new SimpleYieldConvention("Money Market");
  /**
   * The JGB simple yield convention.
   */
  public static final YieldConvention JGB_SIMPLE = new SimpleYieldConvention("JGB simple");
  /**
   * The true yield convention.
   */
  public static final YieldConvention TRUE = new SimpleYieldConvention("True");

  /**
   * The convention name.
   */
  private final String _name;

  /**
   * Creates an instance.
   * @param name  the convention name, not null
   */
  protected SimpleYieldConvention(final String name) {
    ArgumentChecker.notNull(name, "name");
    _name = name;
  }

  //-------------------------------------------------------------------------
  @Override
  public String getConventionName() {
    return _name;
  }

}