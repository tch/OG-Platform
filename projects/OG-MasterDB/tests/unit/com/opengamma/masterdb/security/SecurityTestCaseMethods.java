/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.masterdb.security;

/**
 * Generic tests for all security types implementation.
 */
public interface SecurityTestCaseMethods {

  void testCorporateBondSecurity();

  void testGovernmentBondSecurity();

  void testMunicipalBondSecurity();

  void testCashSecurity();

  void testEquitySecurity();

  void testFRASecurity();

  void testAgricultureFutureSecurity();

  void testBondFutureSecurity();

  void testEnergyFutureSecurity();

  void testFXFutureSecurity();

  void testIndexFutureSecurity();

  void testInterestRateFutureSecurity();

  void testMetalFutureSecurity();

  void testStockFutureSecurity();

  void testBondOptionSecurity();

  void testEquityOptionSecurity();

  void testFutureOptionSecurity();

  void testFXOptionSecurity();

  void testOptionOptionSecurity();

  void testSwapOptionSecurity();

  void testForwardSwapSecurity();

  void testSwapSecurity();

}
