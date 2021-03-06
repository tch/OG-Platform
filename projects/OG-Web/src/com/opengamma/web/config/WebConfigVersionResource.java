/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.config;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.config.ConfigDocument;

/**
 * RESTful resource for a version of a config.
 */
@Path("/configs/{configId}/versions/{versionId}")
@Produces(MediaType.TEXT_HTML)
public class WebConfigVersionResource extends AbstractWebConfigResource {

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  public WebConfigVersionResource(final AbstractWebConfigResource parent) {
    super(parent);
  }

  //-------------------------------------------------------------------------
  @GET
  public String get() {
    FlexiBean out = createRootData();
    return getFreemarker().build("configs/configversion.ftl", out);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = super.createRootData();
    ConfigDocument<?> latestDoc = data().getConfig();
    ConfigDocument<?> versionedConfig = data().getVersioned();
    out.put("latestConfigDoc", latestDoc);
    out.put("latestConfig", latestDoc.getValue());
    out.put("configDoc", versionedConfig);
    out.put("config", versionedConfig.getValue());
    out.put("configXml", createXML(versionedConfig));
    out.put("deleted", !latestDoc.isLatest());
    return out;
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebConfigData data) {
    return uri(data, null);
  }

  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @param overrideVersionId  the override version id, null uses information from data
   * @return the URI, not null
   */
  public static URI uri(final WebConfigData data, final UniqueIdentifier overrideVersionId) {
    String configId = data.getBestConfigUriId(null);
    String versionId = StringUtils.defaultString(overrideVersionId != null ? overrideVersionId.getVersion() : data.getUriVersionId());
    return data.getUriInfo().getBaseUriBuilder().path(WebConfigVersionResource.class).build(configId, versionId);
  }

}
