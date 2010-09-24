/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.number;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * 
 */
public class ComplexNumberTest {
  private static final ComplexNumber Z1 = new ComplexNumber(1, 2);
  private static final ComplexNumber Z2 = new ComplexNumber(1, 2);
  private static final ComplexNumber Z3 = new ComplexNumber(1, 3);
  private static final ComplexNumber Z4 = new ComplexNumber(2, 2);
  private static final ComplexNumber Z5 = new ComplexNumber(2, 3);

  @Test(expected = UnsupportedOperationException.class)
  public void testByteValue() {
    Z1.byteValue();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testIntValue() {
    Z1.intValue();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testLongValue() {
    Z1.longValue();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testFloatValue() {
    Z1.floatValue();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDoubleValue() {
    Z1.doubleValue();
  }

  @Test
  public void test() {
    assertEquals(Double.valueOf(1), Double.valueOf(Z1.getReal()));
    assertEquals(Double.valueOf(2), Double.valueOf(Z1.getImaginary()));
    assertEquals(Z1, Z2);
    assertEquals(Z1.hashCode(), Z2.hashCode());
    assertEquals("1.0 + 2.0i", Z1.toString());
    assertEquals("1.0", new ComplexNumber(1, 0).toString());
    assertEquals("2.3i", new ComplexNumber(0, 2.3).toString());
    assertFalse(Z1.equals(Z3));
    assertFalse(Z1.equals(Z4));
    assertFalse(Z1.equals(Z5));
  }
}