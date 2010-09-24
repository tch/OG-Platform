/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */

package com.opengamma.financial.security.master.db.hibernate.swap;

import com.opengamma.financial.security.master.db.hibernate.CurrencyBean;
import com.opengamma.financial.security.master.db.hibernate.IdentifierBean;
import com.opengamma.financial.security.swap.Notional;

/**
 * A bean representation of a {@link Notional}.
 */
public class NotionalBean {

  // No identifier as this will be a component of the SwapLeg bean
  private NotionalType _notionalType;
  private CurrencyBean _currency;
  private double _amount;
  private IdentifierBean _identifier;

  public NotionalType getNotionalType() {
    return _notionalType;
  }

  public void setNotionalType(final NotionalType notionalType) {
    _notionalType = notionalType;
  }

  /**
   * Gets the currency field.
   * @return the currency
   */
  public CurrencyBean getCurrency() {
    return _currency;
  }

  /**
   * Sets the currency field.
   * @param currency  the currency
   */
  public void setCurrency(CurrencyBean currency) {
    _currency = currency;
  }

  /**
   * Gets the amount field.
   * @return the amount
   */
  public double getAmount() {
    return _amount;
  }

  /**
   * Sets the amount field.
   * @param amount  the amount
   */
  public void setAmount(double amount) {
    _amount = amount;
  }

  /**
   * Gets the identifier field.
   * @return the identifier
   */
  public IdentifierBean getIdentifier() {
    return _identifier;
  }

  /**
   * Sets the identifier field.
   * @param identifier  the identifier
   */
  public void setIdentifier(IdentifierBean identifier) {
    _identifier = identifier;
  }

}