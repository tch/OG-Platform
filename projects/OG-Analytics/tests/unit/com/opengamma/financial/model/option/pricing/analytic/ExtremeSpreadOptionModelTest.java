/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.ZonedDateTime;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.financial.model.option.definition.ExtremeSpreadOptionDefinition;
import com.opengamma.financial.model.option.definition.StandardOptionWithSpotTimeSeriesDataBundle;
import com.opengamma.financial.model.volatility.surface.ConstantVolatilitySurface;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.time.Expiry;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.ArrayZonedDateTimeDoubleTimeSeries;

/**
 * 
 */
public class ExtremeSpreadOptionModelTest {
  private static final double SPOT = 100;
  private static final double B = 0.1;
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(0.1);
  private static final ZonedDateTime DATE = DateUtil.getUTCDate(2010, 7, 1);
  private static final Expiry EXPIRY = new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 1));
  private static final ExtremeSpreadOptionModel MODEL = new ExtremeSpreadOptionModel();
  private static final double EPS = 1e-4;

  @Test
  public void test() {
    DoubleTimeSeries<?> ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 100});
    StandardOptionWithSpotTimeSeriesDataBundle data = new StandardOptionWithSpotTimeSeriesDataBundle(CURVE, B, new ConstantVolatilitySurface(0.15), SPOT, DATE, ts);
    ExtremeSpreadOptionDefinition option = new ExtremeSpreadOptionDefinition(EXPIRY, true, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.25)), false);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 10.6618, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 110});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 8.4878, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 120});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 4.5235, EPS);
    option = new ExtremeSpreadOptionDefinition(EXPIRY, true, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.75)), true);
    data = data.withVolatilitySurface(new ConstantVolatilitySurface(0.3));
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 100});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 13.3404, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 90});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 14.8173, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 80});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 19.0537, EPS);
    option = new ExtremeSpreadOptionDefinition(EXPIRY, true, new Expiry(DateUtil.getDateOffsetWithYearFraction(DATE, 0.)), true);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 100});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 0, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 90});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 1.4769, EPS);
    ts = new ArrayZonedDateTimeDoubleTimeSeries(new ZonedDateTime[] {DateUtil.getUTCDate(2010, 6, 1), DateUtil.getUTCDate(2010, 11, 1)}, new double[] {SPOT, 80});
    data = data.withSpotTimeSeries(ts);
    assertEquals(MODEL.getPricingFunction(option).evaluate(data), 5.7133, EPS);
  }
}