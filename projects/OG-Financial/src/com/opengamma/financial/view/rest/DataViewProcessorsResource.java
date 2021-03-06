/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.view.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.jms.ConnectionFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.fudgemsg.FudgeContext;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.engine.view.ViewProcessor;
import com.opengamma.id.UniqueIdentifier;

/**
 * RESTful back-end to provide access to view processors
 */
@Path("/data/viewProcessors")
public class DataViewProcessorsResource {
  
  private final Map<UniqueIdentifier, DataViewProcessorResource> _viewProcessorResourceMap = new HashMap<UniqueIdentifier, DataViewProcessorResource>();
  
  public DataViewProcessorsResource() {
  }

  public DataViewProcessorsResource(final ViewProcessor viewProcessor, final ConnectionFactory connectionFactory, FudgeContext fudgeContext,
      ScheduledExecutorService scheduler) {
    addViewProcessor(viewProcessor, connectionFactory, fudgeContext, scheduler);
  }

  public DataViewProcessorsResource(final Collection<ViewProcessor> viewProcessors,
      final ConnectionFactory connectionFactory, FudgeContext fudgeContext, ScheduledExecutorService scheduler) {
    for (ViewProcessor viewProcessor : viewProcessors) {
      addViewProcessor(viewProcessor, connectionFactory, fudgeContext, scheduler);
    }
  }

  //-------------------------------------------------------------------------
  public void addViewProcessor(ViewProcessor viewProcessor, ConnectionFactory connectionFactory,
      FudgeContext fudgeContext, ScheduledExecutorService scheduler) {
    if (_viewProcessorResourceMap.get(viewProcessor.getUniqueId()) != null) {
      throw new OpenGammaRuntimeException("A view processor with the ID " + viewProcessor.getUniqueId() + " is already being managed");
    }
    _viewProcessorResourceMap.put(viewProcessor.getUniqueId(),
        new DataViewProcessorResource(viewProcessor, connectionFactory, fudgeContext, scheduler));
  }
  
  //-------------------------------------------------------------------------
  
  @Path("{viewProcessorId}")
  public DataViewProcessorResource findViewProcessor(@PathParam("viewProcessorId") String viewProcessorId) {
    return _viewProcessorResourceMap.get(UniqueIdentifier.parse(viewProcessorId));
  }
  
  @GET
  public Response get() {
    return Response.ok(_viewProcessorResourceMap.keySet()).build();
  }
  
}
