/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.region.master.memory;

import static org.junit.Assert.assertEquals;

import javax.time.calendar.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.opengamma.DataNotFoundException;
import com.opengamma.financial.Currency;
import com.opengamma.financial.world.region.RegionClassification;
import com.opengamma.financial.world.region.RegionUtils;
import com.opengamma.financial.world.region.master.ManageableRegion;
import com.opengamma.financial.world.region.master.RegionDocument;
import com.opengamma.financial.world.region.master.RegionSearchRequest;
import com.opengamma.financial.world.region.master.RegionSearchResult;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;

/**
 * Test InMemoryRegionRepository.
 */
public class InMemoryRegionMasterTest {

  private static String NAME = "France";
  private static Identifier ID_COUNTRY = RegionUtils.countryRegionId("FR");
  private static Identifier ID_CURENCY = RegionUtils.currencyRegionId(Currency.getInstance("EUR"));
  private static Identifier ID_TIME_ZONE = RegionUtils.timeZoneRegionId(TimeZone.of("Europe/Paris"));
  private static Identifier ID_OTHER1 = Identifier.of("TEST_SCHEME", "The French");
  private static Identifier ID_OTHER2 = Identifier.of("TEST_SCHEME", "France");
  private static IdentifierBundle BUNDLE_FULL = IdentifierBundle.of(ID_COUNTRY, ID_TIME_ZONE, ID_CURENCY);
  private static IdentifierBundle BUNDLE_PART = IdentifierBundle.of(ID_COUNTRY, ID_CURENCY);
  private static IdentifierBundle BUNDLE_OTHER = IdentifierBundle.of(ID_COUNTRY, ID_TIME_ZONE, ID_OTHER1);

  private InMemoryRegionMaster master;
  private RegionDocument addedDoc;

  @Before
  public void setUp() {
    master = new InMemoryRegionMaster();
    ManageableRegion inputRegion = new ManageableRegion();
    inputRegion.setName(NAME);
    inputRegion.setFullName(NAME);
    inputRegion.setClassification(RegionClassification.INDEPENDENT_STATE);
    inputRegion.setCountryISO("FR");
    inputRegion.setCurrency(Currency.getInstance("EUR"));
    inputRegion.setTimeZone(TimeZone.of("Europe/Paris"));
    RegionDocument inputDoc = new RegionDocument(inputRegion);
    addedDoc = master.add(inputDoc);
  }

  //-------------------------------------------------------------------------
  @Test(expected = DataNotFoundException.class)
  public void test_get_noMatch() {
    master.get(UniqueIdentifier.of("A", "B"));
  }

  public void test_get_match() {
    RegionDocument result = master.get(addedDoc.getRegionId());
    assertEquals(Identifier.of("MemReg", "1"), result.getRegionId());
    assertEquals(addedDoc, result);
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_oneId_noMatch() {
    RegionSearchRequest request = new RegionSearchRequest(ID_OTHER1);
    RegionSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_oneId_mic() {
    RegionSearchRequest request = new RegionSearchRequest(ID_COUNTRY);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_oneId_ccid() {
    RegionSearchRequest request = new RegionSearchRequest(ID_COUNTRY);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_oneBundle_noMatch() {
    RegionSearchRequest request = new RegionSearchRequest(BUNDLE_OTHER);
    RegionSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_oneBundle_full() {
    RegionSearchRequest request = new RegionSearchRequest(BUNDLE_FULL);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_oneBundle_part() {
    RegionSearchRequest request = new RegionSearchRequest(BUNDLE_PART);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_twoBundles_noMatch() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.addIdentifierBundle(ID_OTHER1);
    request.addIdentifierBundle(ID_OTHER2);
    RegionSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_twoBundles_oneMatch() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.addIdentifierBundle(ID_COUNTRY);
    request.addIdentifierBundle(ID_OTHER1);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  @Test
  public void test_search_twoBundles_bothMatch() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.addIdentifierBundle(ID_COUNTRY);
    request.addIdentifierBundle(ID_CURENCY);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_name_noMatch() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.setName("No match");
    RegionSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_name_match() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.setName(NAME);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_search_classification_noMatch() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.setClassification(RegionClassification.DEPENDENCY);
    RegionSearchResult result = master.search(request);
    assertEquals(0, result.getDocuments().size());
  }

  @Test
  public void test_search_classification_match() {
    RegionSearchRequest request = new RegionSearchRequest();
    request.setClassification(RegionClassification.INDEPENDENT_STATE);
    RegionSearchResult result = master.search(request);
    assertEquals(1, result.getDocuments().size());
    assertEquals(addedDoc, result.getFirstDocument());
  }

}