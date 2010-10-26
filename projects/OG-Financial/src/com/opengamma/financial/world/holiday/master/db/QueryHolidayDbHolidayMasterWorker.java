/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.master.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.time.Instant;
import javax.time.calendar.LocalDate;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.base.Objects;
import com.opengamma.DataNotFoundException;
import com.opengamma.financial.world.holiday.HolidayType;
import com.opengamma.financial.world.holiday.master.HolidayDocument;
import com.opengamma.financial.world.holiday.master.HolidaySearchHistoricRequest;
import com.opengamma.financial.world.holiday.master.HolidaySearchHistoricResult;
import com.opengamma.financial.world.holiday.master.HolidaySearchRequest;
import com.opengamma.financial.world.holiday.master.HolidaySearchResult;
import com.opengamma.financial.world.holiday.master.ManageableHoliday;
import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.db.DbDateUtils;
import com.opengamma.util.db.DbMapSqlParameterSource;
import com.opengamma.util.db.Paging;

/**
 * Holiday master worker to get the holiday.
 */
public class QueryHolidayDbHolidayMasterWorker extends DbHolidayMasterWorker {

  /** Logger. */
  private static final Logger s_logger = LoggerFactory.getLogger(QueryHolidayDbHolidayMasterWorker.class);
  /**
   * SQL select.
   */
  protected static final String SELECT =
      "SELECT " +
        "h.id AS holiday_id, " +
        "h.oid AS holiday_oid, " +
        "h.ver_from_instant AS ver_from_instant, " +
        "h.ver_to_instant AS ver_to_instant, " +
        "h.corr_from_instant AS corr_from_instant, " +
        "h.corr_to_instant AS corr_to_instant, " +
        "h.name AS name, " +
        "h.hol_type AS hol_type, " +
        "h.region_scheme AS region_scheme, " +
        "h.region_value AS region_value, " +
        "h.exchange_scheme AS exchange_scheme, " +
        "h.exchange_value AS exchange_value, " +
        "h.currency_iso AS currency_iso, " +
        "d.hol_date AS hol_date ";
  /**
   * SQL from.
   */
  protected static final String FROM =
      "FROM hol_holiday h LEFT JOIN hol_date d ON (d.holiday_id = h.id) ";

  /**
   * Creates an instance.
   */
  public QueryHolidayDbHolidayMasterWorker() {
    super();
  }

  //-------------------------------------------------------------------------
  @Override
  protected HolidayDocument get(final UniqueIdentifier uid) {
    if (uid.isVersioned()) {
      return getById(uid);
    } else {
      return getByLatest(uid);
    }
  }

  /**
   * Gets a holiday by searching for the latest version of an object identifier.
   * @param uid  the unique identifier
   * @return the holiday document, null if not found
   */
  protected HolidayDocument getByLatest(final UniqueIdentifier uid) {
    s_logger.debug("getHolidayByLatest: {}", uid);
    final Instant now = Instant.now(getTimeSource());
    final HolidaySearchHistoricRequest request = new HolidaySearchHistoricRequest(uid, now, now);
    final HolidaySearchHistoricResult result = getMaster().searchHistoric(request);
    if (result.getDocuments().size() != 1) {
      throw new DataNotFoundException("Holiday not found: " + uid);
    }
    return result.getFirstDocument();
  }

  /**
   * Gets a holiday by identifier.
   * @param uid  the unique identifier
   * @return the holiday document, null if not found
   */
  protected HolidayDocument getById(final UniqueIdentifier uid) {
    s_logger.debug("getHolidayById {}", uid);
    final DbMapSqlParameterSource args = new DbMapSqlParameterSource()
      .addValue("holiday_id", extractRowId(uid));
    
    final HolidayDocumentExtractor extractor = new HolidayDocumentExtractor();
    final NamedParameterJdbcOperations namedJdbc = getJdbcTemplate().getNamedParameterJdbcOperations();
    final List<HolidayDocument> docs = namedJdbc.query(sqlGetHolidayById(), args, extractor);
    if (docs.isEmpty()) {
      throw new DataNotFoundException("Holiday not found: " + uid);
    }
    return docs.get(0);
  }

