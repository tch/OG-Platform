/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.annuity.definition;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 */
public class YieldSensitivityCalculatorTest {
  private static YieldSensitivityCalculator YSC = YieldSensitivityCalculator.getInstance();

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAnnuity1() {
    YSC.calculateYield(null, 1.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAnnuity2() {
    YSC.calculatePriceForYield(null, 0.05);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAnnuity3() {
    YSC.calculateNthOrderSensitivity(null, 1.0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyAnnuity4() {
    YSC.calculateNthOrderSensitivityFromYield(null, 0.04, 1);
  }

  @Test
  public void testSinglePaymentYield() {
    int n = 10;
    double pv = 0.875;
    double[] paymentTimes = new double[n];
    double[] coupons = new double[n];
    double tau = 0.5;
    for (int i = 0; i < n; i++) {
      paymentTimes[i] = (i + 1) * tau;
    }
    coupons[n - 1] = 2.0;

    FixedAnnuity annuity = new FixedAnnuity(paymentTimes, 1.0, coupons, "");
    double yield = YieldSensitivityCalculator.getInstance().calculateYield(annuity, pv);
    assertEquals(Math.log(2.0 * tau / pv) / 10.0 / tau, yield, 1e-8);
  }

  @Test
  public void testSinglePaymentSensitivity() {
    int n = 10;
    double pv = 0.78945;
    double[] paymentTimes = new double[n];
    double[] coupons = new double[n];
    double tau = 0.5;
    for (int i = 0; i < n; i++) {
      paymentTimes[i] = (i + 1) * tau;
    }
    coupons[n - 1] = 2.0;

    FixedAnnuity annuity = new FixedAnnuity(paymentTimes, 1.0, coupons, "");

    for (int order = 1; order < 5; order++) {
      double sense = YieldSensitivityCalculator.getInstance().calculateNthOrderSensitivity(annuity, pv, order);
      assertEquals(Math.pow(tau * n, order) * pv, sense, 1e-8);
    }
  }

  @Test
  public void testSinglePaymentSensitivityFromYield() {
    int n = 10;
    double yield = 0.06;
    double[] paymentTimes = new double[n];
    double[] coupons = new double[n];
    double tau = 0.5;
    for (int i = 0; i < n; i++) {
      paymentTimes[i] = (i + 1) * tau;
    }
    coupons[n - 1] = 2.0;

    FixedAnnuity annuity = new FixedAnnuity(paymentTimes, 1.0, coupons, "");
    double pv = YieldSensitivityCalculator.getInstance().calculatePriceForYield(annuity, yield);
    for (int order = 1; order < 5; order++) {
      double sense = YieldSensitivityCalculator.getInstance().calculateNthOrderSensitivityFromYield(annuity, yield,
          order);
      assertEquals(Math.pow(tau * n, order) * pv, sense, 1e-8);
    }
  }

}