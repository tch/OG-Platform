/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.timeseries.db;

import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.DEACTIVATE_META_DATA;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.DELETE_DATA_POINT;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.DELETE_DATA_POINTS_BY_DATE;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.DELETE_TIME_SERIES_BY_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.FIND_DATA_POINT_BY_DATE_AND_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_META_DATA;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_META_DATA_BY_IDENTIFIERS;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_META_DATA_BY_OID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_META_DATA_WITH_DATES;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_META_DATA_WITH_DATES_BY_IDENTIFIERS;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_ACTIVE_TIME_SERIES_KEY_BY_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_TIME_SERIES_BY_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.GET_TS_DATE_RANGE_BY_OID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_DATA_FIELD;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_DATA_PROVIDER;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_DATA_SOURCE;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_IDENTIFIER;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_OBSERVATION_TIME;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_QUOTED_OBJECT;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_SCHEME;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_TIME_SERIES;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_TIME_SERIES_DELTA_D;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_TIME_SERIES_DELTA_I;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_TIME_SERIES_DELTA_U;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INSERT_TIME_SERIES_KEY;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.INVALID_KEY;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_DATA_FIELDS;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_DATA_PROVIDER;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_DATA_SOURCES;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_IDENTIFIERS;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_OBSERVATION_TIMES;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_ALL_SCHEME;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_TIME_SERIES_DELTA;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.LOAD_TIME_SERIES_WITH_DATES;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_BUNDLE_FROM_IDENTIFIERS;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_DATA_FIELD_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_DATA_PROVIDER_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_DATA_SOURCE_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_OBSERVATION_TIME_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_QUOTED_OBJECT_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.SELECT_SCHEME_ID;
import static com.opengamma.financial.timeseries.db.DbTimeSeriesMasterConstants.UPDATE_TIME_SERIES;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.time.Instant;
import javax.time.calendar.LocalDate;
import javax.time.calendar.format.CalendricalParseException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.opengamma.DataNotFoundException;
import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.IdentifierBundleWithDates;
import com.opengamma.id.IdentifierWithDates;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.timeseries.DataFieldBean;
import com.opengamma.master.timeseries.DataPointDocument;
import com.opengamma.master.timeseries.DataProviderBean;
import com.opengamma.master.timeseries.DataSourceBean;
import com.opengamma.master.timeseries.NamedDescriptionBean;
import com.opengamma.master.timeseries.ObservationTimeBean;
import com.opengamma.master.timeseries.SchemeBean;
import com.opengamma.master.timeseries.TimeSeriesDocument;
import com.opengamma.master.timeseries.TimeSeriesMaster;
import com.opengamma.master.timeseries.TimeSeriesSearchHistoricRequest;
import com.opengamma.master.timeseries.TimeSeriesSearchHistoricResult;
import com.opengamma.master.timeseries.TimeSeriesSearchRequest;
import com.opengamma.master.timeseries.TimeSeriesSearchResult;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.db.DbDateUtils;
import com.opengamma.util.db.DbSource;
import com.opengamma.util.db.Paging;
import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.MutableDoubleTimeSeries;
import com.opengamma.util.tuple.ObjectsPair;
import com.opengamma.util.tuple.Pair;

/**
 * Abstract class that does all the JDBC template work and provides TimeSeriesMaster
 * implementations for a typical RDMS database
 * <p>
 * Expects the subclass to provide a map for specific database SQL queries
 * 
 * @param <T> LocalDate/java.util.Date
 */
@Transactional(readOnly = true)
public abstract class DbTimeSeriesMaster<T> implements TimeSeriesMaster<T> {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(DbTimeSeriesMaster.class);
  /**
   * List of keys expected in the Map containing SQL queries injected to RowStoreTimeSeriesMaster
   */
  private static final Set<String> SQL_MAP_KEYS = Collections.unmodifiableSet(Sets.newHashSet(
      DEACTIVATE_META_DATA,
      DELETE_DATA_POINT,
      DELETE_TIME_SERIES_BY_ID,
      FIND_DATA_POINT_BY_DATE_AND_ID,
      GET_ACTIVE_META_DATA_BY_OID,
      GET_TIME_SERIES_BY_ID,
      GET_ACTIVE_TIME_SERIES_KEY_BY_ID,
      GET_TS_DATE_RANGE_BY_OID,
      INSERT_DATA_FIELD,
      INSERT_DATA_PROVIDER,
      INSERT_DATA_SOURCE,
      INSERT_SCHEME,
      INSERT_IDENTIFIER,
      INSERT_OBSERVATION_TIME,
      INSERT_QUOTED_OBJECT,
      INSERT_TIME_SERIES,
      INSERT_TIME_SERIES_DELTA_D,
      INSERT_TIME_SERIES_DELTA_I,
      INSERT_TIME_SERIES_DELTA_U,
      INSERT_TIME_SERIES_KEY,
      LOAD_ALL_DATA_FIELDS,
      LOAD_ALL_DATA_PROVIDER,
      LOAD_ALL_DATA_SOURCES,
      LOAD_ALL_IDENTIFIERS,
      LOAD_ALL_OBSERVATION_TIMES,
      LOAD_ALL_SCHEME,
      LOAD_TIME_SERIES_DELTA,
      LOAD_TIME_SERIES_WITH_DATES,
      SELECT_DATA_FIELD_ID,
      SELECT_DATA_PROVIDER_ID,
      SELECT_DATA_SOURCE_ID,
      SELECT_SCHEME_ID,
      SELECT_OBSERVATION_TIME_ID,
      SELECT_BUNDLE_FROM_IDENTIFIERS,
      SELECT_QUOTED_OBJECT_ID,
      GET_ACTIVE_META_DATA_WITH_DATES,
      GET_ACTIVE_META_DATA_WITH_DATES_BY_IDENTIFIERS,
      GET_ACTIVE_META_DATA,
      GET_ACTIVE_META_DATA_BY_IDENTIFIERS,
      UPDATE_TIME_SERIES));
  /**
   * The scheme used for UniqueIdentifier objects.
   */
  public static final String IDENTIFIER_SCHEME_DEFAULT = "Tss";

  /**
   * The identifier scheme to use.
   */
  private String _identifierScheme = IDENTIFIER_SCHEME_DEFAULT;
  /**
   * The database source.
   */
  private final DbSource _dbSource;
  /**
   * The map of SQL
   */
  private Map<String, String> _namedSQLMap;
  /**
   * Whether trigger is supported.
   */
  private final boolean _isTriggerSupported;

  /**
   * Creates an instance.
   * 
   * @param dbSource  the database information, not null
   * @param namedSQLMap  the named SQL map, not null
   * @param isTriggerSupported  whether trigger is supported
   */
  public DbTimeSeriesMaster(
      DbSource dbSource, Map<String, String> namedSQLMap, boolean isTriggerSupported) {
    ArgumentChecker.notNull(dbSource, "dbSource");
    
    _dbSource = dbSource;
    checkNamedSQLMap(namedSQLMap);
    
    // see tssQueries.xml
    Map<String, String> namedSQLMapCorrected = new HashMap<String, String>();
    for (String key : namedSQLMap.keySet()) {
      String sql = namedSQLMap.get(key);
      sql = sql.replaceAll("\\{tss_data_point\\}", getDataPointTableName());
      sql = sql.replaceAll("\\{tss_data_point_delta\\}", getDataPointDeltaTableName());
      namedSQLMapCorrected.put(key, sql);      
    }
    _namedSQLMap = Collections.unmodifiableMap(namedSQLMapCorrected);
    _isTriggerSupported = isTriggerSupported;
  }

  //-------------------------------------------------------------------------
  protected abstract String getDataPointTableName();

  protected abstract String getDataPointDeltaTableName();

  /**
   * @return See {@link java.sql.Types}.
   */
  protected abstract int getSqlDateType();

  protected abstract Object getSqlDate(T date);

  protected abstract T getDate(ResultSet rs, String column) throws SQLException;

