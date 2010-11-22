/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.core.position.impl;

import com.opengamma.core.position.PortfolioNode;
import com.opengamma.core.position.Position;

/**
 * A simple no-op implementation of {@code PortfolioNodeTraversalCallback}.
 * Subclasses should override the particular methods they require.
 */
public class AbstractPortfolioNodeTraversalCallback implements PortfolioNodeTraversalCallback {

  @Override
  public void postOrderOperation(PortfolioNode portfolioNode) {
  }

  @Override
  public void postOrderOperation(Position position) {
  }

  @Override
  public void preOrderOperation(PortfolioNode portfolioNode) {
  }

  @Override
  public void preOrderOperation(Position position) {
  }

}