/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.statistics.leastsquare;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.jet.random.engine.MersenneTwister64;

import com.opengamma.math.UtilFunctions;
import com.opengamma.math.function.Function1D;
import com.opengamma.math.function.ParameterizedFunction;
import com.opengamma.math.linearalgebra.LUDecompositionCommons;
import com.opengamma.math.linearalgebra.LUDecompositionResult;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.math.matrix.DoubleMatrixUtils;
import com.opengamma.math.matrix.MatrixAlgebra;
import com.opengamma.math.matrix.OGMatrixAlgebra;
import com.opengamma.math.minimization.BrentMinimizer1D;
import com.opengamma.math.minimization.ConjugateGradientVectorMinimizer;
import com.opengamma.math.statistics.distribution.NormalDistribution;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.monitor.OperationTimer;

public class NonLinearLeastSquareTest {
  private static final Logger s_logger = LoggerFactory.getLogger(NonLinearLeastSquareTest.class);
  private static final int HOTSPOT_WARMUP_CYCLES = 0;
  private static final int BENCHMARK_CYCLES = 1;

  private static final NormalDistribution NORMAL = new NormalDistribution(0, 1.0, new MersenneTwister64(
      MersenneTwister64.DEFAULT_SEED));
  private static final DoubleMatrix1D X;
  private static final DoubleMatrix1D Y;
  private static final DoubleMatrix1D SIGMA;
  private static final NonLinearLeastSquare LS;

  private static final Function1D<Double, Double> TARRGET = new Function1D<Double, Double>() {

    @Override
    public Double evaluate(final Double x) {
      return Math.sin(x);
    }
  };

  private static final Function1D<DoubleMatrix1D, DoubleMatrix1D> FUNCTION = new Function1D<DoubleMatrix1D, DoubleMatrix1D>() {

    @SuppressWarnings("synthetic-access")
    @Override
    public DoubleMatrix1D evaluate(DoubleMatrix1D a) {
      ArgumentChecker.isTrue(a.getNumberOfElements() == 4, "four parameters");
      int n = X.getNumberOfElements();
      double[] res = new double[n];
      for (int i = 0; i < n; i++) {
        res[i] = a.getEntry(0) * Math.sin(a.getEntry(1) * X.getEntry(i) + a.getEntry(2)) + a.getEntry(3);
      }
      return new DoubleMatrix1D(res);
    }
  };

  private static final ParameterizedFunction<Double, DoubleMatrix1D, Double> PARM_FUNCTION = new ParameterizedFunction<Double, DoubleMatrix1D, Double>() {

    @Override
    public Double evaluate(final Double x, final DoubleMatrix1D a) {
      ArgumentChecker.isTrue(a.getNumberOfElements() == 4, "four parameters");
      return a.getEntry(0) * Math.sin(a.getEntry(1) * x + a.getEntry(2)) + a.getEntry(3);
    }
  };

  private static final ParameterizedFunction<Double, DoubleMatrix1D, DoubleMatrix1D> PARM_GRAD = new ParameterizedFunction<Double, DoubleMatrix1D, DoubleMatrix1D>() {

    @Override
    public DoubleMatrix1D evaluate(final Double x, final DoubleMatrix1D a) {
      ArgumentChecker.isTrue(a.getNumberOfElements() == 4, "four parameters");
      final double temp1 = Math.sin(a.getEntry(1) * x + a.getEntry(2));
      final double temp2 = Math.cos(a.getEntry(1) * x + a.getEntry(2));
      final double[] res = new double[4];
      res[0] = temp1;
      res[2] = a.getEntry(0) * temp2;
      res[1] = x * res[2];
      res[3] = 1.0;
      return new DoubleMatrix1D(res);
    }
  };

  static {
    X = new DoubleMatrix1D(new double[20]);
    Y = new DoubleMatrix1D(new double[20]);
    SIGMA = new DoubleMatrix1D(new double[20]);

    for (int i = 0; i < 20; i++) {
      X.getData()[i] = -Math.PI + i * Math.PI / 10;
      Y.getData()[i] = TARRGET.evaluate(X.getEntry(i));
      SIGMA.getData()[i] = 0.1 * Math.exp(Math.abs(X.getEntry(i)) / Math.PI);
    }

    LS = new NonLinearLeastSquare();
  }

  @Test
  public void solveExactTest() {

    final DoubleMatrix1D start = new DoubleMatrix1D(new double[] {1.2, 0.8, -0.2, -0.3});
    final LeastSquareResults res = LS.solve(X, Y, SIGMA, PARM_FUNCTION, PARM_GRAD, start);
    assertEquals(0.0, res.getChiSq(), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(0), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(1), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(2), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(3), 1e-8);
  }

  @Test
  public void solveExactTest2() {

    final DoubleMatrix1D start = new DoubleMatrix1D(new double[] {0.2, 1.8, 0.2, 0.3});
    final LeastSquareResults res = LS.solve(Y, SIGMA, FUNCTION, start);
    assertEquals(0.0, res.getChiSq(), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(0), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(1), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(2), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(3), 1e-8);
  }

