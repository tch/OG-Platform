/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.convention.daycount;

import javax.time.calendar.ZonedDateTime;

/**
 * Base class for 'actual' style day counts.
 */
public abstract class ActualTypeDayCount extends StatelessDayCount {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  @Override
  public abstract double getAccruedInterest(final ZonedDateTime previousCouponDate, final ZonedDateTime date, final ZonedDateTime nextCouponDate, final double coupon, final double paymentsPerYear);

}
