/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.fast.DateTimeNumericEncoding;
import com.opengamma.util.timeseries.fast.integer.FastArrayIntDoubleTimeSeries;

/**
 * 
 */
public class FixedStrikeLookbackOptionDefinitionTest {
  private static final double STRIKE = 100;
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2011, 5, 1);
  private static final Expiry EXPIRY = new Expiry(DATE);
  private static final FixedStrikeLookbackOptionDefinition CALL = new FixedStrikeLookbackOptionDefinition(STRIKE, EXPIRY, true);
  private static final FixedStrikeLookbackOptionDefinition PUT = new FixedStrikeLookbackOptionDefinition(STRIKE, EXPIRY, false);
  private static final double SPOT = 100;
  private static final double DIFF = 10;
  private static final DoubleTimeSeries<?> HIGH_TS = new FastArrayIntDoubleTimeSeries(DateTimeNumericEncoding.DATE_DDMMYYYY, new int[] {20100501, 20101101, 20110501}, new double[] {SPOT, SPOT + DIFF,
      SPOT});
  private static final DoubleTimeSeries<?> LOW_TS = new FastArrayIntDoubleTimeSeries(DateTimeNumericEncoding.DATE_DDMMYYYY, new int[] {20100501, 20101101, 20110501}, new double[] {SPOT, SPOT - DIFF,
      SPOT});
  private static final StandardOptionWithSpotTimeSeriesDataBundle HIGH_DATA = new StandardOptionWithSpotTimeSeriesDataBundle(new ConstantYieldCurve(0.1), 0.05,
      new ConstantVolatilitySurface(0.2), SPOT, DateUtil.getUTCDate(2010, 6, 1), HIGH_TS);
  private static final StandardOptionWithSpotTimeSeriesDataBundle LOW_DATA = new StandardOptionWithSpotTimeSeriesDataBundle(new ConstantYieldCurve(0.1), 0.05,
      new ConstantVolatilitySurface(0.2), SPOT, DateUtil.getUTCDate(2010, 6, 1), LOW_TS);

  @Test(expected = IllegalArgumentException.class)
  public void testNullDataBundle() {
    CALL.getPayoffFunction().getPayoff(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullTS() {
    CALL.getPayoffFunction().getPayoff(HIGH_DATA.withSpotTimeSeries(null), null);
  }

  @Test
  public void testExercise() {
    assertFalse(CALL.getExerciseFunction().shouldExercise(HIGH_DATA, null));
    assertFalse(CALL.getExerciseFunction().shouldExercise(LOW_DATA, null));
    assertFalse(PUT.getExerciseFunction().shouldExercise(HIGH_DATA, null));
    assertFalse(PUT.getExerciseFunction().shouldExercise(LOW_DATA, null));
  }

  @Test
  public void testPayoff() {
    final double eps = 1e-15;
    OptionPayoffFunction<StandardOptionWithSpotTimeSeriesDataBundle> payoff = CALL.getPayoffFunction();
    assertEquals(payoff.getPayoff(LOW_DATA, 0.), 0, eps);
    assertEquals(payoff.getPayoff(HIGH_DATA, 0.), SPOT + DIFF - STRIKE, eps);
    payoff = PUT.getPayoffFunction();
    assertEquals(payoff.getPayoff(LOW_DATA, 0.), STRIKE + DIFF - SPOT, eps);
    assertEquals(payoff.getPayoff(HIGH_DATA, 0.), 0, eps);
  }

  @Test
  public void testEqualsAndHashCode() {
    final OptionDefinition call1 = new FixedStrikeLookbackOptionDefinition(STRIKE, EXPIRY, true);
    final OptionDefinition put1 = new FixedStrikeLookbackOptionDefinition(STRIKE, EXPIRY, false);
    final OptionDefinition call2 = new FixedStrikeLookbackOptionDefinition(STRIKE, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 3)), true);
    final OptionDefinition put2 = new FixedStrikeLookbackOptionDefinition(STRIKE, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 3)), false);
    assertFalse(CALL.equals(PUT));
    assertEquals(call1, CALL);
    assertEquals(put1, PUT);
    assertEquals(call1.hashCode(), CALL.hashCode());
    assertEquals(put1.hashCode(), PUT.hashCode());
    assertFalse(call2.equals(CALL));
    assertFalse(put2.equals(PUT));
  }
}