  public void solveExactFromChiSqTest() {
    DoubleMatrix1D start = new DoubleMatrix1D(new double[] {1.2, 0.8, -0.2, -0.3});
    Function1D<DoubleMatrix1D, Double> f = getChiSqFunction(X, Y, SIGMA, PARM_FUNCTION);
    ConjugateGradientVectorMinimizer minimizer = new ConjugateGradientVectorMinimizer(new BrentMinimizer1D());
    DoubleMatrix1D solution = minimizer.minimize(f, start);
    assertEquals(0.0, f.evaluate(solution), 1e-8);
    assertEquals(1.0, solution.getEntry(0), 1e-8);
    assertEquals(1.0, solution.getEntry(1), 1e-8);
    assertEquals(0.0, solution.getEntry(2), 1e-8);
    assertEquals(0.0, solution.getEntry(3), 1e-8);

  }

  @Test
  public void doExactHotSpot() {
    for (int i = 0; i < HOTSPOT_WARMUP_CYCLES; i++) {
      solveExactWithoutGradientTest();
    }
    if (BENCHMARK_CYCLES > 0) {
      final OperationTimer timer = new OperationTimer(s_logger, "processing {} cycles on Levenberg-Marquardt",
          BENCHMARK_CYCLES);
      for (int i = 0; i < BENCHMARK_CYCLES; i++) {
        solveExactWithoutGradientTest();
      }
      timer.finished();
    }
  }

  @Test
  public void doChiSqHotSpot() {
    for (int i = 0; i < HOTSPOT_WARMUP_CYCLES; i++) {
      solveExactFromChiSqTest();
    }
    if (BENCHMARK_CYCLES > 0) {
      final OperationTimer timer = new OperationTimer(s_logger, "processing {} cycles on Conugate gradient",
          BENCHMARK_CYCLES);
      for (int i = 0; i < BENCHMARK_CYCLES; i++) {
        solveExactFromChiSqTest();
      }
      timer.finished();
    }
  }

  @Test
  public void solveExactWithoutGradientTest() {

    final DoubleMatrix1D start = new DoubleMatrix1D(new double[] {1.2, 0.8, -0.2, -0.3});

    NonLinearLeastSquare ls = new NonLinearLeastSquare();
    LeastSquareResults res = ls.solve(X, Y, SIGMA, PARM_FUNCTION, start);
    assertEquals(0.0, res.getChiSq(), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(0), 1e-8);
    assertEquals(1.0, res.getParameters().getEntry(1), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(2), 1e-8);
    assertEquals(0.0, res.getParameters().getEntry(3), 1e-8);
  }

  /**
   * This tests a fit to random data, so it could fail or rare occasions. Only consecutive fails indicate a bug 
   */
  @Test
  public void solveRandomNoiseTest() {
    final MatrixAlgebra ma = new OGMatrixAlgebra();
    final double[] y = new double[20];
    for (int i = 0; i < 20; i++) {
      y[i] = Y.getEntry(i) + SIGMA.getEntry(i) * NORMAL.nextRandom();
    }
    final DoubleMatrix1D start = new DoubleMatrix1D(new double[] {0.7, 1.4, 0.2, -0.3});
    final NonLinearLeastSquare ls = new NonLinearLeastSquare();
    final LeastSquareResults res = ls.solve(X, new DoubleMatrix1D(y), SIGMA, PARM_FUNCTION, PARM_GRAD, start);

    final double chiSqDoF = res.getChiSq() / 16;
    assertTrue(chiSqDoF > 0.25);
    assertTrue(chiSqDoF < 3.0);

    final DoubleMatrix1D trueValues = new DoubleMatrix1D(new double[] {1, 1, 0, 0});
    final DoubleMatrix1D delta = (DoubleMatrix1D) ma.subtract(res.getParameters(), trueValues);

    final LUDecompositionCommons decmp = new LUDecompositionCommons();
    final LUDecompositionResult decmpRes = decmp.evaluate(res.getCovariance());
    final DoubleMatrix2D invCovariance = decmpRes.solve(DoubleMatrixUtils.getIdentityMatrix2D(4));

    double z = ma.getInnerProduct(delta, ma.multiply(invCovariance, delta));
    z = Math.sqrt(z);

    assertTrue(chiSqDoF < 4.0);

    //    System.out.println("chiSqr: " + res.getChiSq());
    //    System.out.println("params: " + res.getParameters());
    //    System.out.println("covariance: " + res.getCovariance());
    //    System.out.println("z: " + z);
  }

  private Function1D<DoubleMatrix1D, Double> getChiSqFunction(final DoubleMatrix1D x, final DoubleMatrix1D y,
      DoubleMatrix1D sigma, final ParameterizedFunction<Double, DoubleMatrix1D, Double> paramFunc) {

    final int n = x.getNumberOfElements();
    if (y.getNumberOfElements() != n) {
      throw new IllegalArgumentException("y wrong length");
    }
    if (sigma.getNumberOfElements() != n) {
      throw new IllegalArgumentException("sigma wrong length");
    }

    final double[] invSigmaSq = new double[n];
    for (int i = 0; i < n; i++) {
      if (sigma.getEntry(i) <= 0.0) {
        throw new IllegalArgumentException("invalide sigma");
      }
      invSigmaSq[i] = 1 / sigma.getEntry(i) / sigma.getEntry(i);
    }

    Function1D<DoubleMatrix1D, Double> func = new Function1D<DoubleMatrix1D, Double>() {
      @Override
      public Double evaluate(DoubleMatrix1D params) {
        double sum = 0;
        for (int k = 0; k < n; k++) {
          sum += invSigmaSq[k] * UtilFunctions.square(y.getEntry(k) - paramFunc.evaluate(x.getEntry(k), params));
        }
        return sum;
      }

    };

    return func;
  }

}