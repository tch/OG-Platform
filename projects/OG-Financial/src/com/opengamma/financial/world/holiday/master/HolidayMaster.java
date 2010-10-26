/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.world.holiday.master;

import com.opengamma.DataNotFoundException;
import com.opengamma.id.UniqueIdentifier;

/**
 * A general-purpose holiday master.
 * <p>
 * The holiday master provides a uniform view over a set of holiday definitions.
 * This interface provides methods that allow the master to be searched and updated.
 */
public interface HolidayMaster {

  /**
   * Searches for holidays matching the specified search criteria.
   * 
   * @param request  the search request, not null
   * @return the search result, not null
   * @throws IllegalArgumentException if the request is invalid
   */
  HolidaySearchResult search(HolidaySearchRequest request);

  /**
   * Gets a holiday by unique identifier.
   * <p>
   * A full holiday master will store detailed historic information on holidays,
   * including a full version history.
   * The version in the identifier allows access to these historic versions.
   * 
   * @param uid  the unique identifier, not null
   * @return the holiday document, not null
   * @throws IllegalArgumentException if the request is invalid
   * @throws DataNotFoundException if there is no holiday with that unique identifier
   */
  HolidayDocument get(UniqueIdentifier uid);

  /**
   * Adds an holiday to the data store.
   * <p>
   * The specified document must contain the holiday.
   * 
   * @param document  the document, not null
   * @return the updated holiday document, not null
   * @throws IllegalArgumentException if the request is invalid
   * @throws DataNotFoundException if there is no parent node with the specified identifier
   */
  HolidayDocument add(HolidayDocument document);

  /**
   * Updates an holiday in the data store.
   * <p>
   * The specified document must contain the holiday and the unique identifier.
   * If the identifier has a version it must be the latest version.
   * <p>
   * A full holiday master will store detailed historic information on holidays,
   * including a full version history.
   * Older versions can be accessed using a versioned identifier or {@link #searchHistoric}.
   * 
   * @param document  the document, not null
   * @return the updated holiday document, not null
   * @throws IllegalArgumentException if the request is invalid
   * @throws DataNotFoundException if there is no holiday with that unique identifier
   */
  HolidayDocument update(HolidayDocument document);

  /**
   * Removes an holiday from the data store.
   * <p>
   * A full holiday master will store detailed historic information on holidays.
   * Thus, a removal does not prevent retrieval or correction of an earlier version.
   * <p>
   * If the identifier has a version it must be the latest version.
   * 
   * @param uid  the holiday unique identifier to remove, not null
   * @throws IllegalArgumentException if the request is invalid
   * @throws DataNotFoundException if there is no holiday with that unique identifier
   */
  void remove(final UniqueIdentifier uid);

  /**
   * Searches for holidays matching the specified search criteria.
   * <p>
   * The request must contain an object identifier that must not have a version.
   * 
   * @param request  the search request, not null
   * @return the search result, not null
   * @throws IllegalArgumentException if the request is invalid
   */
  HolidaySearchHistoricResult searchHistoric(HolidaySearchHistoricRequest request);

  /**
   * Corrects an holiday in the data store.
   * <p>
   * A full holiday master will store detailed historic information on holidays
   * and will support correction of each holiday.
   * To update the holiday with a new version, use {@link #update}.
   * To correct a previously stored version, use this method.
   * Older versions and corrections can be accessed using a versioned identifier or {@link #searchHistoric}.
   * <p>
   * The specified document must contain the holiday and the unique identifier.
   * The unique identifier must specify the last correction of a specific version of the holiday.
   * 
   * @param document  the document, not null
   * @return the updated holiday document, not null
   * @throws IllegalArgumentException if the request is invalid
   * @throws DataNotFoundException if there is no holiday with that unique identifier
   */
  HolidayDocument correct(HolidayDocument document);

}