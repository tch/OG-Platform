/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.ircurve;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.time.calendar.LocalDate;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.opengamma.id.Identifier;
import com.opengamma.math.interpolation.Interpolator1D;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;

/**
 * 
 *
 */
public class InterpolatedYieldCurveSpecification implements Serializable {
  private final LocalDate _curveDate;
  private final Currency _currency;
  private final String _name;
  private final Interpolator1D<?> _interpolator;
  private final Set<FixedIncomeStripWithIdentifier> _strips = new LinkedHashSet<FixedIncomeStripWithIdentifier>();
  private final Identifier _region;
  
  public InterpolatedYieldCurveSpecification(LocalDate curveDate, String name, Currency currency,  
      Interpolator1D<?> interpolator, Collection<FixedIncomeStripWithIdentifier> resolvedStrips, Identifier region) {
    Validate.notNull(curveDate, "CurveDate");
    Validate.notNull(currency, "Currency");
    Validate.notNull(interpolator, "Interpolator1D");
    Validate.notNull(resolvedStrips, "ResolvedStrips");
    Validate.notNull(region, "RegionID");
    // Name can be null.
    _curveDate = curveDate;
    _currency = currency;
    _name = name;
    _interpolator = interpolator;
    _region = region;
    for (FixedIncomeStripWithIdentifier strip : resolvedStrips) {
      addStrip(strip);
    }
  }
  
  public void addStrip(FixedIncomeStripWithIdentifier strip) {
    ArgumentChecker.notNull(strip, "Strip");
    _strips.add(strip);
  }
  
  /**
   * Gets the region field.
   * @return the region
   */
  public Identifier getRegion() {
    return _region;
  }

  /**
   * @return the curve date
   */
  public LocalDate getCurveDate() {
    return _curveDate;
  }

  /**
   * @return the currency
   */
  public Currency getCurrency() {
    return _currency;
  }

  /**
   * @return the name
   */
  public String getName() {
    return _name;
  }

  /**
   * @return the interpolator
   */
  public Interpolator1D<?> getInterpolator() {
    return _interpolator;
  }

  /**
   * @return the strips
   */
  public Set<FixedIncomeStripWithIdentifier> getStrips() {
    return Collections.unmodifiableSet(_strips);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof InterpolatedYieldCurveSpecification)) {
      return false;
    }
    InterpolatedYieldCurveSpecification other = (InterpolatedYieldCurveSpecification) obj;
    if (!ObjectUtils.equals(_currency, other._currency)) {
      return false;
    }
    if (!ObjectUtils.equals(_name, other._name)) {
      return false;
    }
    if (!ObjectUtils.equals(_interpolator, other._interpolator)) {
      return false;
    }
    if (!ObjectUtils.equals(_strips, other._strips)) {
      return false;
    }
    if (!ObjectUtils.equals(_region, other._region)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int prime = 37;
    int result = 1;
    result = (result * prime) + _currency.hashCode();
    if (_name != null) {
      result = (result * prime) + _name.hashCode(); 
    }
    // since currency/name/date are a candidate key we leave it at that.
    return result;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
