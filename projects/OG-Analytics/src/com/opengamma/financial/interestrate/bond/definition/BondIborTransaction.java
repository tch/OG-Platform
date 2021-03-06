/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.bond.definition;

import com.opengamma.financial.interestrate.payments.Payment;
import com.opengamma.financial.interestrate.payments.PaymentFixed;

/**
 * Describes a transaction on a Ibor floating coupon bond (Floating Rate Note) issue.
 */
public class BondIborTransaction extends BondTransaction<Payment> {

  /**
   * Ibor coupon bond transaction constructor from transaction details.
   * @param bondTransaction The bond underlying the transaction.
   * @param quantity The number of bonds purchased (can be negative or positive).
   * @param settlement Transaction settlement payment (time and amount).
   * @param bondStandard Description of the underlying bond with standard settlement date.
   * @param spotTime Description of the standard spot time.
   * @param notionalStandard The notional at the standard spot time.
   */
  public BondIborTransaction(BondIborDescription bondTransaction, double quantity, PaymentFixed settlement, BondIborDescription bondStandard, double spotTime, double notionalStandard) {
    super(bondTransaction, quantity, settlement, bondStandard, spotTime, notionalStandard);
  }

}
