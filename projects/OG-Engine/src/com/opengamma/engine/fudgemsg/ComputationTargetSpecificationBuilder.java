/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.fudgemsg;

import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.fudgemsg.mapping.FudgeBuilder;
import org.fudgemsg.mapping.FudgeBuilderFor;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;

import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.id.UniqueIdentifier;

/**
 * 
 */
@FudgeBuilderFor(ComputationTargetSpecification.class)
public class ComputationTargetSpecificationBuilder implements FudgeBuilder<ComputationTargetSpecification> {
  /**
   * Fudge field name.
   */
  private static final String TYPE_FIELD_NAME = "computationTargetType";
  /**
   * Fudge field name.
   */
  private static final String IDENTIFIER_FIELD_NAME = "computationTargetIdentifier";

  @Override
  public MutableFudgeFieldContainer buildMessage(FudgeSerializationContext context, ComputationTargetSpecification object) {
    MutableFudgeFieldContainer msg = context.newMessage();
    msg.add(TYPE_FIELD_NAME, object.getType().name());
    UniqueIdentifier uid = object.getUniqueIdentifier();
    if (uid != null) {
      context.objectToFudgeMsg(msg, IDENTIFIER_FIELD_NAME, null, uid);
    }
    return msg;
  }

  @Override
  public ComputationTargetSpecification buildObject(FudgeDeserializationContext context, FudgeFieldContainer message) {
    final ComputationTargetType type = ComputationTargetType.valueOf(message.getString(TYPE_FIELD_NAME));
    UniqueIdentifier uid = null;
    if (message.hasField(IDENTIFIER_FIELD_NAME)) {
      FudgeField fudgeField = message.getByName(IDENTIFIER_FIELD_NAME);
      uid = context.fieldValueToObject(UniqueIdentifier.class, fudgeField);
    }
    return new ComputationTargetSpecification(type, uid);
  }

}