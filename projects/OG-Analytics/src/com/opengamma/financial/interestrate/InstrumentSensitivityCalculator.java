/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import java.util.LinkedHashMap;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;
import com.opengamma.math.matrix.CommonsMatrixAlgebra;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.matrix.MatrixAlgebra;

/**
 * 
 */
public final class InstrumentSensitivityCalculator {
  private final MatrixAlgebra _ma = new CommonsMatrixAlgebra();
  private static final InstrumentSensitivityCalculator INSTANCE = new InstrumentSensitivityCalculator();

  private InstrumentSensitivityCalculator() {
  }

  public static InstrumentSensitivityCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Calculates the sensitivity of the present value (PV) of an instrument to changes in the par-rates of the instruments used to build the curve (i.e. bucketed delta)
   * @param ird The instrument of interest
   * @param fixedCurves any fixed curves (can be null)
   * @param interpolatedCurves The set of interpolatedCurves 
   * @param couponSensitivity The sensitivity of the PV of the instruments used to build the curve(s) to their coupon rate (with the curve fixed)
   * @param pvJacobian Matrix of sensitivity of the PV of the instruments used to build the curve(s) to the yields - 
   * the i,j element is dx_i/dy_j where x_i is the PV of the ith instrument and y_j is the jth yield 
   * @return bucked delta
   */
  public DoubleMatrix1D calculateFromPresentValue(final InterestRateDerivative ird, final YieldCurveBundle fixedCurves, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves,
      final DoubleMatrix1D couponSensitivity, final DoubleMatrix2D pvJacobian) {
    final NodeSensitivityCalculator nsc = new NodeSensitivityCalculator();
    final DoubleMatrix1D nodeSense = nsc.presentValueCalculate(ird, fixedCurves, interpolatedCurves);
    final int n = nodeSense.getNumberOfElements();
    Validate.isTrue(n == couponSensitivity.getNumberOfElements());
    Validate.isTrue(n == pvJacobian.getNumberOfColumns());
    Validate.isTrue(n == pvJacobian.getNumberOfRows());

    final DoubleMatrix2D invJac = _ma.getInverse(pvJacobian);

    final double[] res = new double[n];
    for (int i = 0; i < n; i++) {
      double sum = 0;
      for (int j = 0; j < n; j++) {
        sum += -couponSensitivity.getEntry(i) * invJac.getEntry(j, i) * nodeSense.getEntry(j);
      }
      res[i] = sum;
    }
    return new DoubleMatrix1D(res);
  }

  /**
   * Calculates the sensitivity of the present value (PV) of an instrument to changes in the par-rates of the instruments used to build the curve (i.e. bucketed delta)
   * @param ird The instrument of interest
   * @param fixedCurves any fixed curves (can be null)
   * @param interpolatedCurves The set of interpolatedCurves
   * @param parRateJacobian Matrix of sensitivity of the par-rate of the instruments used to build the curve(s) to the yields - 
   * the i,j element is dr_i/dy_j where r_i is the par-rate of the ith instrument and y_j is the jth yield 
   * @return bucked delta
   */
  public DoubleMatrix1D calculateFromParRate(final InterestRateDerivative ird, final YieldCurveBundle fixedCurves, final LinkedHashMap<String, YieldAndDiscountCurve> interpolatedCurves,
      final DoubleMatrix2D parRateJacobian) {
    final NodeSensitivityCalculator nsc = new NodeSensitivityCalculator();
    final DoubleMatrix1D nodeSensitivities = nsc.presentValueCalculate(ird, fixedCurves, interpolatedCurves);
    final int n = nodeSensitivities.getNumberOfElements();

    Validate.isTrue(n == parRateJacobian.getNumberOfColumns(), "Have " + n + " node sensitivities but " + parRateJacobian.getNumberOfColumns() + " columns in Jacobian");
    Validate.isTrue(n == parRateJacobian.getNumberOfRows(), "Have " + n + " node sensitivities but " + parRateJacobian.getNumberOfRows() + " rows in Jacobian");

    final DoubleMatrix2D invJac = _ma.getInverse(parRateJacobian);

    final double[] res = new double[n];
    for (int i = 0; i < n; i++) {
      double sum = 0;
      for (int j = 0; j < n; j++) {
        sum += invJac.getEntry(j, i) * nodeSensitivities.getEntry(j);
      }
      res[i] = sum;
    }
    return new DoubleMatrix1D(res);
  }

}
