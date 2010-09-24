/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.model.tree;

/**
 * 
 * @param <T>
 */
public interface Lattice<T> {

  T[][] getTree();

  T getNode(int step, int node);
}