/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.master.position;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.time.calendar.LocalDate;
import javax.time.calendar.OffsetTime;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.opengamma.id.Identifier;
import com.opengamma.id.IdentifierBundle;
import com.opengamma.id.MutableUniqueIdentifiable;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * A trade forming part of a position.
 * <p>
 * A position is formed of one or more trades.
 * For example a trade of 200 shares of OpenGamma might be combined with another trade
 * of 450 shares of OpenGamma to create a combined position of 650 shares.
 */
@PublicSPI
@BeanDefinition
public class ManageableTrade extends DirectBean implements MutableUniqueIdentifiable {

  /**
   * The trade unique identifier.
   * This field should be null until added to the master.
   */
  @PropertyDefinition
  private UniqueIdentifier _uniqueId;
  /**
   * The parent position unique identifier.
   * This field is managed by the master.
   */
  @PropertyDefinition
  private UniqueIdentifier _positionId;
  /**
   * The quantity.
   * This field must not be null for the object to be valid.
   */
  @PropertyDefinition
  private BigDecimal _quantity;
  /**
   * The identifiers specifying the security.
   * This field must not be null for the object to be valid.
   */
  @PropertyDefinition
  private IdentifierBundle _securityKey;
  /**
   * The trade date.
   * This field must not be null for the object to be valid.
   */
  @PropertyDefinition
  private LocalDate _tradeDate;
  /**
   * The trade time with offset, null if not known.
   */
  @PropertyDefinition
  private OffsetTime _tradeTime;
  /**
   * The counterparty key identifier, null if not known.
   */
  @PropertyDefinition
  private Identifier _counterpartyKey;
  /**
   * The provider key identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   */
  @PropertyDefinition
  private Identifier _providerKey;

  /**
   * Creates an instance.
   */
  public ManageableTrade() {
  }

  /**
   * Creates a trade from trade quantity, instant and counterparty identifier.
   * 
   * @param quantity  the amount of the trade, not null
   * @param securityKey  the security identifier, not null
   * @param tradeDate  the trade date, not null
   * @param tradeTime the trade time with timezone, may be null
   * @param counterpartyId the counterparty identifier, not null
   */
  public ManageableTrade(final BigDecimal quantity, final Identifier securityKey, final LocalDate tradeDate, final OffsetTime tradeTime, final Identifier counterpartyId) {
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(tradeDate, "tradeDate");
    ArgumentChecker.notNull(counterpartyId, "counterpartyId");
    ArgumentChecker.notNull(securityKey, "securityKey");
    _quantity = quantity;
    _tradeDate = tradeDate;
    _tradeTime = tradeTime;
    _counterpartyKey = counterpartyId;
    _securityKey = IdentifierBundle.of(securityKey);
  }

