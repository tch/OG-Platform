/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.language;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import org.testng.annotations.Test;
import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsg;

/**
 * Tests the methods in the {@link ValueUtil} class.
 */
public class ValueUtilTest {

  @Test
  public void testBoolean() {
    final Value value = ValueUtil.of(true);
    assertNotNull(value);
    assertEquals(Boolean.TRUE, value.getBoolValue());
  }

  @Test
  public void testDouble() {
    final Value value = ValueUtil.of(3.14);
    assertNotNull(value);
    assertEquals(3.14, value.getDoubleValue(), 0);
  }

  @Test
  public void testError() {
    final Value value = ValueUtil.ofError(42);
    assertNotNull(value);
    assertEquals((Integer) 42, value.getErrorValue());
  }

  @Test
  public void testInteger() {
    final Value value = ValueUtil.of(69);
    assertNotNull(value);
    assertEquals((Integer) 69, value.getIntValue());
  }

  @Test
  public void testMessage() {
    final Value value = ValueUtil.of(FudgeContext.EMPTY_MESSAGE);
    assertNotNull(value);
    assertEquals(FudgeContext.EMPTY_MESSAGE, value.getMessageValue());
  }

  @Test
  public void testString() {
    final Value value = ValueUtil.of("Foo");
    assertNotNull(value);
    assertEquals("Foo", value.getStringValue());
  }
  
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullString() {
    ValueUtil.of((String) null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNullMessage() {
    ValueUtil.of((FudgeMsg) null);
  }

}
