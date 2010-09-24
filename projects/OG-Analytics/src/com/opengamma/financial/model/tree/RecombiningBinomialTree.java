/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.tree;

import com.opengamma.math.function.Function1D;

/**
 * 
 * @param <T>
 */
public class RecombiningBinomialTree<T> extends RecombiningTree<T> {
  /** Number of nodes at each level */
  public static final Function1D<Integer, Integer> NODES = new Function1D<Integer, Integer>() {

    @Override
    public Integer evaluate(final Integer i) {
      return i + 1;
    }
  };

  public RecombiningBinomialTree(final T[][] data) {
    super(data);
  }

  @Override
  protected int getMaxNodesForStep(final int step) {
    return NODES.evaluate(step);
  }

}