  /**
   * Creates a trade from trade quantity, instant and counterparty identifier.
   * 
   * @param quantity  the amount of the trade, not null
   * @param securityKey  the security identifier, not null
   * @param tradeDate  the trade date, not null
   * @param tradeTime the trade time with timezone, may be null
   * @param counterpartyId the counterparty identifier, not null
   */
  public ManageableTrade(final BigDecimal quantity, final IdentifierBundle securityKey, final LocalDate tradeDate, final OffsetTime tradeTime, final Identifier counterpartyId) {
    ArgumentChecker.notNull(quantity, "quantity");
    ArgumentChecker.notNull(securityKey, "securityKey");
    ArgumentChecker.notNull(tradeDate, "tradeDate");
    ArgumentChecker.notNull(counterpartyId, "counterpartyId");
    _quantity = quantity;
    _tradeDate = tradeDate;
    _tradeTime = tradeTime;
    _counterpartyKey = counterpartyId;
    _securityKey = securityKey;
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an identifier to the security key.
   * @param securityKeyIdentifier  the identifier to add, not null
   */
  public void addSecurityKey(final Identifier securityKeyIdentifier) {
    ArgumentChecker.notNull(securityKeyIdentifier, "securityKeyIdentifier");
    setSecurityKey(getSecurityKey().withIdentifier(securityKeyIdentifier));
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ManageableTrade}.
   * @return the meta-bean, not null
   */
  public static ManageableTrade.Meta meta() {
    return ManageableTrade.Meta.INSTANCE;
  }

  @Override
  public ManageableTrade.Meta metaBean() {
    return ManageableTrade.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        return getUniqueId();
      case 1381039140:  // positionId
        return getPositionId();
      case -1285004149:  // quantity
        return getQuantity();
      case 1550083839:  // securityKey
        return getSecurityKey();
      case 752419634:  // tradeDate
        return getTradeDate();
      case 752903761:  // tradeTime
        return getTradeTime();
      case 624096149:  // counterpartyKey
        return getCounterpartyKey();
      case 2064682670:  // providerKey
        return getProviderKey();
    }
    return super.propertyGet(propertyName);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case -294460212:  // uniqueId
        setUniqueId((UniqueIdentifier) newValue);
        return;
      case 1381039140:  // positionId
        setPositionId((UniqueIdentifier) newValue);
        return;
      case -1285004149:  // quantity
        setQuantity((BigDecimal) newValue);
        return;
      case 1550083839:  // securityKey
        setSecurityKey((IdentifierBundle) newValue);
        return;
      case 752419634:  // tradeDate
        setTradeDate((LocalDate) newValue);
        return;
      case 752903761:  // tradeTime
        setTradeTime((OffsetTime) newValue);
        return;
      case 624096149:  // counterpartyKey
        setCounterpartyKey((Identifier) newValue);
        return;
      case 2064682670:  // providerKey
        setProviderKey((Identifier) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trade unique identifier.
   * This field should be null until added to the master.
   * @return the value of the property
   */
  public UniqueIdentifier getUniqueId() {
    return _uniqueId;
  }

  /**
   * Sets the trade unique identifier.
   * This field should be null until added to the master.
   * @param uniqueId  the new value of the property
   */
  public void setUniqueId(UniqueIdentifier uniqueId) {
    this._uniqueId = uniqueId;
  }

  /**
   * Gets the the {@code uniqueId} property.
   * This field should be null until added to the master.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> uniqueId() {
    return metaBean().uniqueId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the parent position unique identifier.
   * This field is managed by the master.
   * @return the value of the property
   */
  public UniqueIdentifier getPositionId() {
    return _positionId;
  }

  /**
   * Sets the parent position unique identifier.
   * This field is managed by the master.
   * @param positionId  the new value of the property
   */
  public void setPositionId(UniqueIdentifier positionId) {
    this._positionId = positionId;
  }

  /**
   * Gets the the {@code positionId} property.
   * This field is managed by the master.
   * @return the property, not null
   */
  public final Property<UniqueIdentifier> positionId() {
    return metaBean().positionId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the quantity.
   * This field must not be null for the object to be valid.
   * @return the value of the property
   */
  public BigDecimal getQuantity() {
    return _quantity;
  }

  /**
   * Sets the quantity.
   * This field must not be null for the object to be valid.
   * @param quantity  the new value of the property
   */
  public void setQuantity(BigDecimal quantity) {
    this._quantity = quantity;
  }

  /**
   * Gets the the {@code quantity} property.
   * This field must not be null for the object to be valid.
   * @return the property, not null
   */
  public final Property<BigDecimal> quantity() {
    return metaBean().quantity().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifiers specifying the security.
   * This field must not be null for the object to be valid.
   * @return the value of the property
   */
  public IdentifierBundle getSecurityKey() {
    return _securityKey;
  }

  /**
   * Sets the identifiers specifying the security.
   * This field must not be null for the object to be valid.
   * @param securityKey  the new value of the property
   */
  public void setSecurityKey(IdentifierBundle securityKey) {
    this._securityKey = securityKey;
  }

  /**
   * Gets the the {@code securityKey} property.
   * This field must not be null for the object to be valid.
   * @return the property, not null
   */
  public final Property<IdentifierBundle> securityKey() {
    return metaBean().securityKey().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trade date.
   * This field must not be null for the object to be valid.
   * @return the value of the property
   */
  public LocalDate getTradeDate() {
    return _tradeDate;
  }

  /**
   * Sets the trade date.
   * This field must not be null for the object to be valid.
   * @param tradeDate  the new value of the property
   */
  public void setTradeDate(LocalDate tradeDate) {
    this._tradeDate = tradeDate;
  }

  /**
   * Gets the the {@code tradeDate} property.
   * This field must not be null for the object to be valid.
   * @return the property, not null
   */
  public final Property<LocalDate> tradeDate() {
    return metaBean().tradeDate().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the trade time with offset, null if not known.
   * @return the value of the property
   */
  public OffsetTime getTradeTime() {
    return _tradeTime;
  }

  /**
   * Sets the trade time with offset, null if not known.
   * @param tradeTime  the new value of the property
   */
  public void setTradeTime(OffsetTime tradeTime) {
    this._tradeTime = tradeTime;
  }

  /**
   * Gets the the {@code tradeTime} property.
   * @return the property, not null
   */
  public final Property<OffsetTime> tradeTime() {
    return metaBean().tradeTime().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the counterparty key identifier, null if not known.
   * @return the value of the property
   */
  public Identifier getCounterpartyKey() {
    return _counterpartyKey;
  }

  /**
   * Sets the counterparty key identifier, null if not known.
   * @param counterpartyKey  the new value of the property
   */
  public void setCounterpartyKey(Identifier counterpartyKey) {
    this._counterpartyKey = counterpartyKey;
  }

  /**
   * Gets the the {@code counterpartyKey} property.
   * @return the property, not null
   */
  public final Property<Identifier> counterpartyKey() {
    return metaBean().counterpartyKey().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the provider key identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the value of the property
   */
  public Identifier getProviderKey() {
    return _providerKey;
  }

  /**
   * Sets the provider key identifier for the data.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @param providerKey  the new value of the property
   */
  public void setProviderKey(Identifier providerKey) {
    this._providerKey = providerKey;
  }

  /**
   * Gets the the {@code providerKey} property.
   * This optional field can be used to capture the identifier used by the data provider.
   * This can be useful when receiving updates from the same provider.
   * @return the property, not null
   */
  public final Property<Identifier> providerKey() {
    return metaBean().providerKey().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ManageableTrade}.
   */
  public static class Meta extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code uniqueId} property.
     */
    private final MetaProperty<UniqueIdentifier> _uniqueId = DirectMetaProperty.ofReadWrite(this, "uniqueId", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code positionId} property.
     */
    private final MetaProperty<UniqueIdentifier> _positionId = DirectMetaProperty.ofReadWrite(this, "positionId", UniqueIdentifier.class);
    /**
     * The meta-property for the {@code quantity} property.
     */
    private final MetaProperty<BigDecimal> _quantity = DirectMetaProperty.ofReadWrite(this, "quantity", BigDecimal.class);
    /**
     * The meta-property for the {@code securityKey} property.
     */
    private final MetaProperty<IdentifierBundle> _securityKey = DirectMetaProperty.ofReadWrite(this, "securityKey", IdentifierBundle.class);
    /**
     * The meta-property for the {@code tradeDate} property.
     */
    private final MetaProperty<LocalDate> _tradeDate = DirectMetaProperty.ofReadWrite(this, "tradeDate", LocalDate.class);
    /**
     * The meta-property for the {@code tradeTime} property.
     */
    private final MetaProperty<OffsetTime> _tradeTime = DirectMetaProperty.ofReadWrite(this, "tradeTime", OffsetTime.class);
    /**
     * The meta-property for the {@code counterpartyKey} property.
     */
    private final MetaProperty<Identifier> _counterpartyKey = DirectMetaProperty.ofReadWrite(this, "counterpartyKey", Identifier.class);
    /**
     * The meta-property for the {@code providerKey} property.
     */
    private final MetaProperty<Identifier> _providerKey = DirectMetaProperty.ofReadWrite(this, "providerKey", Identifier.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("uniqueId", _uniqueId);
      temp.put("positionId", _positionId);
      temp.put("quantity", _quantity);
      temp.put("securityKey", _securityKey);
      temp.put("tradeDate", _tradeDate);
      temp.put("tradeTime", _tradeTime);
      temp.put("counterpartyKey", _counterpartyKey);
      temp.put("providerKey", _providerKey);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public ManageableTrade createBean() {
      return new ManageableTrade();
    }

    @Override
    public Class<? extends ManageableTrade> beanType() {
      return ManageableTrade.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code uniqueId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> uniqueId() {
      return _uniqueId;
    }

    /**
     * The meta-property for the {@code positionId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UniqueIdentifier> positionId() {
      return _positionId;
    }

    /**
     * The meta-property for the {@code quantity} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BigDecimal> quantity() {
      return _quantity;
    }

    /**
     * The meta-property for the {@code securityKey} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<IdentifierBundle> securityKey() {
      return _securityKey;
    }

    /**
     * The meta-property for the {@code tradeDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LocalDate> tradeDate() {
      return _tradeDate;
    }

    /**
     * The meta-property for the {@code tradeTime} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<OffsetTime> tradeTime() {
      return _tradeTime;
    }

    /**
     * The meta-property for the {@code counterpartyKey} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Identifier> counterpartyKey() {
      return _counterpartyKey;
    }

    /**
     * The meta-property for the {@code providerKey} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Identifier> providerKey() {
      return _providerKey;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