  protected abstract T getDate(String date);

  protected abstract String printDate(T date);

  protected abstract DoubleTimeSeries<T> getTimeSeries(List<T> dates, List<Double> values);

  protected abstract MutableDoubleTimeSeries<T> getMutableTimeSeries(DoubleTimeSeries<T> timeSeries);

  //-------------------------------------------------------------------------
  /**
   * Gets the database source.
   * 
   * @return the database source, non null
   */
  public DbSource getDbSource() {
    return _dbSource;
  }

  /**
   * Gets the simple JDBC template.
   * 
   * @return the JDBC template, not null
   */
  public SimpleJdbcTemplate getJdbcTemplate() {
    return _dbSource.getJdbcTemplate();
  }

  /**
   * Gets whether trigger is supported.
   * 
   * @return whether trigger is supported
   */
  public boolean isTriggerSupported() {
    return _isTriggerSupported;
  }

  //-------------------------------------------------------------------------
  @Override
  public List<IdentifierBundleWithDates> getAllIdentifiers() {
    IdentifierBundleHandler identifierBundleHandler = new IdentifierBundleHandler();
    JdbcOperations jdbcOperations = getJdbcTemplate().getJdbcOperations();
    jdbcOperations.query(_namedSQLMap.get(LOAD_ALL_IDENTIFIERS), identifierBundleHandler);
    List<IdentifierBundleWithDates> result = new ArrayList<IdentifierBundleWithDates>();
    Map<Long, List<IdentifierWithDates>> identifierBundles = identifierBundleHandler.getResult();
    for (List<IdentifierWithDates> identifiers : identifierBundles.values()) {
      result.add(new IdentifierBundleWithDates(identifiers));
    }
    return result;
  }

  private UniqueIdentifier addTimeSeries(IdentifierBundleWithDates identifierBundleWithDates,
      String dataSource, String dataProvider, String field,
      String observationTime, final DoubleTimeSeries<T> timeSeries) {

    s_logger.debug("adding timeseries for {} with dataSource={}, dataProvider={}, dataField={}, observationTime={} startdate={} endate={}", 
        new Object[]{identifierBundleWithDates, dataSource, dataProvider, field, observationTime, timeSeries.getEarliestTime(), timeSeries.getLatestTime()});
    
    String bundleName = buildBundleName(identifierBundleWithDates);
    long bundleId = getOrCreateIdentifierBundle(bundleName, bundleName, identifierBundleWithDates);
    long tsKey = getOrCreateTimeSeriesKey(bundleId, dataSource, dataProvider, field, observationTime);
    insertDataPoints(timeSeries, tsKey);
    return UniqueIdentifier.of(_identifierScheme, String.valueOf(tsKey));
  }

  private String buildBundleName(IdentifierBundleWithDates identifierBundleWithDates) {
    IdentifierBundle identifierBundle = identifierBundleWithDates.asIdentifierBundle();
    Identifier identifier = identifierBundle.iterator().next();
    String bundleName = identifier.getScheme().getName() + "_" + identifier.getValue();
    return bundleName;
  }

  private void insertDataPoints(DoubleTimeSeries<T> sqlDateDoubleTimeSeries, long tsKey) {
    String insertSQL = _namedSQLMap.get(INSERT_TIME_SERIES);
    String insertDelta = _namedSQLMap.get(INSERT_TIME_SERIES_DELTA_I);
    
    Date now = new Date(System.currentTimeMillis());
    
    SqlParameterSource[] batchArgs = new MapSqlParameterSource[sqlDateDoubleTimeSeries.size()];
    int index = 0;
    
    for (Entry<T, Double> dataPoint : sqlDateDoubleTimeSeries) {
      T date = dataPoint.getKey();
      Double value = dataPoint.getValue();
      MapSqlParameterSource parameterSource = new MapSqlParameterSource();
      parameterSource.addValue("timeSeriesID", tsKey, Types.BIGINT);
      parameterSource.addValue("date", getSqlDate(date), getSqlDateType());
      parameterSource.addValue("value", value, Types.DOUBLE);
      parameterSource.addValue("timeStamp", now, Types.TIMESTAMP);
      batchArgs[index++] = parameterSource;
    }
    if (!isTriggerSupported()) {
      getJdbcTemplate().batchUpdate(insertDelta, batchArgs);
    }
    getJdbcTemplate().batchUpdate(insertSQL, batchArgs);
  }

  private long getOrCreateTimeSeriesKey(long bundleId, String dataSource, String dataProvider, String field, String observationTime) {
//    long timeSeriesKeyID = getTimeSeriesKey(bundleId, dataSource, dataProvider, field, observationTime);
//    if (timeSeriesKeyID == INVALID_KEY) {
//      timeSeriesKeyID = createTimeSeriesKey(bundleId, dataSource, dataProvider, field, observationTime);
//    }
//    return timeSeriesKeyID;
    return createTimeSeriesKey(bundleId, dataSource, dataProvider, field, observationTime);
  }

  private long createDataProvider(String dataProvider, String description) {
    String sql = _namedSQLMap.get(INSERT_DATA_PROVIDER);
    insertNamedDimension(sql, dataProvider, description);
    return getDataProviderId(dataProvider);
  }

  private void insertNamedDimension(String sql, String name, String description) {
    s_logger.debug("running sql={} with values({}, {})", new Object[]{sql, name, description});
    SqlParameterSource parameters = new MapSqlParameterSource()
      .addValue("name", name, Types.VARCHAR)
      .addValue("description", description, Types.VARCHAR);
    getJdbcTemplate().update(sql, parameters);
  }

  private long createDataSource(String dataSource, String description) {
    String sql = _namedSQLMap.get(INSERT_DATA_SOURCE);
    insertNamedDimension(sql, dataSource, description);
    return getDataSourceId(dataSource);
  }

  private Map<Long, List<IdentifierWithDates>> searchIdentifierBundles(final Collection<Identifier> identifiers, String identifierValue, final Date currentDate) {
    IdentifierBundleHandler rowHandler = new IdentifierBundleHandler();
    String namedSql = _namedSQLMap.get(SELECT_BUNDLE_FROM_IDENTIFIERS);
    StringBuilder bundleWhereCondition = new StringBuilder(" ");
    Object[] parameters = null;
    String findIdentifiersSql = null;
    
    if ((identifiers == null || identifiers.isEmpty()) && identifierValue == null) {
      findIdentifiersSql = _namedSQLMap.get(LOAD_ALL_IDENTIFIERS);
    } else {
      int orCounter = 1;
      ArrayList<Object> parametersList = new ArrayList<Object>();
      if (identifiers != null) {
        for (Identifier identifier : identifiers) {
          bundleWhereCondition.append("( ");
          bundleWhereCondition.append("d.name = ? AND dsi.identifier_value = ? ");
          parametersList.add(identifier.getScheme().getName());
          parametersList.add(identifier.getValue());
          
          if (currentDate != null) {
            bundleWhereCondition.append("AND (dsi.valid_from <= ?  OR dsi.valid_from IS NULL) " +
                "AND (dsi.valid_to >= ? OR dsi.valid_to IS NULL)");
            parametersList.add(currentDate);
            parametersList.add(currentDate);
          } 
          
          bundleWhereCondition.append(" )");
          if (orCounter++ != identifiers.size()) {
            bundleWhereCondition.append(" OR ");
          }
        }
      }
      if (identifierValue != null) {
        if (!parametersList.isEmpty()) {
          bundleWhereCondition.append(" OR ");
        }
        String identifierValueQuery = getDbSource().getDialect().sqlWildcardQuery("dsi.identifier_value ", "?", identifierValue);
        bundleWhereCondition.append(identifierValueQuery);
        String adjustedIdentifierValue = getDbSource().getDialect().sqlWildcardAdjustValue(identifierValue);
        parametersList.add(adjustedIdentifierValue);
        
        if (currentDate != null) {
          bundleWhereCondition.append("AND (dsi.valid_from <= ?  OR dsi.valid_from IS NULL) AND (dsi.valid_to >= ? OR dsi.valid_to IS NULL)");
          parametersList.add(currentDate);
          parametersList.add(currentDate);
        }
      }
      bundleWhereCondition.append(" ");
      findIdentifiersSql = StringUtils.replace(namedSql, ":identifierBundleClause", bundleWhereCondition.toString());
      
      parameters = parametersList.toArray();
    }
    
    JdbcOperations jdbcOperations = getJdbcTemplate().getJdbcOperations();
    jdbcOperations.query(findIdentifiersSql, parameters, rowHandler);
    
    return rowHandler.getResult();
  }