  /**
   * Gets the SQL for getting a holiday by unique row identifier.
   * @return the SQL, not null
   */
  protected String sqlGetHolidayById() {
    return SELECT + FROM + "WHERE h.id = :holiday_id ORDER BY d.hol_date ";
  }

  //-------------------------------------------------------------------------
  @Override
  protected HolidaySearchResult search(HolidaySearchRequest request) {
    s_logger.debug("searchHolidays: {}", request);
    final HolidaySearchResult result = new HolidaySearchResult();
    IdentifierBundle regionIds = request.getRegionIdentifiers();
    IdentifierBundle exchangeIds = request.getExchangeIdentifiers();
    String currencyISO = (request.getCurrency() != null ? request.getCurrency().getISOCode() : null);
    if (IdentifierBundle.EMPTY.equals(regionIds) || IdentifierBundle.EMPTY.equals(exchangeIds)) {
      return result;  // containsAny with empty bundle always returns nothing
    }
    final Instant now = Instant.now(getTimeSource());
    final DbMapSqlParameterSource args = new DbMapSqlParameterSource()
      .addTimestamp("version_as_of_instant", Objects.firstNonNull(request.getVersionAsOfInstant(), now))
      .addTimestamp("corrected_to_instant", Objects.firstNonNull(request.getCorrectedToInstant(), now))
      .addValueNullIgnored("name", getDbHelper().sqlWildcardAdjustValue(request.getName()))
      .addValueNullIgnored("hol_type", request.getType() != null ? request.getType().name() : null)
      .addValueNullIgnored("currency_iso", currencyISO);
    if (regionIds != null) {
      int i = 0;
      for (Identifier idKey : regionIds.getIdentifiers()) {
        args.addValue("region_scheme" + i, idKey.getScheme().getName());
        args.addValue("region_value" + i, idKey.getValue());
        i++;
      }
    }
    if (exchangeIds != null) {
      int i = 0;
      for (Identifier idKey : exchangeIds.getIdentifiers()) {
        args.addValue("exchange_scheme" + i, idKey.getScheme().getName());
        args.addValue("exchange_value" + i, idKey.getValue());
        i++;
      }
    }
    final String[] sql = sqlSearchHolidays(request);
    final NamedParameterJdbcOperations namedJdbc = getJdbcTemplate().getNamedParameterJdbcOperations();
    final int count = namedJdbc.queryForInt(sql[1], args);
    result.setPaging(new Paging(request.getPagingRequest(), count));
    if (count > 0) {
      final HolidayDocumentExtractor extractor = new HolidayDocumentExtractor();
      result.getDocuments().addAll(namedJdbc.query(sql[0], args, extractor));
    }
    return result;
  }

  /**
   * Gets the SQL to search for holidays.
   * @param request  the request, not null
   * @return the SQL search and count, not null
   */
  protected String[] sqlSearchHolidays(final HolidaySearchRequest request) {
    String where = "WHERE ver_from_instant <= :version_as_of_instant AND ver_to_instant > :version_as_of_instant " +
                "AND corr_from_instant <= :corrected_to_instant AND corr_to_instant > :corrected_to_instant ";
    if (request.getName() != null) {
      where += getDbHelper().sqlWildcardQuery("AND UPPER(name) ", "UPPER(:name)", request.getName());
    }
    if (request.getType() != null) {
      where += "AND hol_type = :hol_type ";
    }
    if (request.getRegionIdentifiers() != null) {
      where += "AND (" + sqlSelectIdKeys(request.getRegionIdentifiers(), "region") + ") ";
    }
    if (request.getRegionIdentifiers() != null) {
      where += "AND (" + sqlSelectIdKeys(request.getExchangeIdentifiers(), "exchange") + ") ";
    }
    if (request.getCurrency() != null) {
      where += "AND currency_iso = :currency_iso ";
    }
    String selectFromWhereInner = "SELECT id FROM hol_holiday " + where;
    String inner = getDbHelper().sqlApplyPaging(selectFromWhereInner, "ORDER BY id ", request.getPagingRequest());
    String search = SELECT + FROM + "WHERE h.id IN (" + inner + ") ORDER BY h.id, d.hol_date ";
    String count = "SELECT COUNT(*) FROM hol_holiday " + where;
    return new String[] {search, count};
  }

