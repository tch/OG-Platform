/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.schedule;

import static org.testng.AssertJUnit.assertArrayEquals;
import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import javax.time.calendar.LocalDate;
import javax.time.calendar.ZonedDateTime;

import com.opengamma.financial.schedule.EndOfMonthScheduleCalculator;
import com.opengamma.financial.schedule.Schedule;
import com.opengamma.util.time.DateUtil;

/**
 * 
 */
public class EndOfMonthScheduleCalculatorTest extends ScheduleCalculatorTestCase {
  private static final EndOfMonthScheduleCalculator CALCULATOR = new EndOfMonthScheduleCalculator();

  @Override
  public Schedule getScheduleCalculator() {
    return CALCULATOR;
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testStartAndEndSameButInvalid1() {
    CALCULATOR.getSchedule(LocalDate.of(2001, 2, 3), LocalDate.of(2001, 2, 3), false, true);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testStartAndEndSameButInvalid2() {
    CALCULATOR.getSchedule(DateUtil.getUTCDate(2001, 2, 3), DateUtil.getUTCDate(2001, 2, 3), false, true);
  }

  @Test
  public void testSameDates1() {
    final LocalDate date = LocalDate.of(2001, 1, 31);
    final LocalDate[] dates = CALCULATOR.getSchedule(date, date, true, true);
    assertEquals(dates.length, 1);
    assertEquals(dates[0], date);
  }

  @Test
  public void testSameDates2() {
    final ZonedDateTime date = DateUtil.getUTCDate(2001, 1, 31);
    final ZonedDateTime[] dates = CALCULATOR.getSchedule(date, date, true, true);
    assertEquals(dates.length, 1);
    assertEquals(dates[0], date);
  }

  @Test
  public void testNoEndDateInRange1() {
    final LocalDate startDate = LocalDate.of(2000, 1, 1);
    final LocalDate endDate = LocalDate.of(2000, 1, 30);
    final LocalDate[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    final LocalDate[] backward = CALCULATOR.getSchedule(startDate, endDate, true, true);
    assertEquals(forward.length, 0);
    assertEquals(backward.length, 0);
  }

  @Test
  public void testNoEndDateInRange2() {
    final ZonedDateTime startDate = DateUtil.getUTCDate(2000, 1, 1);
    final ZonedDateTime endDate = DateUtil.getUTCDate(2000, 1, 30);
    final ZonedDateTime[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    final ZonedDateTime[] backward = CALCULATOR.getSchedule(startDate, endDate, true, true);
    assertEquals(forward.length, 0);
    assertEquals(backward.length, 0);
  }

  @Test
  public void testStartDateIsEnd1() {
    final LocalDate startDate = LocalDate.of(2002, 1, 31);
    final LocalDate endDate = LocalDate.of(2002, 2, 9);
    final LocalDate[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    final LocalDate[] backward = CALCULATOR.getSchedule(startDate, endDate, true, true);
    assertEquals(forward.length, 1);
    assertEquals(backward.length, 1);
    assertEquals(forward[0], startDate);
    assertEquals(backward[0], startDate);
  }

  @Test
  public void testStartDateIsEnd2() {
    final ZonedDateTime startDate = DateUtil.getUTCDate(2002, 1, 31);
    final ZonedDateTime endDate = DateUtil.getUTCDate(2002, 2, 9);
    final ZonedDateTime[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    final ZonedDateTime[] backward = CALCULATOR.getSchedule(startDate, endDate, true, true);
    assertEquals(forward.length, 1);
    assertEquals(backward.length, 1);
    assertEquals(forward[0], startDate);
    assertEquals(backward[0], startDate);
  }

  @Test
  public void test1() {
    final LocalDate startDate = LocalDate.of(2000, 1, 1);
    final LocalDate endDate = LocalDate.of(2002, 2, 9);
    final int months = 25;
    final LocalDate[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, months);
    final LocalDate firstDate = LocalDate.of(2000, 1, 31);
    assertEquals(forward[0], firstDate);
    final LocalDate lastDate = LocalDate.of(2002, 1, 31);
    assertEquals(forward[months - 1], lastDate);
    LocalDate d1;
    for (int i = 1; i < months; i++) {
      d1 = forward[i];
      if (d1.getYear() == forward[i - 1].getYear()) {
        assertEquals(d1.getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), 1);
      } else {
        assertEquals(d1.getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), -11);
      }
      assertEquals(d1.getDayOfMonth(), d1.getMonthOfYear().lengthInDays(d1.toLocalDate().isLeapYear()));
    }
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, true, false), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, true, true), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, false, false), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, false, true), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate), forward);
  }

  @Test
  public void test2() {
    final ZonedDateTime startDate = DateUtil.getUTCDate(2000, 1, 1);
    final ZonedDateTime endDate = DateUtil.getUTCDate(2002, 2, 9);
    final int months = 25;
    final ZonedDateTime[] forward = CALCULATOR.getSchedule(startDate, endDate, false, true);
    assertEquals(forward.length, months);
    final ZonedDateTime firstDate = DateUtil.getUTCDate(2000, 1, 31);
    assertEquals(forward[0], firstDate);
    final ZonedDateTime lastDate = DateUtil.getUTCDate(2002, 1, 31);
    assertEquals(forward[months - 1], lastDate);
    ZonedDateTime d1;
    for (int i = 1; i < months; i++) {
      d1 = forward[i];
      if (d1.getYear() == forward[i - 1].getYear()) {
        assertEquals(d1.getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), 1);
      } else {
        assertEquals(d1.getMonthOfYear().getValue() - forward[i - 1].getMonthOfYear().getValue(), -11);
      }
      assertEquals(d1.getDayOfMonth(), d1.getMonthOfYear().lengthInDays(d1.toLocalDate().isLeapYear()));
    }
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, true, false), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, true, true), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, false, false), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate, false, true), forward);
    assertArrayEquals(CALCULATOR.getSchedule(startDate, endDate), forward);
  }
}
