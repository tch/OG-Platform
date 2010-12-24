/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.security.hibernate;

/**
 * Hibernate bean for storing a business day convention.
 */
public class BusinessDayConventionBean extends EnumBean {

  protected BusinessDayConventionBean() {
  }

  public BusinessDayConventionBean(String conventionName) {
    super(conventionName);
  }

}