/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.ConstantYieldCurve;
import com.opengamma.financial.model.interestrate.curve.YieldAndDiscountCurve;

/**
 * 
 */
public class YieldCurveBundleTest {
  private static final String[] NAMES = new String[] {"A", "B", "C"};
  private static final YieldAndDiscountCurve[] CURVES = new YieldAndDiscountCurve[3];
  private static final Map<String, YieldAndDiscountCurve> MAP = new HashMap<String, YieldAndDiscountCurve>();
  private static final YieldCurveBundle BUNDLE;

  static {
    CURVES[0] = new ConstantYieldCurve(0.03);
    CURVES[1] = new ConstantYieldCurve(0.04);
    CURVES[2] = new ConstantYieldCurve(0.05);
    for (int i = 0; i < 3; i++) {
      MAP.put(NAMES[i], CURVES[i]);
    }
    BUNDLE = new YieldCurveBundle(NAMES, CURVES);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameArray() {
    new YieldCurveBundle(null, CURVES);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurveArray() {
    new YieldCurveBundle(NAMES, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongNameArrayLength() {
    new YieldCurveBundle(new String[] {"A", "B"}, CURVES);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongCurveArrayLength() {
    new YieldCurveBundle(NAMES, new YieldAndDiscountCurve[] {CURVES[0], CURVES[1]});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameInArray() {
    new YieldCurveBundle(new String[] {"A", "B", null}, CURVES);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurveInArray() {
    new YieldCurveBundle(NAMES, new YieldAndDiscountCurve[] {CURVES[0], CURVES[1], null});
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameInMap() {
    final Map<String, YieldAndDiscountCurve> map = new HashMap<String, YieldAndDiscountCurve>();
    for (int i = 0; i < 2; i++) {
      map.put(NAMES[i], CURVES[i]);
    }
    map.put(null, CURVES[2]);
    new YieldCurveBundle(map);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullCurveInMap() {
    final Map<String, YieldAndDiscountCurve> map = new HashMap<String, YieldAndDiscountCurve>();
    for (int i = 0; i < 2; i++) {
      map.put(NAMES[i], CURVES[i]);
    }
    map.put(NAMES[2], null);
    new YieldCurveBundle(map);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNullName() {
    BUNDLE.setCurve(null, CURVES[1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNullCurve() {
    BUNDLE.setCurve("D", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPreviousCurve() {
    BUNDLE.setCurve(NAMES[0], CURVES[2]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetNonExistentCurve() {
    BUNDLE.getCurve("D");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReplaceNonExistentCurve() {
    BUNDLE.replaceCurve("E", CURVES[1]);
  }

  @Test
  public void testGetters() {
    final YieldCurveBundle bundle = new YieldCurveBundle(MAP);
    assertEquals(bundle.size(), 3);
    assertEquals(bundle.getAllNames(), MAP.keySet());
    for (int i = 0; i < 3; i++) {
      assertEquals(bundle.getCurve(NAMES[i]), CURVES[i]);
    }
  }

  @Test
  public void testHashCodeAndEquals() {
    YieldCurveBundle other = new YieldCurveBundle(NAMES, CURVES);
    assertEquals(BUNDLE, other);
    assertEquals(BUNDLE.hashCode(), other.hashCode());
    other = new YieldCurveBundle(MAP);
    assertEquals(BUNDLE, other);
    assertEquals(BUNDLE.hashCode(), other.hashCode());
    other = new YieldCurveBundle(new String[] {NAMES[0], NAMES[1]}, new YieldAndDiscountCurve[] {CURVES[0], CURVES[1]});
    assertFalse(other.equals(BUNDLE));
  }

  @Test
  public void testSetter() {
    final YieldCurveBundle bundle1 = new YieldCurveBundle();
    final YieldCurveBundle bundle2 = new YieldCurveBundle(MAP);
    bundle1.addAll(bundle2);
    assertEquals(bundle1, bundle2);
  }
}