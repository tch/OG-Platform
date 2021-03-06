/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.engine;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import java.math.BigDecimal;

import javax.time.calendar.OffsetDateTime;

import com.opengamma.core.position.Portfolio;
import com.opengamma.core.position.Position;
import com.opengamma.core.position.PositionSource;
import com.opengamma.core.position.impl.CounterpartyImpl;
import com.opengamma.core.position.impl.MockPositionSource;
import com.opengamma.core.position.impl.PortfolioImpl;
import com.opengamma.core.position.impl.PortfolioNodeImpl;
import com.opengamma.core.position.impl.PositionImpl;
import com.opengamma.core.position.impl.TradeImpl;
import com.opengamma.core.security.Security;
import com.opengamma.core.security.SecuritySource;
import com.opengamma.engine.test.MockSecurity;
import com.opengamma.engine.test.MockSecuritySource;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Test DefaultComputationTargetResolver.
 */
@Test
public class DefaultComputationTargetResolverTest {

  private static final Portfolio PORTFOLIO = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
  private static final PortfolioNodeImpl NODE = new PortfolioNodeImpl(UniqueIdentifier.of("A", "B"), "Name");
  private static final Position POSITION = new PositionImpl(UniqueIdentifier.of("Test", "1"), new BigDecimal(1), IdentifierBundle.EMPTY);
  private static final Security SECURITY = new MockSecurity(UniqueIdentifier.of("Test", "SEC"), "Test security", "EQUITY", IdentifierBundle.EMPTY);

  //-------------------------------------------------------------------------
  public void test_constructor() {
    SecuritySource secSource = new MockSecuritySource();
    PositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    assertEquals(secSource, test.getSecuritySource());
    assertEquals(posSource, test.getPositionSource());
  }

  //-------------------------------------------------------------------------
  public void test_resolve_portfolio() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    posSource.addPortfolio(PORTFOLIO);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(PORTFOLIO);
    ComputationTarget expected = new ComputationTarget(PORTFOLIO);
    assertEquals(expected, test.resolve(spec));
  }

  public void test_resolve_portfolioNode() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    PortfolioImpl p = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
    p.getRootNode().addChildNode(NODE);
    posSource.addPortfolio(p);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(NODE);
    ComputationTarget expected = new ComputationTarget(NODE);
    assertEquals(expected, test.resolve(spec));
  }

  public void test_resolve_position() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    PortfolioImpl p = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
    p.getRootNode().addPosition(POSITION);
    posSource.addPortfolio(p);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(POSITION);
    ComputationTarget expected = new ComputationTarget(POSITION);
    assertEquals(expected, test.resolve(spec));
  }
  
  public void test_resolve_trade() {
    OffsetDateTime now = OffsetDateTime.now();
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    PortfolioImpl portfolio = new PortfolioImpl(UniqueIdentifier.of("Test", "1"), "Name");
    PositionImpl position = new PositionImpl(UniqueIdentifier.of("Test", "1"), new BigDecimal(1), IdentifierBundle.EMPTY);
    TradeImpl trade = new TradeImpl(position.getUniqueId(), IdentifierBundle.EMPTY, new BigDecimal(1), new CounterpartyImpl(Identifier.of("CPARTY", "C100")), now.toLocalDate(), now.toOffsetTime());
    trade.setUniqueId(UniqueIdentifier.of("TradeScheme", "1"));
    position.addTrade(trade);
    portfolio.getRootNode().addPosition(position);
    posSource.addPortfolio(portfolio);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(trade);
    ComputationTarget expected = new ComputationTarget(trade);
    assertEquals(expected, test.resolve(spec));
  }
  
  public void test_resolve_security() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    secSource.addSecurity(SECURITY);
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(SECURITY);
    ComputationTarget expected = new ComputationTarget(SECURITY);
    assertEquals(expected, test.resolve(spec));
  }

  public void test_resolve_primitive() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    ComputationTargetSpecification spec = new ComputationTargetSpecification(ComputationTargetType.PRIMITIVE, (UniqueIdentifier) null);
    ComputationTarget expected = new ComputationTarget(ComputationTargetType.PRIMITIVE, null);
    assertEquals(expected, test.resolve(spec));
  }

  @Test(expectedExceptions=NullPointerException.class)
  public void test_resolve_nullSpecification() {
    MockSecuritySource secSource = new MockSecuritySource();
    MockPositionSource posSource = new MockPositionSource();
    DefaultComputationTargetResolver test = new DefaultComputationTargetResolver(secSource, posSource);
    test.resolve(null);
  }

}
