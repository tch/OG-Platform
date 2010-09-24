/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.math.minimization;

import org.junit.Test;

/**
 * 
 */
public class QuasiNewtonTest extends MultidimensionalMinimizerWithGradiantTestCase {
  private static final QuasiNewtonInverseHessianUpdate BFGS = new BroydenFletcherGoldfarbShannoInverseHessianUpdate();
  private static final QuasiNewtonInverseHessianUpdate DFP = new DavidonFletcherPowellInverseHessianUpdate();
  private static final double EPS = 1e-8;

  @Test
  public void solvingRosenbrockTest() {
    super.testSolvingRosenbrock(new QuasiNewtonVectorMinimizer(EPS, EPS, 100, DFP), EPS);
    super.testSolvingRosenbrock(new QuasiNewtonVectorMinimizer(EPS, EPS, 200, BFGS), EPS);
  }

  //Quasi Newton fails to solve Rosenbrock when finite difference gradients are used - small errors seem to build up in the inverse Hessian estimate 
  // @Test
  // public void solvingRosenbrockTestWithoutGradient() {
  //   super.testSolvingRosenbrockWithoutGradient(new QuasiNewtonVectorMinimizer(EPS, EPS, 100, DFP), EPS);
  //  super.testSolvingRosenbrockWithoutGradient(new QuasiNewtonVectorMinimizer(EPS, EPS, 500, BFGS), EPS);
  // }

  @Test
  public void solvingCoupledRosenbrockTest() {
    super.testSolvingCoupledRosenbrock(new QuasiNewtonVectorMinimizer(EPS, EPS, 1000, DFP), EPS);
    super.testSolvingCoupledRosenbrock(new QuasiNewtonVectorMinimizer(EPS, EPS, 1000, BFGS), EPS);
  }

}