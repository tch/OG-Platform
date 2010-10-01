/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 */
public class FixedIncomeCalculatorFactoryTest {

  @Test(expected = IllegalArgumentException.class)
  public void testBadName() {
    FixedIncomeCalculatorFactory.getCalculator("A");
  }

  @Test
  public void testNullCalculator() {
    assertNull(FixedIncomeCalculatorFactory.getCalculatorName(null));
  }

  @Test
  public void test() {
    assertEquals(FixedIncomeCalculatorFactory.PAR_RATE, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PAR_RATE)));
    assertEquals(FixedIncomeCalculatorFactory.PAR_RATE_CURVE_SENSITIVITY, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PAR_RATE_CURVE_SENSITIVITY)));
    assertEquals(FixedIncomeCalculatorFactory.PRESENT_VALUE, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PRESENT_VALUE)));
    assertEquals(FixedIncomeCalculatorFactory.PRESENT_VALUE_COUPON_SENSITIVITY, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PRESENT_VALUE_COUPON_SENSITIVITY)));
    assertEquals(FixedIncomeCalculatorFactory.PRESENT_VALUE_SENSITIVITY, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PRESENT_VALUE_SENSITIVITY)));
    assertEquals(FixedIncomeCalculatorFactory.PAR_RATE, FixedIncomeCalculatorFactory.getCalculatorName(FixedIncomeCalculatorFactory
        .getCalculator(FixedIncomeCalculatorFactory.PAR_RATE)));
  }
}