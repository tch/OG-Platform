/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.view.rest;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.opengamma.engine.livedata.LiveDataInjector;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.engine.view.ViewProcess;
import com.opengamma.engine.view.ViewProcessState;
import com.opengamma.financial.livedata.rest.RemoteLiveDataInjector;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.rest.FudgeRestClient;

/**
 * Provides access to a remote {@link ViewProcess}.
 */
public class RemoteViewProcess implements ViewProcess {

  private final URI _baseUri;
  private final FudgeRestClient _client;
  
  public RemoteViewProcess(URI baseUri) {
    _baseUri = baseUri;
    _client = FudgeRestClient.create();
  }
  
  //-------------------------------------------------------------------------
  @Override
  public UniqueIdentifier getUniqueId() {
    URI uri = UriBuilder.fromUri(_baseUri).path(DataViewProcessResource.PATH_UNIQUE_ID).build();
    return _client.access(uri).get(UniqueIdentifier.class);
  }
  
  @Override
  public String getDefinitionName() {
    URI uri = UriBuilder.fromUri(_baseUri).path(DataViewProcessResource.PATH_DEFINITION_NAME).build();
    return _client.access(uri).get(String.class);
  }
  
  @Override
  public ViewDefinition getDefinition() {
    URI uri = UriBuilder.fromUri(_baseUri).path(DataViewProcessResource.PATH_DEFINITION).build();
    return _client.access(uri).get(ViewDefinition.class);
  }
  
  @Override
  public ViewProcessState getState() {
    URI uri = UriBuilder.fromUri(_baseUri).path(DataViewProcessResource.PATH_STATE).build();
    return _client.access(uri).get(ViewProcessState.class);
  }
  
  @Override
  public LiveDataInjector getLiveDataOverrideInjector() {
    URI uri = UriBuilder.fromUri(_baseUri).path(DataViewProcessResource.PATH_LIVE_DATA_OVERRIDE_INJECTOR).build();
    return new RemoteLiveDataInjector(uri);
  }
  
  @Override
  public void shutdown() {
    _client.access(_baseUri).delete();
  }

}
