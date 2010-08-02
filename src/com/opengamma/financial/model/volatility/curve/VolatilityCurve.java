/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.volatility.curve;

import java.util.Map;

import com.opengamma.financial.model.volatility.VolatilityModel;

/**
 * 
 */
public abstract class VolatilityCurve implements VolatilityModel<Double> {

  @Override
  public abstract Double getVolatility(final Double x);

  public abstract VolatilityCurve withParallelShift(Double shift);

  public abstract VolatilityCurve withSingleShift(Double x, Double shift);

  public abstract VolatilityCurve withMultipleShifts(Map<Double, Double> shifts);

}
