/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.holiday.impl;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.time.Instant;
import javax.time.calendar.LocalDate;

import com.opengamma.DataNotFoundException;
import com.opengamma.core.holiday.Holiday;
import com.opengamma.core.holiday.HolidayType;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.id.VersionCorrection;
import com.opengamma.master.holiday.HolidayDocument;
import com.opengamma.master.holiday.HolidayMaster;
import com.opengamma.master.holiday.HolidaySearchRequest;
import com.opengamma.master.holiday.HolidaySearchResult;
import com.opengamma.master.holiday.ManageableHoliday;
import com.opengamma.util.money.Currency;

/**
 * Test MasterHolidaySource.
 */
@Test
public class MasterHolidaySourceTest {

  private static final LocalDate DATE_MONDAY = LocalDate.of(2010, 10, 25);
  private static final LocalDate DATE_SUNDAY = LocalDate.of(2010, 10, 24);
  private static final Currency GBP = Currency.GBP;
  private static final UniqueIdentifier UID = UniqueIdentifier.of("A", "B");
  private static final Identifier ID = Identifier.of("C", "D");
  private static final IdentifierBundle BUNDLE = IdentifierBundle.of(ID);
  private static final Instant NOW = Instant.now();
  private static final VersionCorrection VC = VersionCorrection.of(NOW.minusSeconds(2), NOW.minusSeconds(1));

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_1arg_nullMaster() throws Exception {
    new MasterHolidaySource(null);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void test_constructor_2arg_nullMaster() throws Exception {
    new MasterHolidaySource(null, null);
  }

  //-------------------------------------------------------------------------
  public void test_getHoliday_noOverride_found() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    
    HolidayDocument doc = new HolidayDocument(example());
    when(mock.get(UID)).thenReturn(doc);
    MasterHolidaySource test = new MasterHolidaySource(mock);
    Holiday testResult = test.getHoliday(UID);
    verify(mock, times(1)).get(UID);
    
    assertEquals(example(), testResult);
  }

  public void test_getHoliday_found() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    
    HolidayDocument doc = new HolidayDocument(example());
    when(mock.get(UID, VC)).thenReturn(doc);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    Holiday testResult = test.getHoliday(UID);
    verify(mock, times(1)).get(UID, VC);
    
    assertEquals(example(), testResult);
  }

  public void test_getHoliday_notFound() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    
    when(mock.get(UID, VC)).thenThrow(new DataNotFoundException(""));
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    Holiday testResult = test.getHoliday(UID);
    verify(mock, times(1)).get(UID, VC);
    
    assertEquals(null, testResult);
  }

  //-------------------------------------------------------------------------
  public void test_isHoliday_LocalDateCurrency_holiday() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionCorrection(VC);
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    boolean testResult = test.isHoliday(DATE_MONDAY, GBP);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

  public void test_isHoliday_LocalDateCurrency_workday() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionCorrection(VC);
    HolidaySearchResult result = new HolidaySearchResult();
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    boolean testResult = test.isHoliday(DATE_MONDAY, GBP);
    verify(mock, times(1)).search(request);
    
    assertEquals(false, testResult);
  }

  public void test_isHoliday_LocalDateCurrency_sunday() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(GBP);
    request.setDateToCheck(DATE_SUNDAY);
    request.setVersionCorrection(VC);
    HolidaySearchResult result = new HolidaySearchResult();
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    boolean testResult = test.isHoliday(DATE_SUNDAY, GBP);
    verify(mock, times(0)).search(request);
    
    assertEquals(true, testResult);
  }

  //-------------------------------------------------------------------------
  public void test_isHoliday_LocalDateTypeIdentifier_holiday() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(HolidayType.BANK, IdentifierBundle.of(ID));
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionCorrection(VC);
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    boolean testResult = test.isHoliday(DATE_MONDAY, HolidayType.BANK, ID);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

  //-------------------------------------------------------------------------
  public void test_isHoliday_LocalDateTypeIdentifierBundle_holiday() throws Exception {
    HolidayMaster mock = mock(HolidayMaster.class);
    HolidaySearchRequest request = new HolidaySearchRequest(HolidayType.BANK, BUNDLE);
    request.setDateToCheck(DATE_MONDAY);
    request.setVersionCorrection(VC);
    ManageableHoliday holiday = new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
    HolidaySearchResult result = new HolidaySearchResult();
    result.getDocuments().add(new HolidayDocument(holiday));
    
    when(mock.search(request)).thenReturn(result);
    MasterHolidaySource test = new MasterHolidaySource(mock, VC);
    boolean testResult = test.isHoliday(DATE_MONDAY, HolidayType.BANK, BUNDLE);
    verify(mock, times(1)).search(request);
    
    assertEquals(true, testResult);
  }

  //-------------------------------------------------------------------------
  protected Holiday example() {
    return new ManageableHoliday(GBP, Collections.singletonList(DATE_MONDAY));
  }

}
