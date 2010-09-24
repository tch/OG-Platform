/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security;

import com.opengamma.financial.security.bond.BondSecurity;
import com.opengamma.financial.security.bond.BondSecurityVisitor;
import com.opengamma.financial.security.cash.CashSecurity;
import com.opengamma.financial.security.cash.CashSecurityVisitor;
import com.opengamma.financial.security.equity.EquitySecurity;
import com.opengamma.financial.security.equity.EquitySecurityVisitor;
import com.opengamma.financial.security.fra.FRASecurity;
import com.opengamma.financial.security.fra.FRASecurityVisitor;
import com.opengamma.financial.security.future.FutureSecurity;
import com.opengamma.financial.security.future.FutureSecurityVisitor;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.financial.security.option.OptionSecurityVisitor;
import com.opengamma.financial.security.swap.SwapSecurity;
import com.opengamma.financial.security.swap.SwapSecurityVisitor;

/**
 * Adapter for visiting all concrete asset classes.
 * 
 * @param <T> Return type for visitor.
 */
public class FinancialSecurityVisitorAdapter<T> implements FinancialSecurityVisitor<T> {

  private final BondSecurityVisitor<T> _bondSecurityVisitor;
  private final CashSecurityVisitor<T> _cashSecurityVisitor;
  private final EquitySecurityVisitor<T> _equitySecurityVisitor;
  private final FRASecurityVisitor<T> _fraSecurityVisitor;
  private final FutureSecurityVisitor<T> _futureSecurityVisitor;
  private final OptionSecurityVisitor<T> _optionSecurityVisitor;
  private final SwapSecurityVisitor<T> _swapSecurityVisitor;

  public FinancialSecurityVisitorAdapter(BondSecurityVisitor<T> bondSecurityVisitor,
      CashSecurityVisitor<T> cashSecurityVisitor, EquitySecurityVisitor<T> equitySecurityVisitor,
      FRASecurityVisitor<T> fraSecurityVisitor, FutureSecurityVisitor<T> futureSecurityVisitor,
      OptionSecurityVisitor<T> optionSecurityVisitor, SwapSecurityVisitor<T> swapSecurityVisitor) {
    _bondSecurityVisitor = bondSecurityVisitor;
    _cashSecurityVisitor = cashSecurityVisitor;
    _equitySecurityVisitor = equitySecurityVisitor;
    _fraSecurityVisitor = fraSecurityVisitor;
    _futureSecurityVisitor = futureSecurityVisitor;
    _optionSecurityVisitor = optionSecurityVisitor;
    _swapSecurityVisitor = swapSecurityVisitor;
  }

  // FinancialSecurityVisitor

  @Override
  public T visitBondSecurity(BondSecurity security) {
    return (_bondSecurityVisitor != null) ? security.accept(_bondSecurityVisitor) : null;
  }

  @Override
  public T visitCashSecurity(CashSecurity security) {
    return (_cashSecurityVisitor != null) ? security.accept(_cashSecurityVisitor) : null;
  }

  @Override
  public T visitEquitySecurity(EquitySecurity security) {
    return (_equitySecurityVisitor != null) ? security.accept(_equitySecurityVisitor) : null;
  }

  @Override
  public T visitFRASecurity(FRASecurity security) {
    return (_fraSecurityVisitor != null) ? security.accept(_fraSecurityVisitor) : null;
  }

  @Override
  public T visitFutureSecurity(FutureSecurity security) {
    return (_futureSecurityVisitor != null) ? security.accept(_futureSecurityVisitor) : null;
  }

  @Override
  public T visitOptionSecurity(OptionSecurity security) {
    return (_optionSecurityVisitor != null) ? security.accept(_optionSecurityVisitor) : null;
  }

  @Override
  public T visitSwapSecurity(SwapSecurity security) {
    return (_swapSecurityVisitor != null) ? security.accept(_swapSecurityVisitor) : null;
  }

}