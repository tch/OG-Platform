/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.function.special;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function;

/**
 * 
 */
public class KroneckerDeltaFunction implements Function<Integer, Integer> {

  @Override
  public Integer evaluate(final Integer... x) {
    Validate.notNull(x, "integer array");
    Validate.isTrue(x.length == 2, "Can only have two inputs to Kronecker delta function");
    final double i = x[0];
    final double j = x[1];
    return i == j ? 1 : 0;
  }

}
