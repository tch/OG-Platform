/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.timeseries.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.time.calendar.LocalDate;
import javax.time.calendar.format.CalendricalParseException;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.opengamma.DataNotFoundException;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.IdentifierBundleWithDates;
import com.opengamma.id.IdentifierWithDates;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.id.UniqueIdentifierSupplier;
import com.opengamma.master.timeseries.DataFieldBean;
import com.opengamma.master.timeseries.DataPointDocument;
import com.opengamma.master.timeseries.DataProviderBean;
import com.opengamma.master.timeseries.DataSourceBean;
import com.opengamma.master.timeseries.ObservationTimeBean;
import com.opengamma.master.timeseries.SchemeBean;
import com.opengamma.master.timeseries.TimeSeriesDocument;
import com.opengamma.master.timeseries.TimeSeriesMaster;
import com.opengamma.master.timeseries.TimeSeriesSearchHistoricRequest;
import com.opengamma.master.timeseries.TimeSeriesSearchHistoricResult;
import com.opengamma.master.timeseries.TimeSeriesSearchRequest;
import com.opengamma.master.timeseries.TimeSeriesSearchResult;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.db.Paging;
import com.opengamma.util.time.DateUtil;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.MapLocalDateDoubleTimeSeries;
import com.opengamma.util.tuple.Pair;

/**
 * An in-memory implementation of a time-series master.
 */
public class InMemoryLocalDateTimeSeriesMaster implements TimeSeriesMaster<LocalDate> {

  /**
   * A cache of LocalDate time-series by identifier.
   */
  private final ConcurrentHashMap<UniqueIdentifier, TimeSeriesDocument<LocalDate>> _timeseriesDb = new ConcurrentHashMap<UniqueIdentifier, TimeSeriesDocument<LocalDate>>();
  /**
   * The default scheme used for each {@link UniqueIdentifier}.
   */
  public static final String DEFAULT_UID_SCHEME = "TssMemory";
  /**
   * The supplied of identifiers.
   */
  private final Supplier<UniqueIdentifier> _uniqueIdSupplier;

  /**
   * Creates an empty time-series master using the default scheme for any {@link UniqueIdentifier}s created.
   */
  public InMemoryLocalDateTimeSeriesMaster() {
    this(new UniqueIdentifierSupplier(DEFAULT_UID_SCHEME));
  }

  /**
   * Creates an instance specifying the supplier of unique identifiers.
   * 
   * @param uniqueIdSupplier  the supplier of unique identifiers, not null
   */
  private InMemoryLocalDateTimeSeriesMaster(final Supplier<UniqueIdentifier> uniqueIdSupplier) {
    ArgumentChecker.notNull(uniqueIdSupplier, "uniqueIdSupplier");
    _uniqueIdSupplier = uniqueIdSupplier;
  }

  //-------------------------------------------------------------------------
  @Override
  public DataSourceBean getOrCreateDataSource(String dataSource, String description) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<DataSourceBean> getDataSources() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataProviderBean getOrCreateDataProvider(String dataProvider, String description) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<DataProviderBean> getDataProviders() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataFieldBean getOrCreateDataField(String field, String description) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<DataFieldBean> getDataFields() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ObservationTimeBean getOrCreateObservationTime(String observationTime, String description) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<ObservationTimeBean> getObservationTimes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemeBean getOrCreateScheme(String scheme, String descrption) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<SchemeBean> getSchemes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IdentifierBundleWithDates> getAllIdentifiers() {
    List<IdentifierBundleWithDates> result = new ArrayList<IdentifierBundleWithDates>();
    Collection<TimeSeriesDocument<LocalDate>> docs = _timeseriesDb.values();
    for (TimeSeriesDocument<LocalDate> tsDoc : docs) {
      result.add(tsDoc.getIdentifiers());
    }
    return result;
  }

