/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.engine.position.Portfolio;
import com.opengamma.engine.position.PortfolioNode;
import com.opengamma.engine.position.Position;
import com.opengamma.engine.security.Security;

/**
 * The type that computation will be base on.
 */
public enum ComputationTargetType {

  /**
   * A set of positions (a portfolio node, or whole portfolio).
   */
  PORTFOLIO_NODE,
  /**
   * A position.
   */
  POSITION,
  /**
   * A security.
   */
  SECURITY,
  /**
   * A simple type, effectively "anything else".
   */
  PRIMITIVE;

  /**
   * Checks if the type is compatible with the target.
   * @param target The target to check for compatibility
   * @return true if compatible
   */
  public boolean isCompatible(final Object target) {
    switch(this) {
      case PORTFOLIO_NODE:
        return (target instanceof PortfolioNode || target instanceof Portfolio);
      case POSITION:
        return (target instanceof Position);
      case SECURITY:
        return (target instanceof Security);
      case PRIMITIVE:
        return (target instanceof Portfolio == false &&
                target instanceof PortfolioNode == false &&
                target instanceof Position == false &&
                target instanceof Security == false);
      default:
        throw new OpenGammaRuntimeException("Unhandled computation target type: " + this);
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Derives the type for the specified target.
   * @param target  the target to introspect, may be null
   * @return the type, not null
   */
  public static ComputationTargetType determineFromTarget(final Object target) {
    if (target instanceof Portfolio) {
      return PORTFOLIO_NODE;
    }
    if (target instanceof PortfolioNode) {
      return PORTFOLIO_NODE;
    }
    if (target instanceof Position) {
      return POSITION;
    }
    if (target instanceof Security) {
      return SECURITY;
    }
    return PRIMITIVE;  // anything else
  }

}