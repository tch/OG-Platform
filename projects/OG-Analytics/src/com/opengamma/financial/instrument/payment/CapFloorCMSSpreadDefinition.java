/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.instrument.payment;

import javax.time.calendar.ZonedDateTime;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;

import com.opengamma.financial.convention.daycount.DayCount;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.index.CMSIndex;
import com.opengamma.financial.instrument.swap.SwapFixedIborDefinition;
import com.opengamma.financial.interestrate.payments.CapFloorCMSSpread;
import com.opengamma.financial.interestrate.payments.CouponFixed;
import com.opengamma.financial.interestrate.payments.Payment;
import com.opengamma.financial.interestrate.swap.definition.FixedCouponSwap;
import com.opengamma.util.money.Currency;
import com.opengamma.util.timeseries.DoubleTimeSeries;

/**
 * Class describing a caplet/floorlet on CMS spread. The notional is positive for long the option and negative for short the option.
 * The pay-off of the instrument is a cap/floor on the difference between the first CMS rate and the second CMS rate. 
 * Both swaps underlying the CMS need to have the same settlement date.
 */
public class CapFloorCMSSpreadDefinition extends CouponFloatingDefinition implements CapFloor {

  /**
   * The swap underlying the first CMS.
   */
  private final SwapFixedIborDefinition _underlyingSwap1;
  /**
   * The index associated to the first CMS.
   */
  private final CMSIndex _cmsIndex1;
  /**
   * The swap underlying the second CMS.
   */
  private final SwapFixedIborDefinition _underlyingSwap2;
  /**
   * The index associated to the second CMS.
   */
  private final CMSIndex _cmsIndex2;
  /**
   * The cap/floor strike.
   */
  private final double _strike;
  /**
   * The cap (true) / floor (false) flag.
   */
  private final boolean _isCap;

  /**
   * Cap/floor CMS spread constructor from all the details.
   * @param currency The payment currency.
   * @param paymentDate Coupon payment date.
   * @param accrualStartDate Start date of the accrual period.
   * @param accrualEndDate End date of the accrual period.
   * @param accrualFactor Accrual factor of the accrual period.
   * @param notional Coupon notional.
   * @param fixingDate The coupon fixing date.
   * @param underlyingSwap1 The swap underlying the first CMS.
   * @param cmsIndex1 The index associated to the first CMS.
   * @param underlyingSwap2 The swap underlying the second CMS.
   * @param cmsIndex2 The index associated to the second CMS.
   * @param strike The strike
   * @param isCap The cap (true) /floor (false) flag.
   */
  public CapFloorCMSSpreadDefinition(final Currency currency, final ZonedDateTime paymentDate, final ZonedDateTime accrualStartDate, final ZonedDateTime accrualEndDate, final double accrualFactor,
      final double notional,
      final ZonedDateTime fixingDate, final SwapFixedIborDefinition underlyingSwap1, final CMSIndex cmsIndex1, final SwapFixedIborDefinition underlyingSwap2, final CMSIndex cmsIndex2,
      final double strike, final boolean isCap) {
    super(currency, paymentDate, accrualStartDate, accrualEndDate, accrualFactor, notional, fixingDate);
    Validate.notNull(underlyingSwap1, "underlying swap");
    Validate.notNull(cmsIndex1, "CMS index");
    Validate.notNull(underlyingSwap2, "underlying swap");
    Validate.notNull(cmsIndex2, "CMS index");
    Validate.notNull(underlyingSwap1.getFixedLeg().getNthPayment(0).getAccrualStartDate() == underlyingSwap2.getFixedLeg().getNthPayment(0).getAccrualStartDate(), "identic settlement date");
    _underlyingSwap1 = underlyingSwap1;
    _cmsIndex1 = cmsIndex1;
    _underlyingSwap2 = underlyingSwap2;
    _cmsIndex2 = cmsIndex2;
    _strike = strike;
    _isCap = isCap;
  }

  /**
   * Gets the underlying swap associated to the first CMS.
   * @return The underlying swap
   */
  public SwapFixedIborDefinition getUnderlyingSwap1() {
    return _underlyingSwap1;
  }

  /**
   * Gets the index associated to the first CMS.
   * @return The CMS index.
   */
  public CMSIndex getCmsIndex1() {
    return _cmsIndex1;
  }

  /**
   * Gets the underlying swap associated to the second CMS.
   * @return The underlying swap
   */
  public SwapFixedIborDefinition getUnderlyingSwap2() {
    return _underlyingSwap2;
  }

  /**
   * Gets the index associated to the second CMS.
   * @return The CMS index.
   */
  public CMSIndex getCmsIndex2() {
    return _cmsIndex2;
  }

  @Override
  public double getStrike() {
    return _strike;
  }

  @Override
  public boolean isCap() {
    return _isCap;
  }