  @Override
  public TimeSeriesSearchResult<LocalDate> searchTimeSeries(final TimeSeriesSearchRequest<LocalDate> request) {
    ArgumentChecker.notNull(request, "request");
    final TimeSeriesSearchResult<LocalDate> result = new TimeSeriesSearchResult<LocalDate>();
    Collection<TimeSeriesDocument<LocalDate>> docs = _timeseriesDb.values();
    
    if (request.getTimeSeriesId() != null) {
      docs = Collections.singleton(getTimeSeries(request.getTimeSeriesId()));
    }
    
    if (request.getDataField() != null) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          return request.getDataField().equals(doc.getDataField());
        }
      });
    }
    
    if (request.getDataProvider() != null) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          return request.getDataProvider().equals(doc.getDataProvider());
        }
      });
    }
    
    if (request.getDataSource() != null) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          return request.getDataSource().equals(doc.getDataSource());
        }
      });
    }
    
    if (request.getObservationTime() != null) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          return request.getObservationTime().equals(doc.getObservationTime());
        }
      });
    }
    
    if (request.getIdentifiers() != null && !request.getIdentifiers().isEmpty()) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          IdentifierBundleWithDates bundleWithDates = doc.getIdentifiers();
          List<Identifier> requestIdentifiers = request.getIdentifiers();
          LocalDate currentDate = request.getCurrentDate();
          for (IdentifierWithDates idWithDates : bundleWithDates) {
            LocalDate validFrom = idWithDates.getValidFrom();
            LocalDate validTo = idWithDates.getValidTo();
            if (requestIdentifiers.contains(idWithDates.asIdentifier())) {
              if (currentDate != null) {
                if (validFrom == null && validTo != null) {
                  return currentDate.isBefore(validTo) || currentDate.equals(validTo);
                }
                if (validFrom != null && validTo == null) {
                  return currentDate.isAfter(validFrom) || currentDate.equals(validFrom);
                }
                if (validFrom != null && validTo != null) {
                  return currentDate.equals(validFrom) || currentDate.equals(validTo) || (currentDate.isAfter(validFrom) && currentDate.isBefore(validTo));
                }
              } else {
                return true;
              }
            }
          }
          return false;
        }
      });
    }
        
    if (request.getIdentifierValue() != null) {
      docs = Collections2.filter(docs, new Predicate<TimeSeriesDocument<LocalDate>>() {
        @Override
        public boolean apply(final TimeSeriesDocument<LocalDate> doc) {
          IdentifierBundle bundle = doc.getIdentifiers().asIdentifierBundle();
          Set<String> identifierValues = new HashSet<String>();
          for (Identifier identifier : bundle.getIdentifiers()) {
            identifierValues.add(identifier.getValue());
          }
          return identifierValues.contains(request.getIdentifierValue());
        }
      });
    }
    
    if (request.isLoadDates()) {
      for (TimeSeriesDocument<LocalDate> tsDocument : docs) {
        assert tsDocument.getTimeSeries() != null;
        tsDocument.setLatest(tsDocument.getTimeSeries().getLatestTime());
        tsDocument.setEarliest(tsDocument.getTimeSeries().getEarliestTime());
      }
    }
    
    if (!request.isLoadTimeSeries()) {
      List<TimeSeriesDocument<LocalDate>> noSeries = new ArrayList<TimeSeriesDocument<LocalDate>>();
      for (TimeSeriesDocument<LocalDate> tsDocument : docs) {
        TimeSeriesDocument<LocalDate> doc = new TimeSeriesDocument<LocalDate>();
        doc.setDataField(tsDocument.getDataField());
        doc.setDataProvider(tsDocument.getDataProvider());
        doc.setDataSource(tsDocument.getDataSource());
        doc.setEarliest(tsDocument.getEarliest());
        doc.setIdentifiers(tsDocument.getIdentifiers());
        doc.setLatest(tsDocument.getLatest());
        doc.setObservationTime(tsDocument.getObservationTime());
        doc.setUniqueId(tsDocument.getUniqueId());
        noSeries.add(doc);
      }
      docs = noSeries;
    }
    
    if (request.getStart() != null && request.getEnd() != null) {
      List<TimeSeriesDocument<LocalDate>> subseriesList = new ArrayList<TimeSeriesDocument<LocalDate>>();
      for (TimeSeriesDocument<LocalDate> tsDocument : docs) {
        TimeSeriesDocument<LocalDate> doc = new TimeSeriesDocument<LocalDate>();
        doc.setDataField(tsDocument.getDataField());
        doc.setDataProvider(tsDocument.getDataProvider());
        doc.setDataSource(tsDocument.getDataSource());
        doc.setEarliest(tsDocument.getEarliest());
        doc.setIdentifiers(tsDocument.getIdentifiers());
        doc.setLatest(tsDocument.getLatest());
        doc.setObservationTime(tsDocument.getObservationTime());
        doc.setUniqueId(tsDocument.getUniqueId());
        DoubleTimeSeries<LocalDate> subseries = tsDocument.getTimeSeries().subSeries(request.getStart(), true, request.getEnd(), false);
        doc.setTimeSeries(subseries);
        subseriesList.add(doc);
      }
      docs = subseriesList;
    }
    
    result.getDocuments().addAll(docs);
    result.setPaging(Paging.of(docs));
    return result;
    
  }
  
  @Override
  public TimeSeriesDocument<LocalDate> getTimeSeries(UniqueIdentifier uniqueId) {
    validateUId(uniqueId);
    final TimeSeriesDocument<LocalDate> document = _timeseriesDb.get(uniqueId);
    if (document == null) {
      throw new DataNotFoundException("Timeseries not found: " + uniqueId);
    }
    return document;
  }
  
  private void validateUId(UniqueIdentifier uniqueId) {
    ArgumentChecker.notNull(uniqueId, "TimeSeries UID");
    ArgumentChecker.isTrue(uniqueId.getScheme().equals(DEFAULT_UID_SCHEME), "UID not " + DEFAULT_UID_SCHEME);
    ArgumentChecker.isTrue(uniqueId.getValue() != null, "Uid value cannot be null");
    
    try {
      Long.parseLong(uniqueId.getValue());
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Invalid UID " + uniqueId);
    }
  }

  @Override
  public TimeSeriesDocument<LocalDate> addTimeSeries(TimeSeriesDocument<LocalDate> document) {
    validateTimeSeriesDocument(document);
    if (!contains(document)) {
      final UniqueIdentifier uniqueId = _uniqueIdSupplier.get();
      final TimeSeriesDocument<LocalDate> doc = new TimeSeriesDocument<LocalDate>();
      doc.setUniqueId(uniqueId);
      doc.setDataField(document.getDataField());
      doc.setDataProvider(document.getDataProvider());
      doc.setDataSource(document.getDataSource());
      doc.setIdentifiers(document.getIdentifiers());
      doc.setObservationTime(document.getObservationTime());
      doc.setTimeSeries(document.getTimeSeries());
      _timeseriesDb.put(uniqueId, doc);  // unique identifier should be unique
      document.setUniqueId(uniqueId);
      return document;
    } else {
      throw new IllegalArgumentException("cannot add duplicate TimeSeries for identifiers " + document.getIdentifiers());
    }
  }
  
  private void validateTimeSeriesDocument(TimeSeriesDocument<LocalDate> document) {
    ArgumentChecker.notNull(document, "timeseries document");
    ArgumentChecker.notNull(document.getTimeSeries(), "Timeseries");
    ArgumentChecker.notNull(document.getIdentifiers(), "identifiers");
    ArgumentChecker.isTrue(!document.getIdentifiers().asIdentifierBundle().getIdentifiers().isEmpty(), "cannot add timeseries with empty identifiers");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getDataSource()), "cannot add timeseries with blank dataSource");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getDataProvider()), "cannot add timeseries with blank dataProvider");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getDataProvider()), "cannot add timeseries with blank dataProvider");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getDataField()), "cannot add timeseries with blank field");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getObservationTime()), "cannot add timeseries with blank observationTime");
    ArgumentChecker.isTrue(!StringUtils.isBlank(document.getDataProvider()), "cannot add timeseries with blank dataProvider");
  }
  
  private boolean contains(TimeSeriesDocument<LocalDate> document) {
    for (IdentifierWithDates identifierWithDates : document.getIdentifiers()) {
      Identifier identifier = identifierWithDates.asIdentifier();
      UniqueIdentifier uniqueId = resolveIdentifier(
          IdentifierBundle.of(identifier), 
          identifierWithDates.getValidFrom(), 
          document.getDataSource(), 
          document.getDataProvider(), 
          document.getDataField());
      if (uniqueId != null) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public TimeSeriesDocument<LocalDate> updateTimeSeries(TimeSeriesDocument<LocalDate> document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getTimeSeries(), "document.timeseries");
    ArgumentChecker.notNull(document.getDataField(), "document.dataField");
    ArgumentChecker.notNull(document.getDataProvider(), "document.dataProvider");
    ArgumentChecker.notNull(document.getDataSource(), "document.dataSource");
    ArgumentChecker.notNull(document.getObservationTime(), "document.observationTime");
    ArgumentChecker.notNull(document.getUniqueId(), "document.uniqueId");
    
    final UniqueIdentifier uniqueId = document.getUniqueId();
    final TimeSeriesDocument<LocalDate> storedDocument = _timeseriesDb.get(uniqueId);
    if (storedDocument == null) {
      throw new DataNotFoundException("Timeseries not found: " + uniqueId);
    }
    if (_timeseriesDb.replace(uniqueId, storedDocument, document) == false) {
      throw new IllegalArgumentException("Concurrent modification");
    }
    return document;
  }
  
  @Override
  public void removeTimeSeries(UniqueIdentifier uniqueId) {
    ArgumentChecker.notNull(uniqueId, "uniqueId");
    if (_timeseriesDb.remove(uniqueId) == null) {
      throw new DataNotFoundException("Timeseries not found: " + uniqueId);
    }
  }

  @Override
  public TimeSeriesSearchHistoricResult<LocalDate> searchHistoric(final TimeSeriesSearchHistoricRequest request) {
    ArgumentChecker.notNull(request, "request");
    ArgumentChecker.notNull(request.getTimeSeriesId(), "request.timeseriesId");
    
    final TimeSeriesSearchHistoricResult<LocalDate> result = new TimeSeriesSearchHistoricResult<LocalDate>();
    TimeSeriesDocument<LocalDate> doc = getTimeSeries(request.getTimeSeriesId());
    if (doc != null) {
      result.getDocuments().add(doc);
    }
    result.setPaging(Paging.of(result.getDocuments()));
    return result;
  }
  
  @Override
  public DataPointDocument<LocalDate> updateDataPoint(DataPointDocument<LocalDate> document) {
    ArgumentChecker.notNull(document, "dataPoint document");
    ArgumentChecker.notNull(document.getDate(), "data point date");
    ArgumentChecker.notNull(document.getValue(), "data point value");
    
    UniqueIdentifier timeSeriesId = document.getTimeSeriesId();
    validateUId(timeSeriesId);
    
    TimeSeriesDocument<LocalDate> storeDoc = _timeseriesDb.get(timeSeriesId);
    DoubleTimeSeries<LocalDate> timeSeries = storeDoc.getTimeSeries();
    MapLocalDateDoubleTimeSeries mutableTS = new MapLocalDateDoubleTimeSeries(timeSeries);
    mutableTS.putDataPoint(document.getDate(), document.getValue());
    storeDoc.setTimeSeries(mutableTS);
    return document;
  }

  @Override
  public DataPointDocument<LocalDate> addDataPoint(DataPointDocument<LocalDate> document) {
    ArgumentChecker.notNull(document, "dataPoint document");
    ArgumentChecker.notNull(document.getDate(), "data point date");
    ArgumentChecker.notNull(document.getValue(), "data point value");
    UniqueIdentifier timeSeriesId = document.getTimeSeriesId();
    validateUId(timeSeriesId);
    
    TimeSeriesDocument<LocalDate> storedDoc = _timeseriesDb.get(timeSeriesId);
    MapLocalDateDoubleTimeSeries mutableTS = new MapLocalDateDoubleTimeSeries();
    mutableTS.putDataPoint(document.getDate(), document.getValue());
    DoubleTimeSeries<LocalDate> mergedTS = storedDoc.getTimeSeries().noIntersectionOperation(mutableTS);
    storedDoc.setTimeSeries(mergedTS);
    
    String uniqueId = new StringBuilder(timeSeriesId.getValue()).append("/").append(DateUtil.printYYYYMMDD(document.getDate())).toString();
    document.setDataPointId(UniqueIdentifier.of(DEFAULT_UID_SCHEME, uniqueId));
    return document;
    
  }
  
  @Override
  public DataPointDocument<LocalDate> getDataPoint(UniqueIdentifier uniqueId) {
    Pair<Long, LocalDate> uniqueIdPair = validateAndGetDataPointId(uniqueId);
    
    Long tsId = uniqueIdPair.getFirst();
    LocalDate date = uniqueIdPair.getSecond();
    
    final DataPointDocument<LocalDate> result = new DataPointDocument<LocalDate>();
    result.setDate(uniqueIdPair.getSecond());
    result.setTimeSeriesId(UniqueIdentifier.of(DEFAULT_UID_SCHEME, String.valueOf(tsId)));
    result.setDataPointId(uniqueId);
    
    TimeSeriesDocument<LocalDate> storedDoc = _timeseriesDb.get(UniqueIdentifier.of(DEFAULT_UID_SCHEME, String.valueOf(tsId)));
    Double value = storedDoc.getTimeSeries().getValue(date);
    result.setValue(value);
       
    return result;
  }
  
  private Pair<Long, LocalDate> validateAndGetDataPointId(UniqueIdentifier uniqueId) {
    ArgumentChecker.notNull(uniqueId, "DataPoint UID");
    ArgumentChecker.isTrue(uniqueId.getScheme().equals(DEFAULT_UID_SCHEME), "UID not TssMemory");
    ArgumentChecker.isTrue(uniqueId.getValue() != null, "Uid value cannot be null");
    String[] tokens = StringUtils.split(uniqueId.getValue(), '/');
    if (tokens.length != 2) {
      throw new IllegalArgumentException("UID not expected format<12345/date> " + uniqueId);
    }
    String id = tokens[0];
    String dateStr = tokens[1];
    LocalDate date = null;
    Long tsId = Long.MIN_VALUE;
    if (id != null && dateStr != null) {
      try {
        date = DateUtil.toLocalDate(dateStr);
      } catch (CalendricalParseException ex) {
        throw new IllegalArgumentException("UID not expected format<12345/date> " + uniqueId, ex);
      }
      try {
        tsId = Long.parseLong(id);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("UID not expected format<12345/date> " + uniqueId, ex);
      }
    } else {
      throw new IllegalArgumentException("UID not expected format<12345/date> " + uniqueId);
    }
    return Pair.of(tsId, date);
  }

  @Override
  public void removeDataPoint(UniqueIdentifier uniqueId) {
    Pair<Long, LocalDate> uniqueIdPair = validateAndGetDataPointId(uniqueId);
    
    Long tsId = uniqueIdPair.getFirst();
    TimeSeriesDocument<LocalDate> storedDoc = _timeseriesDb.get(UniqueIdentifier.of(DEFAULT_UID_SCHEME, String.valueOf(tsId)));
    
    MapLocalDateDoubleTimeSeries mutableTS = new MapLocalDateDoubleTimeSeries(storedDoc.getTimeSeries());
    mutableTS.removeDataPoint(uniqueIdPair.getSecond());
    storedDoc.setTimeSeries(mutableTS);
  }

  

  @Override
  public void appendTimeSeries(TimeSeriesDocument<LocalDate> document) {
    ArgumentChecker.notNull(document, "document");
    ArgumentChecker.notNull(document.getIdentifiers(), "identifiers");
    ArgumentChecker.notNull(document.getDataSource(), "dataSource");
    ArgumentChecker.notNull(document.getDataProvider(), "dataProvider");
    ArgumentChecker.notNull(document.getDataField(), "dataField");
    
    validateUId(document.getUniqueId());
    UniqueIdentifier uniqueId = document.getUniqueId();
    TimeSeriesDocument<LocalDate> storedDoc = _timeseriesDb.get(uniqueId);
    DoubleTimeSeries<LocalDate> mergedTS = storedDoc.getTimeSeries().noIntersectionOperation(document.getTimeSeries());
    storedDoc.setTimeSeries(mergedTS);
  }
  
  @Override
  public UniqueIdentifier resolveIdentifier(IdentifierBundle securityBundle, String dataSource, String dataProvider, String dataField) {
    return resolveIdentifier(securityBundle, (LocalDate) null, dataSource, dataProvider, dataField);
  }

  @Override
  public UniqueIdentifier resolveIdentifier(IdentifierBundle securityBundle, LocalDate currentDate, String dataSource, String dataProvider, String dataField) {
    ArgumentChecker.notNull(securityBundle, "securityBundle");
    ArgumentChecker.notNull(dataSource, "dataSource");
    ArgumentChecker.notNull(dataProvider, "dataProvider");
    ArgumentChecker.notNull(dataField, "dataField");
    
    for (Entry<UniqueIdentifier, TimeSeriesDocument<LocalDate>> entry : _timeseriesDb.entrySet()) {
      UniqueIdentifier uniqueId = entry.getKey();
      TimeSeriesDocument<LocalDate> tsDoc = entry.getValue();
      if (tsDoc.getDataSource().equals(dataSource) && tsDoc.getDataProvider().equals(dataProvider) && tsDoc.getDataField().equals(dataField)) {
        for (IdentifierWithDates idWithDates : tsDoc.getIdentifiers()) {
          if (securityBundle.contains(idWithDates.asIdentifier())) {
            LocalDate validFrom = idWithDates.getValidFrom();
            LocalDate validTo = idWithDates.getValidTo();
            if (currentDate != null) {
              if (currentDate.equals(validFrom) || (currentDate.isAfter(validFrom) && currentDate.isBefore(validTo)) || currentDate.equals(validTo)) {
                return uniqueId;
              }
            } else {
              return uniqueId;
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public void removeDataPoints(UniqueIdentifier timeSeriesUid, LocalDate firstDateToRetain) {
    validateUId(timeSeriesUid);
    TimeSeriesDocument<LocalDate> storedDoc = _timeseriesDb.get(timeSeriesUid);
    DoubleTimeSeries<LocalDate> timeSeries = storedDoc.getTimeSeries();
    DoubleTimeSeries<LocalDate> subSeries = timeSeries.subSeries(firstDateToRetain, true, timeSeries.getLatestTime(), false);
    storedDoc.setTimeSeries(subSeries);
  }

}