  /**
   * Gets the SQL to search for.
   * @param bundle  the bundle, not null
   * @param type  the type to search for, not null
   * @return the SQL search and count, not null
   */
  protected String sqlSelectIdKeys(final IdentifierBundle bundle, final String type) {
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < bundle.size(); i++) {
      list.add("(" + type + "_scheme = :" + type + "_scheme" + i + " AND " + type + "_value = :" + type + "_value" + i + ") ");
    }
    return StringUtils.join(list, "OR ");
  }

  //-------------------------------------------------------------------------
  @Override
  protected HolidaySearchHistoricResult searchHistoric(final HolidaySearchHistoricRequest request) {
    s_logger.debug("searchHolidayHistoric: {}", request);
    final DbMapSqlParameterSource args = new DbMapSqlParameterSource()
      .addValue("holiday_oid", extractOid(request.getHolidayId()))
      .addTimestampNullIgnored("versions_from_instant", request.getVersionsFromInstant())
      .addTimestampNullIgnored("versions_to_instant", request.getVersionsToInstant())
      .addTimestampNullIgnored("corrections_from_instant", request.getCorrectionsFromInstant())
      .addTimestampNullIgnored("corrections_to_instant", request.getCorrectionsToInstant());
    final String[] sql = sqlSearchHolidayHistoric(request);
    final NamedParameterJdbcOperations namedJdbc = getJdbcTemplate().getNamedParameterJdbcOperations();
    final int count = namedJdbc.queryForInt(sql[1], args);
    final HolidaySearchHistoricResult result = new HolidaySearchHistoricResult();
    result.setPaging(new Paging(request.getPagingRequest(), count));
    if (count > 0) {
      final HolidayDocumentExtractor extractor = new HolidayDocumentExtractor();
      result.getDocuments().addAll((List<HolidayDocument>) namedJdbc.query(sql[0], args, extractor));
    }
    return result;
  }

  /**
   * Gets the SQL for searching the history of a holiday.
   * @param request  the request, not null
   * @return the SQL search and count, not null
   */
  protected String[] sqlSearchHolidayHistoric(final HolidaySearchHistoricRequest request) {
    String where = "WHERE oid = :holiday_oid ";
    if (request.getVersionsFromInstant() != null && request.getVersionsFromInstant().equals(request.getVersionsToInstant())) {
      where += "AND ver_from_instant <= :versions_from_instant AND ver_to_instant > :versions_from_instant ";
    } else {
      if (request.getVersionsFromInstant() != null) {
        where += "AND ((ver_from_instant <= :versions_from_instant AND ver_to_instant > :versions_from_instant) " +
                            "OR ver_from_instant >= :versions_from_instant) ";
      }
      if (request.getVersionsToInstant() != null) {
        where += "AND ((ver_from_instant <= :versions_to_instant AND ver_to_instant > :versions_to_instant) " +
                            "OR ver_to_instant < :versions_to_instant) ";
      }
    }
    if (request.getCorrectionsFromInstant() != null && request.getCorrectionsFromInstant().equals(request.getCorrectionsToInstant())) {
      where += "AND corr_from_instant <= :corrections_from_instant AND corr_to_instant > :corrections_from_instant ";
    } else {
      if (request.getCorrectionsFromInstant() != null) {
        where += "AND ((corr_from_instant <= :corrections_from_instant AND corr_to_instant > :corrections_from_instant) " +
                            "OR corr_from_instant >= :corrections_from_instant) ";
      }
      if (request.getCorrectionsToInstant() != null) {
        where += "AND ((corr_from_instant <= :corrections_to_instant AND ver_to_instant > :corrections_to_instant) " +
                            "OR corr_to_instant < :corrections_to_instant) ";
      }
    }
    String selectFromWhereInner = "SELECT id FROM hol_holiday " + where;
    String inner = getDbHelper().sqlApplyPaging(selectFromWhereInner, "ORDER BY ver_from_instant DESC, corr_from_instant DESC ", request.getPagingRequest());
    String search = SELECT + FROM + "WHERE h.id IN (" + inner + ") ORDER BY h.ver_from_instant DESC, h.corr_from_instant DESC, d.hol_date ";
    String count = "SELECT COUNT(*) FROM hol_holiday " + where;
    return new String[] {search, count};
  }

  //-------------------------------------------------------------------------
  /**
   * Mapper from SQL rows to a HolidayDocument.
   */
  protected final class HolidayDocumentExtractor implements ResultSetExtractor<List<HolidayDocument>> {
    private long _lastHolidayId = -1;
    private ManageableHoliday _holiday;
    private List<HolidayDocument> _documents = new ArrayList<HolidayDocument>();

    @Override
    public List<HolidayDocument> extractData(final ResultSet rs) throws SQLException, DataAccessException {
      while (rs.next()) {
        final long holidayId = rs.getLong("HOLIDAY_ID");
        if (_lastHolidayId != holidayId) {
          _lastHolidayId = holidayId;
          buildHoliday(rs, holidayId);
        }
        final LocalDate holDate = DbDateUtils.fromSqlDate(rs.getDate("HOL_DATE"));
        _holiday.getHolidayDates().add(holDate);
      }
      return _documents;
    }

    private void buildHoliday(final ResultSet rs, final long holidayId) throws SQLException {
      final long holidayOid = rs.getLong("HOLIDAY_OID");
      final Timestamp versionFrom = rs.getTimestamp("VER_FROM_INSTANT");
      final Timestamp versionTo = rs.getTimestamp("VER_TO_INSTANT");
      final Timestamp correctionFrom = rs.getTimestamp("CORR_FROM_INSTANT");
      final Timestamp correctionTo = rs.getTimestamp("CORR_TO_INSTANT");
      final String name = rs.getString("NAME");
      final String type = rs.getString("HOL_TYPE");
      final String regionScheme = rs.getString("REGION_SCHEME");
      final String regionValue = rs.getString("REGION_VALUE");
      final String exchangeScheme = rs.getString("EXCHANGE_SCHEME");
      final String exchangeValue = rs.getString("EXCHANGE_VALUE");
      final String currencyISO = rs.getString("CURRENCY_ISO");
      UniqueIdentifier uid = createUniqueIdentifier(holidayOid, holidayId);
      ManageableHoliday holiday = new ManageableHoliday();
      holiday.setUniqueIdentifier(uid);
      holiday.setType(HolidayType.valueOf(type));
      if (regionScheme != null && regionValue != null) {
        holiday.setRegionId(Identifier.of(regionScheme, regionValue));
      }
      if (exchangeScheme != null && exchangeValue != null) {
        holiday.setExchangeId(Identifier.of(exchangeScheme, exchangeValue));
      }
      holiday.setCurrencyISO(currencyISO);
      HolidayDocument doc = new HolidayDocument(holiday);
      doc.setVersionFromInstant(DbDateUtils.fromSqlTimestamp(versionFrom));
      doc.setVersionToInstant(DbDateUtils.fromSqlTimestampNullFarFuture(versionTo));
      doc.setCorrectionFromInstant(DbDateUtils.fromSqlTimestamp(correctionFrom));
      doc.setCorrectionToInstant(DbDateUtils.fromSqlTimestampNullFarFuture(correctionTo));
      doc.setHolidayId(uid);
      doc.setName(name);
      _holiday = doc.getHoliday();
      _documents.add(doc);
    }
  }

}