  @Override
  /**
   * The "fixing" is the difference between the first and the second CMS rates fixings.
   */
  public double payOff(final double fixing) {
    final double omega = (_isCap) ? 1.0 : -1.0;
    return Math.max(omega * (fixing - _strike), 0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + _cmsIndex1.hashCode();
    result = prime * result + _cmsIndex2.hashCode();
    result = prime * result + (_isCap ? 1231 : 1237);
    long temp;
    temp = Double.doubleToLongBits(_strike);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + _underlyingSwap1.hashCode();
    result = prime * result + _underlyingSwap2.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CapFloorCMSSpreadDefinition other = (CapFloorCMSSpreadDefinition) obj;
    if (!ObjectUtils.equals(_cmsIndex1, other._cmsIndex1)) {
      return false;
    }
    if (!ObjectUtils.equals(_cmsIndex2, other._cmsIndex2)) {
      return false;
    }
    if (_isCap != other._isCap) {
      return false;
    }
    if (Double.doubleToLongBits(_strike) != Double.doubleToLongBits(other._strike)) {
      return false;
    }
    if (!ObjectUtils.equals(_underlyingSwap1, other._underlyingSwap1)) {
      return false;
    }
    if (!ObjectUtils.equals(_underlyingSwap2, other._underlyingSwap2)) {
      return false;
    }
    return true;
  }

  @Override
  public Payment toDerivative(final ZonedDateTime date, final String... yieldCurveNames) {
    Validate.notNull(date, "date");
    Validate.isTrue(date.isBefore(getFixingDate()), "Do not have any fixing data but are asking for a derivative after the fixing date");
    Validate.notNull(yieldCurveNames, "yield curve names");
    Validate.isTrue(yieldCurveNames.length > 1, "at least two curves required");
    Validate.isTrue(!date.isAfter(getPaymentDate()), "date is after payment date");
    // First curve used for discounting. If two curves, the same forward is used for both swaps; 
    // if more than two curves, the second is used for the forward of the first swap and the third for the forward of the second swap.
    final DayCount actAct = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
    final String fundingCurveName = yieldCurveNames[0];
    final double paymentTime = actAct.getDayCountFraction(date, getPaymentDate());
    // CMS spread is not fixed yet, all the details are required.
    final double fixingTime = actAct.getDayCountFraction(date, getFixingDate());
    final double settlementTime = actAct.getDayCountFraction(date, _underlyingSwap1.getFixedLeg().getNthPayment(0).getAccrualStartDate());
    final FixedCouponSwap<Payment> swap1 = _underlyingSwap1.toDerivative(date, yieldCurveNames);
    String[] yieldCurveNames2;
    if (yieldCurveNames.length == 2) {
      yieldCurveNames2 = yieldCurveNames;
    } else {
      yieldCurveNames2 = new String[] {yieldCurveNames[0], yieldCurveNames[2]};
    }
    final FixedCouponSwap<Payment> swap2 = _underlyingSwap2.toDerivative(date, yieldCurveNames2);
    return new CapFloorCMSSpread(getCurrency(), paymentTime, getPaymentYearFraction(), getNotional(), fixingTime, swap1, _cmsIndex1, swap2, _cmsIndex2, settlementTime, _strike, _isCap,
          fundingCurveName);
  }

  @Override
  public Payment toDerivative(final ZonedDateTime date, final DoubleTimeSeries<ZonedDateTime> data, final String... yieldCurveNames) {
    Validate.notNull(date, "date");
    Validate.notNull(yieldCurveNames, "yield curve names");
    Validate.isTrue(yieldCurveNames.length > 1, "at least two curves required");
    Validate.isTrue(!date.isAfter(getPaymentDate()), "date is after payment date");
    // First curve used for discounting. If two curves, the same forward is used for both swaps; 
    // if more than two curves, the second is used for the forward of the first swap and the third for the forward of the second swap.
    final DayCount actAct = DayCountFactory.INSTANCE.getDayCount("Actual/Actual ISDA");
    final String fundingCurveName = yieldCurveNames[0];
    final double paymentTime = actAct.getDayCountFraction(date, getPaymentDate());
    if (date.isAfter(getFixingDate()) || date.equals(getFixingDate())) { // The CMS coupon has already fixed, it is now a fixed coupon.
      final double fixedRate = data.getValue(getFixingDate());
      return new CouponFixed(getCurrency(), paymentTime, fundingCurveName, getPaymentYearFraction(), getNotional(), payOff(fixedRate));
    }
    // CMS spread is not fixed yet, all the details are required.
    final double fixingTime = actAct.getDayCountFraction(date, getFixingDate());
    final double settlementTime = actAct.getDayCountFraction(date, _underlyingSwap1.getFixedLeg().getNthPayment(0).getAccrualStartDate());
    final FixedCouponSwap<Payment> swap1 = _underlyingSwap1.toDerivative(date, yieldCurveNames);
    String[] yieldCurveNames2;
    if (yieldCurveNames.length == 2) {
      yieldCurveNames2 = yieldCurveNames;
    } else {
      yieldCurveNames2 = new String[] {yieldCurveNames[0], yieldCurveNames[2]};
    }
    final FixedCouponSwap<Payment> swap2 = _underlyingSwap2.toDerivative(date, yieldCurveNames2);
    return new CapFloorCMSSpread(getCurrency(), paymentTime, getPaymentYearFraction(), getNotional(), fixingTime, swap1, _cmsIndex1, swap2, _cmsIndex2, settlementTime, _strike, _isCap,
          fundingCurveName);
  }

}
