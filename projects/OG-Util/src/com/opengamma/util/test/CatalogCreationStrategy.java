/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

/**
 * Strategy handling the creation of a database catalog.
 * <p>
 * This allows databases to be created on the fly.
 */
public interface CatalogCreationStrategy {

  /**
   * Checks if the database catalog exists already.
   * 
   * @param catalog  the catalog name, not null
   * @return true if it exists
   */
  boolean catalogExists(String catalog);

  /**
   * Creates a database catalog.
   * <p>
   * If the catalog already exists, does nothing.
   * 
   * @param catalog  the name of the catalog to create, not null
   */
  void create(String catalog);

}