  private long createObservationTime(String observationTime, String description) {
    String sql = _namedSQLMap.get(INSERT_OBSERVATION_TIME);
    insertNamedDimension(sql, observationTime, description);
    return getObservationTimeId(observationTime);
  }

  private long createBundle(String name, String description) {
    String sql = _namedSQLMap.get(INSERT_QUOTED_OBJECT);
    insertNamedDimension(sql, name, description);
    return getBundleId(name);
  }

  private long createDataField(String field, String description) {
    String sql = _namedSQLMap.get(INSERT_DATA_FIELD);
    insertNamedDimension(sql, field, description);
    return getDataFieldId(field);
  }

  private long getDataProviderId(String name) {
    s_logger.debug("looking up id for dataProvider={}", name);
    String sql = _namedSQLMap.get(SELECT_DATA_PROVIDER_ID);
    return getNamedDimensionId(sql, name);
  }

  private long getNamedDimensionId(final String sql, final String name) {
    s_logger.debug("looking up id from sql={} with name={}", sql, name);
    SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", name);

    long result = INVALID_KEY;
    try {
      result = getJdbcTemplate().queryForInt(sql, parameters);
    } catch (EmptyResultDataAccessException e) {
      s_logger.debug("Empty row return for name = {} from sql = {}", name, sql);
      result = INVALID_KEY;
    }
    s_logger.debug("id = {}", result);
    return result;
  }

  private long getDataSourceId(String name) {
    s_logger.debug("looking up id for dataSource={}", name);
    String sql = _namedSQLMap.get(SELECT_DATA_SOURCE_ID);
    return getNamedDimensionId(sql, name);
  }

  private long getDataFieldId(String name) {
    s_logger.debug("looking up id for dataField={}", name);
    String sql = _namedSQLMap.get(SELECT_DATA_FIELD_ID);
    return getNamedDimensionId(sql, name);
  }

  private long getObservationTimeId(String name) {
    s_logger.debug("looking up id for observationTime={}", name);
    String sql = _namedSQLMap.get(SELECT_OBSERVATION_TIME_ID);
    return getNamedDimensionId(sql, name);
  }

  private long getBundleId(String name) {
    s_logger.debug("looking up id for bundle={}", name);
    String sql = _namedSQLMap.get(SELECT_QUOTED_OBJECT_ID);
    return getNamedDimensionId(sql, name);
  }
  
  private long getSchemeId(String name) {
    s_logger.debug("looking up id for domain={}", name);
    String sql = _namedSQLMap.get(SELECT_SCHEME_ID);
    return getNamedDimensionId(sql, name);
  }
  
  private long createScheme(String scheme, String description) {
    String sql = _namedSQLMap.get(INSERT_SCHEME);
    insertNamedDimension(sql, scheme, description);
    return getSchemeId(scheme);
  }
  
  private long createTimeSeriesKey(long bundleId, String dataSource,
      String dataProvider, String dataField, String observationTime) {
    
    s_logger.debug("creating timeSeriesKey with quotedObjId={}, dataSource={}, dataProvider={}, dataField={}, observationTime={}", 
        new Object[]{bundleId, dataSource, dataProvider, dataField, observationTime});
    
    DataSourceBean dataSourceBean = getOrCreateDataSource(dataSource, null);
    DataProviderBean dataProviderBean = getOrCreateDataProvider(dataProvider, null);
    DataFieldBean dataFieldBean = getOrCreateDataField(dataField, null);
    ObservationTimeBean observationTimeBean = getOrCreateObservationTime(observationTime, null);
    
    String sql = _namedSQLMap.get(INSERT_TIME_SERIES_KEY);
    
    SqlParameterSource parameterSource = new MapSqlParameterSource()
      .addValue("bundleId", bundleId)
      .addValue("dataSourceId", dataSourceBean.getId())
      .addValue("dataProviderId", dataProviderBean.getId())
      .addValue("dataFieldId", dataFieldBean.getId())
      .addValue("observationTimeId", observationTimeBean.getId());
    
    getJdbcTemplate().update(sql, parameterSource);
    
    return getActiveTimeSeriesKey(bundleId, dataSourceBean.getId(), dataProviderBean.getId(), dataFieldBean.getId(), observationTimeBean.getId());
  }

  private long getActiveTimeSeriesKey(long quotedObjId, long dataSourceId, long dataProviderId, long dataFieldId, long observationTimeId) {
    long result = INVALID_KEY;
    s_logger.debug("looking up timeSeriesKey quotedObjId={}, dataSourceId={}, dataProviderId={}, dataFieldId={}, observationTimeId={}", 
        new Object[]{quotedObjId, dataSourceId, dataProviderId, dataFieldId, observationTimeId});
    String sql = _namedSQLMap.get(GET_ACTIVE_TIME_SERIES_KEY_BY_ID);
    SqlParameterSource parameterSource = new MapSqlParameterSource()
      .addValue("qoid", quotedObjId, Types.BIGINT)
      .addValue("dsid", dataSourceId, Types.BIGINT)
      .addValue("dpid", dataProviderId, Types.BIGINT)
      .addValue("dfid", dataFieldId, Types.BIGINT)
      .addValue("otid", observationTimeId, Types.BIGINT);
    try {
      result = getJdbcTemplate().queryForInt(sql, parameterSource);
    } catch (EmptyResultDataAccessException e) {
      s_logger.debug("Empty row returned for timeSeriesKeyID");
      result = INVALID_KEY;
    }
    s_logger.debug("timeSeriesKeyID = {}", result);
    return result;
  }
  
  private void deleteDataPoints(long tsId) {
    
    s_logger.debug("deleting timeseries with id = {}", tsId);
    
    String deleteSql = _namedSQLMap.get(DELETE_TIME_SERIES_BY_ID);
    MapSqlParameterSource tsIDParameter = new MapSqlParameterSource().addValue("tsID", tsId, Types.BIGINT);
    
    if (!isTriggerSupported()) {
      String selectTSSQL = _namedSQLMap.get(GET_TIME_SERIES_BY_ID);
      List<Pair<T, Double>> queryResult = getJdbcTemplate().query(selectTSSQL, new RowMapper<Pair<T, Double>>() {
        @Override
        public Pair<T, Double> mapRow(ResultSet rs, int rowNum) throws SQLException {
          double tsValue = rs.getDouble("value");
          T tsDate = getDate(rs, "ts_date");
          return Pair.of(tsDate, tsValue);
        }
      }, tsIDParameter);
      
      String insertDelta = _namedSQLMap.get(INSERT_TIME_SERIES_DELTA_D);
      Date now = new Date(System.currentTimeMillis());
      SqlParameterSource[] batchArgs = new MapSqlParameterSource[queryResult.size()];
      int i = 0;
      for (Pair<T, Double> pair : queryResult) {
        T date = pair.getFirst();
        Double value = pair.getSecond();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("timeSeriesID", tsId, Types.BIGINT);
        parameterSource.addValue("date", getSqlDate(date), getSqlDateType());
        parameterSource.addValue("value", value, Types.DOUBLE);
        parameterSource.addValue("timeStamp", now, Types.TIMESTAMP);
        batchArgs[i++] = parameterSource;
      }
      
      getJdbcTemplate().batchUpdate(insertDelta, batchArgs);
    }
      
    getJdbcTemplate().update(deleteSql, tsIDParameter);
    
  }
    
