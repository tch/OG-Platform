/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db.equity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.opengamma.financial.security.db.CurrencyBean;
import com.opengamma.financial.security.db.ExchangeBean;
import com.opengamma.financial.security.db.SecurityBean;

/**
 * A concrete, JavaBean-based implementation of {@link Security}. 
 *
 * @author kirk
 */

public class EquitySecurityBean extends SecurityBean {
  private ExchangeBean _exchange;
  private String _shortName;
  private String _companyName;
  private CurrencyBean _currency;
  private GICSCodeBean _gicsCode;
  
  // Identifiers that might be valid for equities:
  // - Bloomberg ticker (in BbgId)
  // - CUSIP (in CUSIP)
  // - ISIN (in ISIN)
  // - Bloomberg Unique ID (in BbgUniqueId)
    
  public void setShortName(final String shortName) {
    _shortName = shortName;
  }
  
  public String getShortName() {
    return _shortName;
  }

  /**
   * @return the exchange
   */
  public ExchangeBean getExchange() {
    return _exchange;
  }

  /**
   * @param exchange the exchange to set
   */
  public void setExchange(ExchangeBean exchange) {
    _exchange = exchange;
  }

  /**
   * @return the companyName
   */
  public String getCompanyName() {
    return _companyName;
  }

  /**
   * @param companyName the companyName to set
   */
  public void setCompanyName(String companyName) {
    _companyName = companyName;
  }

  /**
   * @return the currency
   */
  public CurrencyBean getCurrency() {
    return _currency;
  }

  /**
   * @param currency the currency to set
   */
  public void setCurrency(CurrencyBean currency) {
    _currency = currency;
  }

  /**
   * @return the gicsCode
   */
  public GICSCodeBean getGICSCode() {
    return _gicsCode;
  }

  /**
   * @param gicsCode the gicsCode to set
   */
  public void setGICSCode(GICSCodeBean gicsCode) {
    _gicsCode = gicsCode;
  }

  public boolean equals(Object other) {
    if (!(other instanceof EquitySecurityBean)) {
      return false;
    }
    EquitySecurityBean equity = (EquitySecurityBean) other;
    if (getId() != -1 && equity.getId() != -1) {
      return getId().longValue() == equity.getId().longValue();
    }
    return new EqualsBuilder().append(getExchange(), equity.getExchange())
                              .append(getCompanyName(), equity.getCompanyName())
                              .append(getCurrency(), equity.getCurrency())
                              .append(getEffectiveDateTime(), equity.getEffectiveDateTime())
                              .append(isDeleted(), equity.isDeleted()).isEquals(); 
  }
  
  public int hashCode() {
    return new HashCodeBuilder().append(getExchange())
                                .append(getCompanyName())
                                .append(getCurrency())
                                .append(getEffectiveDateTime())
                                .append(isDeleted())
                                .toHashCode(); 
  }
  
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
