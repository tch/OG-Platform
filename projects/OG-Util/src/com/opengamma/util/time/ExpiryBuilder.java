/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.util.time;

import javax.time.calendar.TimeZone;
import javax.time.calendar.ZonedDateTime;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.FudgeMessageFactory;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;
import org.fudgemsg.types.DateTimeAccuracy;
import org.fudgemsg.types.FudgeDateTime;
import org.fudgemsg.types.FudgeMsgFieldType;
import org.fudgemsg.types.FudgeSecondaryType;
import org.fudgemsg.types.SecondaryFieldType;

import com.opengamma.OpenGammaRuntimeException;

/**
 * Expiry builder.
 */
@FudgeBuilderFor(Expiry.class)
public final class ExpiryBuilder implements FudgeBuilder<Expiry> {

  /**
   * Field name for the date & time component.
   */
  public static final String DATETIME_KEY = "datetime";

  /**
   * Field name for the timezone component.
   */
  public static final String TIMEZONE_KEY = "timezone";

  /**
   * Dummy secondary type to force serialization.
   */
  @FudgeSecondaryType
  public static final SecondaryFieldType<Expiry, FudgeFieldContainer> SECONDARY_TYPE_INSTANCE = new SecondaryFieldType<Expiry, FudgeFieldContainer>(FudgeMsgFieldType.INSTANCE, Expiry.class) {

    @Override
    public FudgeFieldContainer secondaryToPrimary(final Expiry object) {
      throw new OpenGammaRuntimeException("Expiry should be serialized, not added directly to a Fudge message");
    }

    @Override
    public Expiry primaryToSecondary(final FudgeFieldContainer message) {
      return fromFudgeMsg(message);
    }

  };

  protected static FudgeDateTime expiryToDateTime(final Expiry object) {
    ExpiryAccuracy accuracy = object.getAccuracy();
    if (accuracy == null) {
      accuracy = ExpiryAccuracy.DAY_MONTH_YEAR;
    }
    switch (accuracy) {
      case MIN_HOUR_DAY_MONTH_YEAR:
        return new FudgeDateTime(DateTimeAccuracy.MINUTE, object.getExpiry().toInstant());
      case HOUR_DAY_MONTH_YEAR:
        return new FudgeDateTime(DateTimeAccuracy.HOUR, object.getExpiry().toInstant());
      case DAY_MONTH_YEAR:
        return new FudgeDateTime(DateTimeAccuracy.DAY, object.getExpiry().toInstant());
      case MONTH_YEAR:
        return new FudgeDateTime(DateTimeAccuracy.MONTH, object.getExpiry().toInstant());
      case YEAR:
        return new FudgeDateTime(DateTimeAccuracy.YEAR, object.getExpiry().toInstant());
      default:
        throw new IllegalArgumentException("Invalid accuracy value on " + object);
    }
  }

  protected static Expiry dateTimeToExpiry(final FudgeDateTime datetime, final String timezone) {
    final ZonedDateTime zdt = ZonedDateTime.ofInstant(datetime.toInstant(), TimeZone.of(timezone));
    switch (datetime.getAccuracy()) {
      case MINUTE:
        return new Expiry(zdt, ExpiryAccuracy.MIN_HOUR_DAY_MONTH_YEAR);
      case HOUR:
        return new Expiry(zdt, ExpiryAccuracy.HOUR_DAY_MONTH_YEAR);
      case DAY:
        return new Expiry(zdt, ExpiryAccuracy.DAY_MONTH_YEAR);
      case MONTH:
        return new Expiry(zdt, ExpiryAccuracy.MONTH_YEAR);
      case YEAR:
        return new Expiry(zdt, ExpiryAccuracy.YEAR);
      default:
        throw new IllegalArgumentException("Invalid accuracy value on " + datetime);
    }
  }

  protected static MutableFudgeFieldContainer toFudgeMsg(final FudgeMessageFactory context, final Expiry expiry) {
    final MutableFudgeFieldContainer message = context.newMessage();
    toFudgeMsg(expiry, message);
    return message;
  }

  protected static void toFudgeMsg(final Expiry expiry, final MutableFudgeFieldContainer message) {
    message.add(DATETIME_KEY, expiryToDateTime(expiry));
    message.add(TIMEZONE_KEY, expiry.getExpiry().getZone().getID());
  }

  protected static Expiry fromFudgeMsg(final FudgeFieldContainer message) {
    return dateTimeToExpiry(message.getFieldValue(FudgeDateTime.class, message.getByName(DATETIME_KEY)), message.getFieldValue(String.class, message.getByName(TIMEZONE_KEY)));
  }

  @Override
  public MutableFudgeFieldContainer buildMessage(final FudgeSerializationContext context, final Expiry expiry) {
    return toFudgeMsg(context, expiry);
  }

  @Override
  public Expiry buildObject(final FudgeDeserializationContext context, final FudgeFieldContainer message) {
    return fromFudgeMsg(message);
  }

}