/**
 * Copyright (C) 2009 - 2009 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.livedata.msg.UserPrincipal;
import com.opengamma.util.ArgumentChecker;

/**
 * The encapsulated logic that controls how precisely a view is to be constructed
 * and computed.
 */
public class ViewDefinition implements Serializable {
  private final String _name;
  private final UniqueIdentifier _portfolioId;
  private final UserPrincipal _liveDataUser;
  
  /** 
   * View cannot be recalculated more frequently than this.
   * Milliseconds.
   * Null = view can be recalculated as frequently as you want.
   */
  private Long _minimumRecalculationPeriod;
  
  /** 
   * A full recomputation of the view should be performed at this interval 
   * (i.e., no delta vs. previous result should be used).
   * Milliseconds.
   * Null = no full recomputation needs to be performed - previous result can always be used
   */ 
  private Long _fullRecalculationPeriod;
  
  private final Map<String, ViewCalculationConfiguration> _calculationConfigurationsByName =
    new TreeMap<String, ViewCalculationConfiguration>();
  
  public ViewDefinition(String name, UniqueIdentifier portfolioId, String userName) {
    ArgumentChecker.notNull(name, "View name");
    ArgumentChecker.notNull(portfolioId, "Portfolio id");
    ArgumentChecker.notNull(userName, "User name");
    
    _name = name;
    _portfolioId = portfolioId;
    _liveDataUser = UserPrincipal.getLocalUser(userName);
  }
  
  public ViewDefinition(String name, UniqueIdentifier portfolioId, UserPrincipal liveDataUser) {
    ArgumentChecker.notNull(name, "View name");
    ArgumentChecker.notNull(portfolioId, "Portfolio id");
    ArgumentChecker.notNull(liveDataUser, "User name");
    
    _name = name;
    _portfolioId = portfolioId;
    _liveDataUser = liveDataUser;
  }
  
  public Set<String> getAllValueRequirements() {
    Set<String> requirements = new TreeSet<String>();
    for (ViewCalculationConfiguration calcConfig : _calculationConfigurationsByName.values()) {
      requirements.addAll(calcConfig.getAllValueRequirements());
    }
    return requirements;
  }

  public String getName() {
    return _name;
  }

  public UniqueIdentifier getPortfolioId() {
    return _portfolioId;
  }
  
  /**
   * @return The LiveData user should be used to create 
   * LiveData subscriptions. It is thus a kind of 'super-user'
   * and ensures that the View can be materialized even without
   * any end user trying to use it.
   * <p>
   * Authenticating the end users of the View (of which there can be many) 
   * is a separate matter entirely and has nothing to do with this user.  
   */
  public UserPrincipal getLiveDataUser() {
    return _liveDataUser;
  }
  
  public Collection<ViewCalculationConfiguration> getAllCalculationConfigurations() {
    return new ArrayList<ViewCalculationConfiguration>(_calculationConfigurationsByName.values());
  }
  
  public Set<String> getAllCalculationConfigurationNames() {
    return Collections.unmodifiableSet(_calculationConfigurationsByName.keySet());
  }
  
  public Map<String, ViewCalculationConfiguration> getAllCalculationConfigurationsByName() {
    return Collections.unmodifiableMap(_calculationConfigurationsByName);
  }
  
  public ViewCalculationConfiguration getCalculationConfiguration(String configurationName) {
    return _calculationConfigurationsByName.get(configurationName);
  }
  
  public void addViewCalculationConfiguration(ViewCalculationConfiguration calcConfig) {
    ArgumentChecker.notNull(calcConfig, "calculation configuration");
    ArgumentChecker.notNull(calcConfig.getName(), "Configuration name");
    _calculationConfigurationsByName.put(calcConfig.getName(), calcConfig);
  }
  
  public void addValueDefinition(String calculationConfigurationName, String securityType, String requirementName) {
    ViewCalculationConfiguration calcConfig = _calculationConfigurationsByName.get(calculationConfigurationName);
    if (calcConfig == null) {
      calcConfig = new ViewCalculationConfiguration(this, calculationConfigurationName);
      _calculationConfigurationsByName.put(calculationConfigurationName, calcConfig);
    }
    calcConfig.addValueRequirement(securityType, requirementName);
  }

  /**
   * @return Minimum recalculation period, milliseconds
   */
  public Long getMinimumRecalculationPeriod() {
    return _minimumRecalculationPeriod;
  }

  /**
   * @param minimumRecalculationPeriod the minimumRecalculationPeriod to set, milliseconds
   */
  public void setMinimumRecalculationPeriod(Long minimumRecalculationPeriod) {
    _minimumRecalculationPeriod = minimumRecalculationPeriod;
  }

  /**
   * @return Full recalculation period, milliseconds
   */
  public Long getFullRecalculationPeriod() {
    return _fullRecalculationPeriod;
  }

  /**
   * @param fullRecalculationPeriod the fullRecalculationPeriod to set, milliseconds
   */
  public void setFullRecalculationPeriod(Long fullRecalculationPeriod) {
    _fullRecalculationPeriod = fullRecalculationPeriod;
  }
  

}
