/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.option.pricing.analytic.formula;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.opengamma.financial.model.volatility.BlackImpliedVolatilityFormula;
import com.opengamma.financial.model.volatility.smile.function.SABRFormulaData;
import com.opengamma.financial.model.volatility.smile.function.SABRHaganVolatilityFunction;
import com.opengamma.math.rootfinding.VanWijngaardenDekkerBrentSingleRootFinder;

/**
 * 
 */
public class CEVPriceFunctionTest {
  private static final SABRHaganVolatilityFunction SABR = new SABRHaganVolatilityFunction();
  private static final CEVPriceFunction CEV = new CEVPriceFunction();
  private static final BlackImpliedVolatilityFormula BLACK_IMPLIED_VOL = new BlackImpliedVolatilityFormula(new VanWijngaardenDekkerBrentSingleRootFinder());

  /**
   * For short dated options should have good agreement with the SABR formula for nu = 0
   */
  @Test
  public void testBeta() {
    final double f = 4;
    final double k = 3.5;
    final double atmVol = 0.3;
    final double t = 0.1;
    double beta;

    for (int i = 0; i < 200; i++) {
      beta = i / 100.0;
      final double sigma = atmVol * Math.pow(f, 1 - beta);
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, t, true);
      final CEVFunctionData cevData = new CEVFunctionData(f, 1.0, sigma, beta);
      final double price = CEV.getPriceFunction(option).evaluate(cevData);
      final double vol = BLACK_IMPLIED_VOL.getImpliedVolatility(cevData, option, price);
      final SABRFormulaData sabrData = new SABRFormulaData(f, sigma, beta, 0.0, 0.0);
      final double sabrVol = SABR.getVolatilityFunction(option).evaluate(sabrData);
      assertEquals(sabrVol, vol, 1e-5);
    }
  }

  @Test
  public void testStrike() {
    final double f = 4;
    double k;
    final double atmVol = 0.3;
    final double t = 0.5;
    final double beta = 0.5;
    final double sigma = atmVol * Math.pow(f, 1 - beta);

    for (int i = 0; i < 20; i++) {
      k = 1.0 + i / 2.5;
      final EuropeanVanillaOption option = new EuropeanVanillaOption(k, t, true);
      final CEVFunctionData cevData = new CEVFunctionData(f, 1.0, sigma, beta);
      final double price = CEV.getPriceFunction(option).evaluate(cevData);
      final double vol = BLACK_IMPLIED_VOL.getImpliedVolatility(cevData, option, price);
      final SABRFormulaData sabrData = new SABRFormulaData(f, sigma, beta, 0.0, 0.0);
      final double sabrVol = SABR.getVolatilityFunction(option).evaluate(sabrData);
      assertEquals(sabrVol, vol, 1e-4);
    }
  }

  @Test
  public void testBetaAndStrike() {
    final double f = 4;
    double k;
    final double atmVol = 0.3;
    final double t = 0.1;
    double beta;
    double sigma;

    for (int i = 0; i < 20; i++) {
      beta = (i + 1) / 20.0;
      sigma = atmVol * Math.pow(f, 1 - beta);
      for (int j = 0; j < 20; j++) {
        k = 3.0 + j / 10.0;
        final EuropeanVanillaOption option = new EuropeanVanillaOption(k, t, true);
        final CEVFunctionData cevData = new CEVFunctionData(f, 1.0, sigma, beta);
        final double price = CEV.getPriceFunction(option).evaluate(cevData);
        final double vol = BLACK_IMPLIED_VOL.getImpliedVolatility(cevData, option, price);
        final SABRFormulaData sabrData = new SABRFormulaData(f, sigma, beta, 0.0, 0.0);
        final double sabrVol = SABR.getVolatilityFunction(option).evaluate(sabrData);
        assertEquals(sabrVol, vol, 1e-5);
      }
    }
  }

}