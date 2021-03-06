/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.model.finitedifference;

import org.apache.commons.lang.Validate;

/**
 * 
 */
public class PDEFullResults1D {

  private final double[][] _f;
  @SuppressWarnings("unused")
  private final PDEGrid1D _grid;
  @SuppressWarnings("unused")
  private final boolean _fullDataSet;

  public PDEFullResults1D(final PDEGrid1D grid, final double[][] fullSolverData) {
    Validate.isTrue(grid.getNumTimeNodes() == fullSolverData.length, "time steps in grid not equal to that in data");
    Validate.isTrue(grid.getNumSpaceNodes() == fullSolverData[0].length, "space steps in grid not equal to that in data");
    _grid = grid;
    _f = fullSolverData;
    _fullDataSet = true;
  }

  // penultimate
  public PDEFullResults1D(final PDEGrid1D grid, final double[] penultimateTimeStep, final double[] finalTimeStep) {
    Validate.isTrue(grid.getNumSpaceNodes() == penultimateTimeStep.length, "space steps in grid not equal to that in data");
    Validate.isTrue(grid.getNumSpaceNodes() == finalTimeStep.length, "space steps in grid not equal to that in data");
    _f = new double[2][];
    _f[0] = finalTimeStep;
    _f[1] = penultimateTimeStep;
    _grid = grid;
    _fullDataSet = false;
  }

  double getFunctionValue(final int index) {
    return _f[0][index];
  }

}
