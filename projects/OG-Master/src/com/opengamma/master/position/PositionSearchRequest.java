/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.master.position;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.AbstractSearchRequest;

/**
 * Request for searching for positions.
 * <p>
 * Documents will be returned that match the search criteria.
 * This class provides the ability to page the results and to search
 * as at a specific version and correction instant.
 * See {@link PositionHistoryRequest} for more details on how history works.
 */
@BeanDefinition
public class PositionSearchRequest extends AbstractSearchRequest {

  /**
   * The portfolio object identifier, null to search all portfolios.
   */
  @PropertyDefinition
  private UniqueIdentifier _portfolioId;
  /**
   * The node to search within, null to search all nodes.
   */
  @PropertyDefinition
  private UniqueIdentifier _parentNodeId;
  /**
   * The minimum quantity, inclusive, null for no minimum.
   */
  @PropertyDefinition
  private BigDecimal _minQuantity;
  /**
   * The maximum quantity, exclusive, null for no maximum.
   */
  @PropertyDefinition
  private BigDecimal _maxQuantity;
  /**
   * The security key to match, null to not match on security key.
   */
  @PropertyDefinition
  private IdentifierBundle _securityKey;

  /**
   * Creates an instance.
   */
  public PositionSearchRequest() {
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code PositionSearchRequest}.
   * @return the meta-bean, not null
   */
  public static PositionSearchRequest.Meta meta() {
    return PositionSearchRequest.Meta.INSTANCE;
  }

  @Override
  public PositionSearchRequest.Meta metaBean() {
    return PositionSearchRequest.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -5186429:  // portfolioId
        return getPortfolioId();
      case 915246087:  // parentNodeId
        return getParentNodeId();
      case 69860605:  // minQuantity
        return getMinQuantity();
      case 747293199:  // maxQuantity
        return getMaxQuantity();
      case 1550083839:  // securityKey
        return getSecurityKey();
    }
    return super.propertyGet(propertyName);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -5186429:  // portfolioId
        setPortfolioId((UniqueIdentifier) newValue);
        return;
      case 915246087:  // parentNodeId
        setParentNodeId((UniqueIdentifier) newValue);
        return;
      case 69860605:  // minQuantity
        setMinQuantity((BigDecimal) newValue);
        return;
      case 747293199:  // maxQuantity
        setMaxQuantity((BigDecimal) newValue);
        return;
      case 1550083839:  // securityKey
        setSecurityKey((IdentifierBundle) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the portfolio object identifier, null to search all portfolios.
   * @return the value of the property
   */
  public UniqueIdentifier getPortfolioId() {
    return _portfolioId;
  }

  /**
   * Sets the portfolio object identifier, null to search all portfolios.
   * @param portfolioId  the new value of the property
   */
  public void setPortfolioId(UniqueIdentifier portfolioId) {
    this._portfolioId = portfolioId;
  }

  /**
   * Gets the the {@code portfolioId} property.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> portfolioId() {
    return metaBean().portfolioId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the node to search within, null to search all nodes.
   * @return the value of the property
   */
  public UniqueIdentifier getParentNodeId() {
    return _parentNodeId;
  }

  /**
   * Sets the node to search within, null to search all nodes.
   * @param parentNodeId  the new value of the property
   */
  public void setParentNodeId(UniqueIdentifier parentNodeId) {
    this._parentNodeId = parentNodeId;
  }

  /**
   * Gets the the {@code parentNodeId} property.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> parentNodeId() {
    return metaBean().parentNodeId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the minimum quantity, inclusive, null for no minimum.
   * @return the value of the property
   */
  public BigDecimal getMinQuantity() {
    return _minQuantity;
  }

  /**
   * Sets the minimum quantity, inclusive, null for no minimum.
   * @param minQuantity  the new value of the property
   */
  public void setMinQuantity(BigDecimal minQuantity) {
    this._minQuantity = minQuantity;
  }

  /**
   * Gets the the {@code minQuantity} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> minQuantity() {
    return metaBean().minQuantity().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the maximum quantity, exclusive, null for no maximum.
   * @return the value of the property
   */
  public BigDecimal getMaxQuantity() {
    return _maxQuantity;
  }

  /**
   * Sets the maximum quantity, exclusive, null for no maximum.
   * @param maxQuantity  the new value of the property
   */
  public void setMaxQuantity(BigDecimal maxQuantity) {
    this._maxQuantity = maxQuantity;
  }

  /**
   * Gets the the {@code maxQuantity} property.
   * @return the property, not null
   */
  public final Property<BigDecimal> maxQuantity() {
    return metaBean().maxQuantity().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the security key to match, null to not match on security key.
   * @return the value of the property
   */
  public IdentifierBundle getSecurityKey() {
    return _securityKey;
  }

  /**
   * Sets the security key to match, null to not match on security key.
   * @param securityKey  the new value of the property
   */
  public void setSecurityKey(IdentifierBundle securityKey) {
    this._securityKey = securityKey;
  }

  /**
   * Gets the the {@code securityKey} property.
   * @return the property, not null
   */
  public final Property<IdentifierBundle> securityKey() {
    return metaBean().securityKey().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code PositionSearchRequest}.
   */
  public static class Meta extends AbstractSearchRequest.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code portfolioId} property.
     */
    private final MetaProperty<UniqueIdentifier> _portfolioId = DirectMetaProperty.ofReadWrite(this, "portfolioId", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code parentNodeId} property.
     */
    private final MetaProperty<UniqueIdentifier> _parentNodeId = DirectMetaProperty.ofReadWrite(this, "parentNodeId", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code minQuantity} property.
     */
    private final MetaProperty<BigDecimal> _minQuantity = DirectMetaProperty.ofReadWrite(this, "minQuantity", BigDecimal.class);
    /**
     * The meta-property for the {@code maxQuantity} property.
     */
    private final MetaProperty<BigDecimal> _maxQuantity = DirectMetaProperty.ofReadWrite(this, "maxQuantity", BigDecimal.class);
    /**
     * The meta-property for the {@code securityKey} property.
     */
    private final MetaProperty<IdentifierBundle> _securityKey = DirectMetaProperty.ofReadWrite(this, "securityKey", IdentifierBundle.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap(super.metaPropertyMap());
      temp.put("portfolioId", _portfolioId);
      temp.put("parentNodeId", _parentNodeId);
      temp.put("minQuantity", _minQuantity);
      temp.put("maxQuantity", _maxQuantity);
      temp.put("securityKey", _securityKey);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public PositionSearchRequest createBean() {
      return new PositionSearchRequest();
    }

    @Override
    public Class<? extends PositionSearchRequest> beanType() {
      return PositionSearchRequest.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code portfolioId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> portfolioId() {
      return _portfolioId;
    }

    /**
     * The meta-property for the {@code parentNodeId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> parentNodeId() {
      return _parentNodeId;
    }

    /**
     * The meta-property for the {@code minQuantity} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> minQuantity() {
      return _minQuantity;
    }

    /**
     * The meta-property for the {@code maxQuantity} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> maxQuantity() {
      return _maxQuantity;
    }

    /**
     * The meta-property for the {@code securityKey} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IdentifierBundle> securityKey() {
      return _securityKey;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}