  private DoubleTimeSeries<T> loadTimeSeries(long timeSeriesKey, T start, T end) {
    String sql = _namedSQLMap.get(LOAD_TIME_SERIES_WITH_DATES);
    MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("timeSeriesKey", timeSeriesKey, Types.INTEGER);
    
    if (start != null) {
      sql += " AND ts_date >= :startDate";
      parameters.addValue("startDate", getSqlDate(start), getSqlDateType());
    }
    
    if (end != null) {
      sql += " AND ts_date <= :endDate";
      parameters.addValue("endDate", getSqlDate(end), getSqlDateType());
    }
    
    sql += " ORDER BY ts_date";
    
    final List<T> dates = new LinkedList<T>();
    final List<Double> values = new LinkedList<Double>();
    
    NamedParameterJdbcOperations parameterJdbcOperations = getJdbcTemplate().getNamedParameterJdbcOperations();
    parameterJdbcOperations.query(sql, parameters, new RowCallbackHandler() {
      
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        dates.add(getDate(rs, "ts_date"));
        values.add(rs.getDouble("value"));
      }
    });
    
    return getTimeSeries(dates, values);
  }

  private void updateDataPoint(T date, Double value, long tsID) {
    String selectSQL = _namedSQLMap.get(FIND_DATA_POINT_BY_DATE_AND_ID);
    
    MapSqlParameterSource parameters = new MapSqlParameterSource()
      .addValue("tsID", tsID, Types.BIGINT)
      .addValue("date", getSqlDate(date), getSqlDateType());
    
    Double oldValue = getJdbcTemplate().queryForObject(selectSQL, Double.class, parameters);
    
    String updateSql = _namedSQLMap.get(UPDATE_TIME_SERIES);
    String insertDelta = _namedSQLMap.get(INSERT_TIME_SERIES_DELTA_U);
    
    Date now = new Date(System.currentTimeMillis());
    
    parameters.addValue("timeStamp", now, Types.TIMESTAMP);
    parameters.addValue("oldValue", oldValue, Types.DOUBLE);
    parameters.addValue("newValue", value, Types.DOUBLE);
    
    if (!isTriggerSupported()) {
      getJdbcTemplate().update(insertDelta, parameters);
    }
    getJdbcTemplate().update(updateSql, parameters);
  }
  
  private void removeDataPoint(long tsID, T date) {
    String selectTSSQL = _namedSQLMap.get(FIND_DATA_POINT_BY_DATE_AND_ID);
    MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("tsID", tsID, Types.INTEGER);
    parameters.addValue("date", getSqlDate(date), getSqlDateType());
    
    Double oldValue = getJdbcTemplate().queryForObject(selectTSSQL, Double.class, parameters);
    
    String deleteSql = _namedSQLMap.get(DELETE_DATA_POINT);
    String insertDelta = _namedSQLMap.get(INSERT_TIME_SERIES_DELTA_D);
    
    Date now = new Date(System.currentTimeMillis());
    
    MapSqlParameterSource deltaParameters = new MapSqlParameterSource();
    deltaParameters.addValue("timeSeriesID", tsID, Types.INTEGER);
    deltaParameters.addValue("date", getSqlDate(date), getSqlDateType());
    deltaParameters.addValue("value", oldValue, Types.DOUBLE);
    deltaParameters.addValue("timeStamp", now, Types.TIMESTAMP);
    
    if (!isTriggerSupported()) {
      getJdbcTemplate().update(insertDelta, deltaParameters);
    }
    getJdbcTemplate().update(deleteSql, parameters);
  }
    
  private DoubleTimeSeries<T> getTimeSeriesSnapshot(Instant timeStamp, long tsID) {
    String selectDeltaSql = _namedSQLMap.get(LOAD_TIME_SERIES_DELTA);
    
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue("time", new Date(timeStamp.toEpochMillisLong()), Types.TIMESTAMP);
    parameterSource.addValue("tsID", tsID, Types.BIGINT);

    final List<T> deltaDates = new ArrayList<T>();
    final List<Double> deltaValues = new ArrayList<Double>();
    final List<String> deltaOperations = new ArrayList<String>();
    
    NamedParameterJdbcOperations jdbcOperations = getJdbcTemplate().getNamedParameterJdbcOperations();
    
    jdbcOperations.query(selectDeltaSql, parameterSource, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        deltaDates.add(getDate(rs, "ts_date"));
        deltaValues.add(rs.getDouble("old_value"));
        deltaOperations.add(rs.getString("operation"));
      }
    });
    
    DoubleTimeSeries<T> timeSeries = loadTimeSeries(tsID, null, null);
    
    MutableDoubleTimeSeries<T> tsMap = getMutableTimeSeries(timeSeries); 
    
    //reapply deltas
    for (int i = 0; i < deltaDates.size(); i++) {
      T date = deltaDates.get(i);
      Double oldValue = deltaValues.get(i);
      String operation = deltaOperations.get(i);
      if (operation.toUpperCase().equals("I")) {
        tsMap.removeDataPoint(date);
      }
      if (operation.toUpperCase().equals("D") || operation.toUpperCase().equals("U")) {
        tsMap.putDataPoint(date, oldValue);
      }
    }
    
    return tsMap;
  }
  
  /**
   * @param namedSQLMap the map containing sql queries
   */
  protected void checkNamedSQLMap(Map<String, String> namedSQLMap) {
    ArgumentChecker.notNull(namedSQLMap, "namedSQLMap");
    for (String queryName : SQL_MAP_KEYS) {
      checkSQLQuery(queryName, namedSQLMap);
    }
  }

  private void checkSQLQuery(String key, Map<String, String> namedSQLMap) {
    if (StringUtils.isBlank(namedSQLMap.get(key))) {
      s_logger.warn(key + " query is missing from injected SQLMap when creating " + getClass());
      throw new IllegalArgumentException(key + " query is missing from injected SQLMap when creating " + getClass());
    }
  }

  protected DoubleTimeSeries<T> getHistoricalTimeSeries(UniqueIdentifier uid, T start, T end) {
    ArgumentChecker.notNull(uid, "UniqueIdentifier");
    ArgumentChecker.isTrue(uid.getScheme().equals(_identifierScheme), "Uid not for TimeSeriesStorage");
    ArgumentChecker.isTrue(uid.getValue() != null, "Uid value cannot be null");
    int timeSeriesKey = Integer.parseInt(uid.getValue());
    DoubleTimeSeries<T> timeSeries = loadTimeSeries(timeSeriesKey, start, end);
    return timeSeries;
  }

  protected DoubleTimeSeries<T> getHistoricalTimeSeries(UniqueIdentifier uid) {
    return getHistoricalTimeSeries(uid, null, null);
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public TimeSeriesDocument<T> addTimeSeries(TimeSeriesDocument<T> document) {
    validateTimeSeriesDocument(document);
    if (!contains(document)) {
      UniqueIdentifier uid = addTimeSeries(
          document.getIdentifiers(), 
          document.getDataSource(), 
          document.getDataProvider(), 
          document.getDataField(), 
          document.getObservationTime(),
          document.getTimeSeries());
      document.setUniqueIdentifier(uid);
      return document;
    } else {
      throw new IllegalArgumentException("cannot add duplicate TimeSeries for identifiers " + document.getIdentifiers());
    }
  }
 
  private boolean contains(TimeSeriesDocument<T> document) {
    for (IdentifierWithDates identifierWithDates : document.getIdentifiers()) {
      Identifier identifier = identifierWithDates.asIdentifier();
      UniqueIdentifier uid = resolveIdentifier(
          IdentifierBundle.of(identifier), 
          identifierWithDates.getValidFrom(), 
          document.getDataSource(), 
          document.getDataProvider(), 
          document.getDataField());
      if (uid != null) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void appendTimeSeries(TimeSeriesDocument<T> document) {
    Long tsId = validateAndGetTimeSeriesId(document.getUniqueIdentifier());
    insertDataPoints(document.getTimeSeries(), tsId);
  }

  private void validateTimeSeriesDocument(TimeSeriesDocument<T> document) {
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
  
  private MetaData<T> getTimeSeriesMetaData(long tsId) {
    
    MetaData<T> result = new MetaData<T>();
    
    final Set<IdentifierWithDates> identifiers = new HashSet<IdentifierWithDates>();
    final Set<String> dataSourceSet = new HashSet<String>();
    final Set<String> dataProviderSet = new HashSet<String>();
    final Set<String> dataFieldSet = new HashSet<String>();
    final Set<String> observationTimeSet = new HashSet<String>();
    final Set<Long> tsKeySet = new HashSet<Long>();
    
    String sql = _namedSQLMap.get(GET_ACTIVE_META_DATA_BY_OID);
    MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("oid", tsId, Types.BIGINT);
    NamedParameterJdbcOperations parameterJdbcOperations = getJdbcTemplate().getNamedParameterJdbcOperations();
    parameterJdbcOperations.query(sql, parameters, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        String scheme = rs.getString("scheme");
        String value = rs.getString("value");
        Identifier identifier = Identifier.of(scheme, value);
        Date date = rs.getDate("valid_from");
        LocalDate validFrom = date != null ? DbDateUtils.fromSqlDate(date) : null;
        date = rs.getDate("valid_to");
        LocalDate validTo = date != null ? DbDateUtils.fromSqlDate(date) : null;
        identifiers.add(IdentifierWithDates.of(identifier, validFrom, validTo));
        dataSourceSet.add(rs.getString("dataSource"));
        dataProviderSet.add(rs.getString("dataProvider"));
        dataFieldSet.add(rs.getString("dataField"));
        observationTimeSet.add(rs.getString("observationTime"));
        tsKeySet.add(rs.getLong("tsKey"));
      }
    });
    
    if (tsKeySet.isEmpty()) {
      s_logger.debug("TimeSeries not found id: {}", tsId);
      throw new DataNotFoundException("TimeSeries not found id: " + tsId);
    }
    
    
    
    result.setIdentifiers(new IdentifierBundleWithDates(identifiers));
    assert (dataFieldSet.size() == 1);
    result.setDataField(dataFieldSet.iterator().next());
    assert (dataProviderSet.size() == 1);
    result.setDataProvider(dataProviderSet.iterator().next());
    assert (dataSourceSet.size() == 1);
    result.setDataSource(dataSourceSet.iterator().next());
    assert (observationTimeSet.size() == 1);
    result.setObservationTime(observationTimeSet.iterator().next());
    assert (tsKeySet.size() == 1);
    
    return result;
    
  }

  @Override
  public TimeSeriesDocument<T> getTimeSeries(UniqueIdentifier uid) {
    Long tsId = validateAndGetTimeSeriesId(uid);
    
    TimeSeriesDocument<T> result = new TimeSeriesDocument<T>();
    result.setUniqueIdentifier(uid);
    
    MetaData<T> metaData = getTimeSeriesMetaData(tsId);
    
    result.setIdentifiers(metaData.getIdentifiers());
    result.setDataField(metaData.getDataField());
    result.setDataProvider(metaData.getDataProvider());
    result.setDataSource(metaData.getDataSource());
    result.setObservationTime(metaData.getObservationTime());
    DoubleTimeSeries<T> timeSeries = loadTimeSeries(tsId, null, null);
    result.setTimeSeries(timeSeries);
    
    return result;
  }
  
  private ObjectsPair<Long, T> validateAndGetDataPointId(UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "DataPoint UID");
    ArgumentChecker.isTrue(uid.getScheme().equals(_identifierScheme), "UID not TSS");
    ArgumentChecker.isTrue(uid.getValue() != null, "Uid value cannot be null");
    String[] tokens = StringUtils.split(uid.getValue(), '/');
    if (tokens.length != 2) {
      throw new IllegalArgumentException("UID not expected format<12345/date> " + uid);
    }
    String id = tokens[0];
    String dateStr = tokens[1];
    T date = null;
    Long tsId = Long.MIN_VALUE;
    if (id != null && dateStr != null) {
      try {
        date = getDate(dateStr);
      } catch (CalendricalParseException ex) {
        throw new IllegalArgumentException("UID not expected format<12345/date> " + uid, ex);
      }
      try {
        tsId = Long.parseLong(id);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("UID not expected format<12345/date> " + uid, ex);
      }
    } else {
      throw new IllegalArgumentException("UID not expected format<12345/date> " + uid);
    }
    return ObjectsPair.of(tsId, date);
  }

  private Long validateAndGetTimeSeriesId(UniqueIdentifier uid) {
    ArgumentChecker.notNull(uid, "TimeSeries UID");
    ArgumentChecker.isTrue(uid.getScheme().equals(_identifierScheme), "UID not TSS");
    ArgumentChecker.isTrue(uid.getValue() != null, "Uid value cannot be null");
    
    Long tsId = Long.MIN_VALUE;
    
    try {
      tsId = Long.parseLong(uid.getValue());
    } catch (NumberFormatException ex) {
      s_logger.warn("Invalid UID {}", uid);
      throw new IllegalArgumentException("Invalid UID " + uid);
    }
    return tsId;
  }

  @Override
  public List<DataFieldBean> getDataFields() {
    List<DataFieldBean> result = new ArrayList<DataFieldBean>();
    for (NamedDescriptionBean bean : loadEnumWithDescription(_namedSQLMap.get(LOAD_ALL_DATA_FIELDS))) {
      DataFieldBean dataField = new DataFieldBean(bean.getName(), bean.getDescription());
      dataField.setId(bean.getId());
      result.add(dataField);
    }
    return result;
  }

  @Override
  public List<DataProviderBean> getDataProviders() {
    List<DataProviderBean> result = new ArrayList<DataProviderBean>();
    for (NamedDescriptionBean bean : loadEnumWithDescription(_namedSQLMap.get(LOAD_ALL_DATA_PROVIDER))) {
      DataProviderBean dataProviderBean = new DataProviderBean(bean.getName(), bean.getDescription());
      dataProviderBean.setId(bean.getId());
      result.add(dataProviderBean);
    }
    return result;
  }

  @Override
  public List<DataSourceBean> getDataSources() {
    List<DataSourceBean> result = new ArrayList<DataSourceBean>();
    for (NamedDescriptionBean bean : loadEnumWithDescription(_namedSQLMap.get(LOAD_ALL_DATA_SOURCES))) {
      DataSourceBean dataSourceBean = new DataSourceBean(bean.getName(), bean.getDescription());
      dataSourceBean.setId(bean.getId());
      result.add(dataSourceBean);
    }
    return result;
  }

  @Override
  public List<ObservationTimeBean> getObservationTimes() {
    List<ObservationTimeBean> result = new ArrayList<ObservationTimeBean>();
    for (NamedDescriptionBean bean : loadEnumWithDescription(_namedSQLMap.get(LOAD_ALL_OBSERVATION_TIMES))) {
      ObservationTimeBean obBean = new ObservationTimeBean(bean.getName(), bean.getDescription());
      obBean.setId(bean.getId());
      result.add(obBean);
    }
    return result;
  }
  
  

  @Override
  public List<SchemeBean> getSchemes() {
    List<SchemeBean> result = new ArrayList<SchemeBean>();
    for (NamedDescriptionBean bean : loadEnumWithDescription(_namedSQLMap.get(LOAD_ALL_SCHEME))) {
      SchemeBean schemeBean = new SchemeBean(bean.getName(), bean.getDescription());
      schemeBean.setId(bean.getId());
      result.add(schemeBean);
    }
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public DataFieldBean getOrCreateDataField(String field, String description) {
    long id = getDataFieldId(field);
    if (id == INVALID_KEY) {
      id = createDataField(field, description);
    }
    DataFieldBean result = new DataFieldBean(field, description);
    result.setId(id);
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public DataProviderBean getOrCreateDataProvider(String dataProvider, String description) {
    long id = getDataProviderId(dataProvider);
    if (id == INVALID_KEY) {
      id = createDataProvider(dataProvider, description);
    }
    DataProviderBean result = new DataProviderBean(dataProvider, description);
    result.setId(id);
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public DataSourceBean getOrCreateDataSource(String dataSource, String description) {
    long id = getDataSourceId(dataSource);
    if (id == INVALID_KEY) {
      id = createDataSource(dataSource, description);
    }
    DataSourceBean result = new DataSourceBean(dataSource, description);
    result.setId(id);
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public SchemeBean getOrCreateScheme(String scheme, String description) {
    long id = getSchemeId(scheme);
    if (id == INVALID_KEY) {
      id = createScheme(scheme, description);
    }
    SchemeBean result = new SchemeBean(scheme, description);
    result.setId(id);
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public ObservationTimeBean getOrCreateObservationTime(String observationTime, String description) {
    long id = getObservationTimeId(observationTime);
    if (id == INVALID_KEY) {
      id = createObservationTime(observationTime, null);
    }
    ObservationTimeBean result = new ObservationTimeBean(observationTime, description);
    result.setId(id);
    return result;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void removeTimeSeries(UniqueIdentifier uid) {
    Long tsId = validateAndGetTimeSeriesId(uid);
    SqlParameterSource parameters = new MapSqlParameterSource()
      .addValue("tsKey", tsId, Types.BIGINT);
    getJdbcTemplate().update(_namedSQLMap.get(DEACTIVATE_META_DATA), parameters);
    deleteDataPoints(tsId);
  }

  @Override
  public TimeSeriesSearchResult<T> searchTimeSeries(TimeSeriesSearchRequest<T> request) {
    ArgumentChecker.notNull(request, "timeseries request");
    
    TimeSeriesSearchResult<T> result = new TimeSeriesSearchResult<T>();
    UniqueIdentifier uid = request.getTimeSeriesId();
    if (uid != null) {
      long tsId = validateAndGetTimeSeriesId(uid);
      MetaData<T> tsMetaData = getTimeSeriesMetaData(tsId);
      s_logger.debug("tsMetaData={}", tsMetaData);
      TimeSeriesDocument<T> document = new TimeSeriesDocument<T>();
      document.setDataField(tsMetaData.getDataField());
      document.setDataProvider(tsMetaData.getDataProvider());
      document.setDataSource(tsMetaData.getDataSource());
      document.setIdentifiers(tsMetaData.getIdentifiers());
      document.setObservationTime(tsMetaData.getObservationTime());
      document.setUniqueIdentifier(uid);
      if (request.isLoadDates()) {
        //load timeseries date ranges
        Map<String, T> dates = getTimeSeriesDateRange(tsId);
        tsMetaData.setEarliestDate(dates.get("earliest"));
        tsMetaData.setLatestDate(dates.get("latest"));
      }
      if (request.isLoadTimeSeries()) {
        DoubleTimeSeries<T> timeSeries = loadTimeSeries(tsId, request.getStart(), request.getEnd());
        document.setTimeSeries(timeSeries);
      }
      result.getDocuments().add(document);
    } else {
      String dataSource = request.getDataSource();
      String dataProvider = request.getDataProvider();
      String dataField = request.getDataField();
      String observationTime = request.getObservationTime();
      String metaDataSql = null;
      Collection<Identifier> requestIdentifiers = request.getIdentifiers();
      Date currentDate = null;
      if (request.getCurrentDate() != null) {
        currentDate = DbDateUtils.toSqlDate(request.getCurrentDate());
      }
      Map<Long, List<IdentifierWithDates>> bundles = searchIdentifierBundles(requestIdentifiers, request.getIdentifierValue(), currentDate);
      
      boolean useBundleIds = (requestIdentifiers != null && !requestIdentifiers.isEmpty()) || request.getIdentifierValue() != null;
      
      if (request.isLoadDates()) {
        if (useBundleIds) {
          metaDataSql = _namedSQLMap.get(GET_ACTIVE_META_DATA_WITH_DATES_BY_IDENTIFIERS);
        } else {
          metaDataSql = _namedSQLMap.get(GET_ACTIVE_META_DATA_WITH_DATES);
        }
      } else {
        if (useBundleIds) {
          if (bundles.isEmpty()) {
            return result;
          }
          metaDataSql = _namedSQLMap.get(GET_ACTIVE_META_DATA_BY_IDENTIFIERS);
        } else {
          metaDataSql = _namedSQLMap.get(GET_ACTIVE_META_DATA);
        }
      }
      metaDataSql = metaDataSql.toUpperCase();
      MapSqlParameterSource parameters = new MapSqlParameterSource();
      if (dataSource != null) {
        metaDataSql +=  " AND DS.NAME = :DATASOURCE ";
        parameters.addValue("DATASOURCE", dataSource, Types.VARCHAR);
      }
      if (dataProvider != null) {
        metaDataSql +=  " AND DP.NAME = :DATAPROVIDER ";
        parameters.addValue("DATAPROVIDER", dataProvider, Types.VARCHAR);
      }
      if (dataField != null) {
        metaDataSql +=  " AND DF.NAME = :DATAFIELD ";
        parameters.addValue("DATAFIELD", dataField, Types.VARCHAR);
      }
      if (observationTime != null) {
        metaDataSql +=  " AND OT.NAME = :OBSERVATIONTIME ";
        parameters.addValue("OBSERVATIONTIME", observationTime, Types.VARCHAR);
      }
      
      if (useBundleIds) {
        parameters.addValue("BUNDLEIDS", bundles.keySet());
      }
      TimeSeriesMetaDataRowMapper<T> rowMapper = new TimeSeriesMetaDataRowMapper<T>(this);
      rowMapper.setLoadDates(request.isLoadDates());
      
      String countSql = createTotalCountSql(metaDataSql);
      int count = getJdbcTemplate().queryForInt(countSql, parameters);
      String sqlApplyPaging = getDbSource().getDialect().sqlApplyPaging(metaDataSql, StringUtils.EMPTY, request.getPagingRequest());
      
      List<MetaData<T>> tsMetaDataList = getJdbcTemplate().query(sqlApplyPaging, rowMapper, parameters);
      for (MetaData<T> tsMetaData : tsMetaDataList) {
        TimeSeriesDocument<T> document = new TimeSeriesDocument<T>();
        Long bundleId = tsMetaData.getIdentifierBundleId();
        long timeSeriesKey = tsMetaData.getTimeSeriesId();
        document.setDataField(tsMetaData.getDataField());
        document.setDataProvider(tsMetaData.getDataProvider());
        document.setDataSource(tsMetaData.getDataSource());
        
        List<IdentifierWithDates> identifiers = bundles.get(bundleId);
        
        document.setIdentifiers(new IdentifierBundleWithDates(identifiers));
        document.setObservationTime(tsMetaData.getObservationTime());
        document.setUniqueIdentifier(UniqueIdentifier.of(IDENTIFIER_SCHEME_DEFAULT, String.valueOf(tsMetaData.getTimeSeriesId())));
        if (request.isLoadDates()) {
          document.setEarliest(tsMetaData.getEarliestDate());
          document.setLatest(tsMetaData.getLatestDate());
        }
        if (request.isLoadTimeSeries()) {
          DoubleTimeSeries<T> loadTimeSeries = loadTimeSeries(timeSeriesKey, request.getStart(), request.getEnd());
          document.setTimeSeries(loadTimeSeries);
        }
        result.getDocuments().add(document);
      }
      result.setPaging(new Paging(request.getPagingRequest(), count));
    }
    return result;
  }

  private String createTotalCountSql(String metaDataSql) {
    StringBuilder buf = new StringBuilder();
    int fromIndex = metaDataSql.indexOf("FROM");
    if (fromIndex != -1) {
      buf.append("SELECT COUNT(*) FROM  ");
      buf.append(metaDataSql.substring(fromIndex + 4));
    }
    return buf.toString();
  }

  private Map<String, T> getTimeSeriesDateRange(long tsId) {
    final Map<String, T> result = new HashMap<String, T>();
    NamedParameterJdbcOperations jdbcOperations = getJdbcTemplate().getNamedParameterJdbcOperations();
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("oid", tsId, Types.BIGINT);
    jdbcOperations.query(_namedSQLMap.get(GET_TS_DATE_RANGE_BY_OID), parameters, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        result.put("earliest", getDate("earliest"));
        result.put("latest", getDate("latest"));
      }
    });
    return result;
  }

  @Override
  public TimeSeriesSearchHistoricResult<T> searchHistoric(TimeSeriesSearchHistoricRequest request) {
    ArgumentChecker.notNull(request, "TimeSeriesSearchHistoricRequest");
    ArgumentChecker.notNull(request.getTimestamp(), "Timestamp");
    UniqueIdentifier uid = request.getTimeSeriesId();
    TimeSeriesSearchHistoricResult<T>  searchResult = new TimeSeriesSearchHistoricResult<T>();
    if (uid == null) {
      validateSearchHistoricRequest(request);
      String dataProvider = request.getDataProvider();
      String dataSource = request.getDataSource();
      String field = request.getDataField();
      IdentifierBundle identifiers = request.getIdentifiers();
      LocalDate currentDate = request.getCurrentDate();
      uid = resolveIdentifier(identifiers, currentDate, dataSource, dataProvider, field);
      if (uid == null) {
        return searchResult;
      }
    }
    Instant timeStamp = request.getTimestamp();
    long tsId = validateAndGetTimeSeriesId(uid);
    DoubleTimeSeries<T> seriesSnapshot = getTimeSeriesSnapshot(timeStamp, tsId);
    TimeSeriesDocument<T> document = new TimeSeriesDocument<T>();
    document.setDataField(request.getDataField());
    document.setDataProvider(request.getDataProvider());
    document.setDataSource(request.getDataSource());
    document.setIdentifiers(IdentifierBundleWithDates.of(request.getIdentifiers()));
    document.setObservationTime(request.getObservationTime());
    document.setUniqueIdentifier(uid);
    document.setTimeSeries(seriesSnapshot);
    searchResult.getDocuments().add(document);
    return searchResult;
  }

  private void validateSearchHistoricRequest(TimeSeriesSearchHistoricRequest request) {
    ArgumentChecker.isTrue(request.getIdentifiers() != null && !request.getIdentifiers().getIdentifiers().isEmpty(), "cannot search with null/empty identifiers");
    ArgumentChecker.isTrue(!StringUtils.isBlank(request.getDataSource()), "cannot search with blank dataSource");
    ArgumentChecker.isTrue(!StringUtils.isBlank(request.getDataProvider()), "cannot search with blank dataProvider");
    ArgumentChecker.isTrue(!StringUtils.isBlank(request.getDataProvider()), "cannot search with blank dataProvider");
    ArgumentChecker.isTrue(!StringUtils.isBlank(request.getDataField()), "cannot search with blank field");
    ArgumentChecker.isTrue(!StringUtils.isBlank(request.getDataProvider()), "cannot add timeseries with blank dataProvider");
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public TimeSeriesDocument<T> updateTimeSeries(TimeSeriesDocument<T> document) {
    ArgumentChecker.notNull(document, "timeseries document");
    ArgumentChecker.notNull(document.getTimeSeries(), "Timeseries");
    Long tsId = validateAndGetTimeSeriesId(document.getUniqueIdentifier());
    //check we have timeseries with given Id
    //getTimeSeriesMetaData() will throw DataNotFoundException if Id is not present
    getTimeSeriesMetaData(tsId);
    
    deleteDataPoints(tsId);
    insertDataPoints(document.getTimeSeries(), tsId);
    return document;
  }
  
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public DataPointDocument<T> updateDataPoint(DataPointDocument<T> document) {
    ArgumentChecker.notNull(document, "dataPoint document");
    ArgumentChecker.notNull(document.getDate(), "data point date");
    ArgumentChecker.notNull(document.getValue(), "data point value");
    Long tsId = validateAndGetTimeSeriesId(document.getTimeSeriesId());
    updateDataPoint(document.getDate(), document.getValue(), tsId);
    return document;
  }
  
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public DataPointDocument<T> addDataPoint(DataPointDocument<T> document) {
    ArgumentChecker.notNull(document, "dataPoint document");
    ArgumentChecker.notNull(document.getDate(), "data point date");
    ArgumentChecker.notNull(document.getValue(), "data point value");
    Long tsId = validateAndGetTimeSeriesId(document.getTimeSeriesId());
    
    String insertSQL = _namedSQLMap.get(INSERT_TIME_SERIES);
    String insertDelta = _namedSQLMap.get(INSERT_TIME_SERIES_DELTA_I);
    
    Date now = new Date(System.currentTimeMillis());
    
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue("timeSeriesID", tsId, Types.BIGINT);
    parameterSource.addValue("date", getSqlDate(document.getDate()), getSqlDateType());
    parameterSource.addValue("value", document.getValue(), Types.DOUBLE);
    parameterSource.addValue("timeStamp", now, Types.TIMESTAMP);
    
    if (!isTriggerSupported()) {
      getJdbcTemplate().update(insertDelta, parameterSource);
    } 
    getJdbcTemplate().update(insertSQL, parameterSource);
    String uid = new StringBuilder(String.valueOf(tsId)).append("/").append(printDate(document.getDate())).toString();
    document.setDataPointId(UniqueIdentifier.of(_identifierScheme, uid));
    return document;
  }
  
  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
  public void removeDataPoint(UniqueIdentifier uid) {
    ObjectsPair<Long, T> tsIdDatePair = validateAndGetDataPointId(uid);
    Long tsId = tsIdDatePair.getFirst();
    T date = tsIdDatePair.getSecond();
    removeDataPoint(tsId, date);
  }

  @Override
  public DataPointDocument<T> getDataPoint(UniqueIdentifier uid) {
    ObjectsPair<Long, T> tsIdDatePair = validateAndGetDataPointId(uid);
    
    Long tsId = tsIdDatePair.getFirst();
    T date = tsIdDatePair.getSecond();
    
    NamedParameterJdbcOperations jdbcOperations = getJdbcTemplate().getNamedParameterJdbcOperations();
    MapSqlParameterSource paramSource = new MapSqlParameterSource();
    paramSource.addValue("tsID", tsId, Types.BIGINT);
    paramSource.addValue("date", getSqlDate(date), getSqlDateType());
    
    final DataPointDocument<T> result = new DataPointDocument<T>();
    result.setDate(tsIdDatePair.getSecond());
    result.setTimeSeriesId(UniqueIdentifier.of(_identifierScheme, String.valueOf(tsId)));
    result.setDataPointId(uid);
    jdbcOperations.query(_namedSQLMap.get(FIND_DATA_POINT_BY_DATE_AND_ID), paramSource, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        result.setValue(rs.getDouble("value"));
      }
    });
       
    return result;
  }

  @Override
  public UniqueIdentifier resolveIdentifier(IdentifierBundle identifiers, LocalDate currentDate, String dataSource, String dataProvider, String field) {
    ArgumentChecker.notNull(identifiers, "identifiers");
    ArgumentChecker.notNull(dataSource, "dataSource");
    ArgumentChecker.notNull(dataProvider, "dataProvider");
    ArgumentChecker.notNull(field, "field");
    
    TimeSeriesSearchRequest<T> request = new TimeSeriesSearchRequest<T>();
    request.getIdentifiers().addAll(identifiers.getIdentifiers());
    request.setDataField(field);
    request.setDataProvider(dataProvider);
    request.setDataSource(dataSource);
    request.setCurrentDate(currentDate);
    request.setLoadTimeSeries(false);
    
    UniqueIdentifier result = null;
    TimeSeriesSearchResult<T> searchResult = searchTimeSeries(request);
    List<TimeSeriesDocument<T>> documents = searchResult.getDocuments();
    if (!documents.isEmpty()) {
      result = documents.get(0).getUniqueIdentifier();
    }
    return result;
  }
  
  @Override
  public UniqueIdentifier resolveIdentifier(IdentifierBundle identifiers, String dataSource, String dataProvider, String dataField) {
    return resolveIdentifier(identifiers, null, dataSource, dataProvider, dataField);
  }

  private long getOrCreateIdentifierBundle(String bundleName, String description, IdentifierBundleWithDates identifierBundleWithDates) {
    s_logger.debug("creating/updating identifiers {} with bundleName={}", identifierBundleWithDates, bundleName);
    
    long result = INVALID_KEY;
    
    String namedSql = _namedSQLMap.get(SELECT_BUNDLE_FROM_IDENTIFIERS);
    StringBuilder bundleWhereCondition = new StringBuilder(" ");
    List<Object> parameters = new ArrayList<Object>();
    String findIdentifiersSql = null;
    int orCounter = 1;
    for (IdentifierWithDates identifierWithDates : identifierBundleWithDates) {
      
      Identifier identifier = identifierWithDates.asIdentifier();
      Date validFrom = identifierWithDates.getValidFrom() != null ? DbDateUtils.toSqlDate(identifierWithDates.getValidFrom()) : null;
      Date validTo = identifierWithDates.getValidTo() != null ? DbDateUtils.toSqlDate(identifierWithDates.getValidTo()) : null;
      
      parameters.add(identifier.getScheme().getName());
      parameters.add(identifier.getValue());
      
      if (validFrom != null && validTo != null) {
        bundleWhereCondition.append("(d.name = ? AND dsi.identifier_value = ? AND dsi.valid_from = ? AND dsi.valid_to = ?)");
        parameters.add(validFrom);
        parameters.add(validTo);
      } else if (validFrom != null) {
        bundleWhereCondition.append("(d.name = ? AND dsi.identifier_value = ? AND dsi.valid_from = ?)");
        parameters.add(validFrom);
      } else if (validTo != null) {
        bundleWhereCondition.append("(d.name = ? AND dsi.identifier_value = ? AND dsi.valid_to = ?)");
        parameters.add(validTo);
      } else {
        bundleWhereCondition.append("(d.name = ? AND dsi.identifier_value = ?)");
      }
      
      if (orCounter++ != identifierBundleWithDates.size()) {
        bundleWhereCondition.append(" OR ");
      }
    }
    
    bundleWhereCondition.append(" ");
    
    findIdentifiersSql = StringUtils.replace(namedSql, ":identifierBundleClause", bundleWhereCondition.toString());
    JdbcOperations jdbcOperations = getJdbcTemplate().getJdbcOperations();
    IdentifierBundleHandler rowHandler = new IdentifierBundleHandler();
    jdbcOperations.query(findIdentifiersSql, parameters.toArray(), rowHandler);
    Map<Long, List<IdentifierWithDates>> bundleResult = rowHandler.getResult();
    
    if (bundleResult.size() == 1) {
      result = bundleResult.keySet().iterator().next();
    } else if (bundleResult.size() == 0) {
      long bundleId = getOrCreateIdentifierBundle(bundleName, description);
      List<MapSqlParameterSource> batchArgs = new ArrayList<MapSqlParameterSource>();
      for (IdentifierWithDates identifierWithDates : identifierBundleWithDates) {
        Identifier identifier = identifierWithDates.asIdentifier();
        Date validFrom = identifierWithDates.getValidFrom() != null ? DbDateUtils.toSqlDate(identifierWithDates.getValidFrom()) : null;
        Date validTo = identifierWithDates.getValidTo() != null ? DbDateUtils.toSqlDate(identifierWithDates.getValidTo()) : null;
        String scheme = identifier.getScheme().getName();
        NamedDescriptionBean schemeBean = getOrCreateScheme(scheme, null);
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("bundleId", bundleId);
        valueMap.put("schemeId", schemeBean.getId());
        valueMap.put("identifier_value", identifier.getValue());
        valueMap.put("valid_from", validFrom);
        valueMap.put("valid_to", validTo);
        batchArgs.add(new MapSqlParameterSource(valueMap));
      }
      getJdbcTemplate().batchUpdate(_namedSQLMap.get(INSERT_IDENTIFIER), batchArgs.toArray(new SqlParameterSource[0]));
      result = bundleId;
    } else {
      s_logger.warn("{} has more than one bundle ids associated to identifiers {}", identifierBundleWithDates);
      throw new OpenGammaRuntimeException(identifierBundleWithDates + " has more than one bundle ids associated to them, can not treat as same instrument");
    }
    return result;
  }

  private long getOrCreateIdentifierBundle(String quotedObj, String desc) {
    long result = getBundleId(quotedObj);
    if (result == INVALID_KEY) {
      result = createBundle(quotedObj, desc);
    }
    return result;
  }
  
  private List<NamedDescriptionBean> loadEnumWithDescription(String sql) {
    List<NamedDescriptionBean> result = new ArrayList<NamedDescriptionBean>();
    SqlParameterSource parameterSource = null;
    List<Map<String, Object>> sqlResult = getJdbcTemplate().queryForList(sql, parameterSource);
    for (Map<String, Object> element : sqlResult) {
      Long id = (Long) element.get("id");
      String name = (String) element.get("name");
      String desc = (String) element.get("description");
      NamedDescriptionBean bean = new NamedDescriptionBean();
      bean.setId(id);
      bean.setName(name);
      bean.setDescription(desc);
      result.add(bean);
    }
    return result;
  }
    
  private static class IdentifierBundleHandler implements RowCallbackHandler {
    private Map<Long, List<IdentifierWithDates>> _identifierBundleMap = new HashMap<Long, List<IdentifierWithDates>>();
    
    @Override
    public void processRow(ResultSet rs) throws SQLException {
      long bundleId = rs.getLong("bundleId");
      List<IdentifierWithDates> identifiers = _identifierBundleMap.get(bundleId);
      if (identifiers == null) {
        identifiers = new ArrayList<IdentifierWithDates>();
        _identifierBundleMap.put(bundleId, identifiers);
      }
      Identifier identifier = Identifier.of(rs.getString("scheme"), rs.getString("identifier_value"));
      Date date = rs.getDate("valid_from");
      LocalDate validFrom = date != null ? DbDateUtils.fromSqlDate(date) : null;
      date = rs.getDate("valid_to");
      LocalDate validTo = date != null ? DbDateUtils.fromSqlDate(date) : null;
      identifiers.add(IdentifierWithDates.of(identifier, validFrom, validTo));
    }
    
    public Map<Long, List<IdentifierWithDates>> getResult() {
      return _identifierBundleMap;
    }
    
  }

  @Override
  public void removeDataPoints(UniqueIdentifier timeSeriesUid, T firstDateToRetain) {
    Long tsId = validateAndGetTimeSeriesId(timeSeriesUid);
    
    if (!isTriggerSupported()) {
      
      // this may be rather slow if there are lots of points to be removed
      DoubleTimeSeries<T> timeSeries = loadTimeSeries(tsId, null, firstDateToRetain);
      //the last datapoint is included so dont delete that
      for (int i = 0; i < timeSeries.size() - 1; i++) {
        removeDataPoint(tsId, timeSeries.getTime(i));   
      }
    } else {
      
      String deleteSql = _namedSQLMap.get(DELETE_DATA_POINTS_BY_DATE);
      
      MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("tsID", tsId, Types.INTEGER);
      parameters.addValue("date", getSqlDate(firstDateToRetain), getSqlDateType());
      
      getJdbcTemplate().update(deleteSql, parameters);
    
    }

  }
 
}