/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.resolver;

import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.FudgeMsgEnvelope;
import org.fudgemsg.mapping.FudgeDeserializationContext;
import org.fudgemsg.mapping.FudgeSerializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.id.Identifier;
import com.opengamma.livedata.LiveDataSpecification;
import com.opengamma.livedata.msg.ResolveRequest;
import com.opengamma.livedata.msg.ResolveResponse;
import com.opengamma.transport.FudgeRequestReceiver;
import com.opengamma.util.ArgumentChecker;

/**
 * Receives <code>ResolveRequests</code>, passes them onto a delegate <code>IdResolver</code>,
 * and returns <code>ResolveResponses</code>. 
 *
 * @author pietari
 */
public class IdResolverServer implements FudgeRequestReceiver {
  
  private static final Logger s_logger = LoggerFactory.getLogger(IdResolverServer.class);
  private final IdResolver _delegate;
  
  public IdResolverServer(IdResolver delegate) {
    ArgumentChecker.notNull(delegate, "Delegate specification resolver");
    _delegate = delegate;
  }
  
  @Override
  public FudgeFieldContainer requestReceived(FudgeDeserializationContext context, FudgeMsgEnvelope requestEnvelope) {
    FudgeFieldContainer requestFudgeMsg = requestEnvelope.getMessage();
    ResolveRequest resolveRequest = ResolveRequest.fromFudgeMsg(context, requestFudgeMsg);
    s_logger.debug("Received resolve request for {}", resolveRequest.getRequestedSpecification());
    
    LiveDataSpecification requestedSpec = resolveRequest.getRequestedSpecification();
    Identifier resolvedId = _delegate.resolve(requestedSpec.getIdentifiers());
    LiveDataSpecification resolvedSpec = new LiveDataSpecification(
        requestedSpec.getNormalizationRuleSetId(),
        resolvedId);
    
    ResolveResponse response = new ResolveResponse(resolvedSpec);
    FudgeFieldContainer responseFudgeMsg = response.toFudgeMsg(new FudgeSerializationContext(context.getFudgeContext()));
    return responseFudgeMsg;
  }
  
}