/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class RiskAdjustedPerformanceCalculatorTest {

  @Test
  public void test() {
    final double assetReturn = 0.12;
    final double riskFreeReturn = 0.03;
    final double assetStandardDeviation = 0.15;
    final double marketStandardDeviation = 0.17;
    assertEquals(new RiskAdjustedPerformanceCalculator().calculate(assetReturn, riskFreeReturn, assetStandardDeviation, marketStandardDeviation), 0.1320, 1e-4);
  }
}