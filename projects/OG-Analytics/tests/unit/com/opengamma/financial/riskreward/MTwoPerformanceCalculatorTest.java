/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.riskreward;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;

/**
 * 
 */
public class MTwoPerformanceCalculatorTest {

  @Test
  public void test() {
    final double assetReturn = 0.12;
    final double riskFreeReturn = 0.03;
    final double marketReturn = 0.11;
    final double assetStandardDeviation = 0.15;
    final double marketStandardDeviation = 0.17;
    assertEquals(new MTwoPerformanceCalculator().calculate(assetReturn, riskFreeReturn, marketReturn, assetStandardDeviation, marketStandardDeviation), 0.022, 1e-3);
  }
}
