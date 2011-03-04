/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic.formula;

import org.apache.commons.lang.Validate;

/**
 * 
 */
public class EuropeanVanillaOption {
  private final boolean _isCall;
  private final double _t;
  private final double _k;

  public EuropeanVanillaOption(final double k, final double t, final boolean isCall) {
    Validate.isTrue(k > 0.0, "k must be > 0.0");
    Validate.isTrue(t >= 0.0, "t must be >= 0.0");
    _k = k;
    _t = t;
    _isCall = isCall;
  }

  public boolean isCall() {
    return _isCall;
  }

  public double getT() {
    return _t;
  }

  public double getK() {
    return _k;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_isCall ? 1231 : 1237);
    long temp;
    temp = Double.doubleToLongBits(_k);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_t);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final EuropeanVanillaOption other = (EuropeanVanillaOption) obj;
    if (_isCall != other._isCall) {
      return false;
    }
    if (Double.doubleToLongBits(_k) != Double.doubleToLongBits(other._k)) {
      return false;
    }
    if (Double.doubleToLongBits(_t) != Double.doubleToLongBits(other._t)) {
      return false;
    }
    return true;
  }

}