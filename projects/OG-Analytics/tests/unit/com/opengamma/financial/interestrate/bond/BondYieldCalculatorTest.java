/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.bond;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.interestrate.ParRateCalculator;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.interestrate.bond.definition.Bond;
import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;

/**
 * 
 */
public class BondYieldCalculatorTest {
  private static final BondYieldCalculator CALCULATOR = new BondYieldCalculator();
  private static final ParRateCalculator PRC = ParRateCalculator.getInstance();
  private static final double PRICE = 1.;
  private static final double YIELD = 0.05;
  private static final YieldAndDiscountCurve CURVE = new ConstantYieldCurve(YIELD);
  private static final YieldCurveBundle BUNDLE = new YieldCurveBundle();
  private static final String CURVE_NAME = "Flat 5% curve";
  private static Bond BOND;

  static {
    final int n = 15;
    final double[] paymentTimes = new double[n];
    final double alpha = 0.5;
    for (int i = 0; i < n; i++) {
      paymentTimes[i] = (i + 1) * alpha;
    }
    BUNDLE.setCurve(CURVE_NAME, CURVE);

    BOND = new Bond(paymentTimes, 0.0, CURVE_NAME);
    final double rate = PRC.getValue(BOND, BUNDLE);
    BOND = new Bond(paymentTimes, rate, CURVE_NAME);
  }

  @Test
  public void testBond() {
    final double yield = CALCULATOR.calculate(BOND, PRICE);
    assertEquals(YIELD, yield, 1e-8);
  }

}