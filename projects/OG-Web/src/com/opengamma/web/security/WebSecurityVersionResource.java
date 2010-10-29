/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.web.security;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.joda.beans.impl.flexi.FlexiBean;

import com.opengamma.financial.security.master.SecurityDocument;
import com.opengamma.id.UniqueIdentifier;

/**
 * RESTful resource for a version of a security.
 */
@Path("/securities/{securityId}/versions/{versionId}")
@Produces(MediaType.TEXT_HTML)
public class WebSecurityVersionResource extends AbstractWebSecurityResource {

  /**
   * Creates the resource.
   * @param parent  the parent resource, not null
   */
  public WebSecurityVersionResource(final AbstractWebSecurityResource parent) {
    super(parent);
  }

  //-------------------------------------------------------------------------
  @GET
  public String get() {
    FlexiBean out = createRootData();
    return getFreemarker().build("securities/securityversion.ftl", out);
  }

  //-------------------------------------------------------------------------
  /**
   * Creates the output root data.
   * @return the output root data, not null
   */
  protected FlexiBean createRootData() {
    FlexiBean out = super.createRootData();
    SecurityDocument latestSecDoc = data().getSecurity();
    SecurityDocument versionedSecurity = data().getVersioned();
    out.put("latestSecurityDoc", latestSecDoc);
    out.put("latestSecurity", latestSecDoc.getSecurity());
    out.put("securityDoc", versionedSecurity);
    out.put("security", versionedSecurity.getSecurity());
    return out;
  }

  //-------------------------------------------------------------------------
  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @return the URI, not null
   */
  public static URI uri(final WebSecuritiesData data) {
    return uri(data, null);
  }

  /**
   * Builds a URI for this resource.
   * @param data  the data, not null
   * @param overrideVersionId  the override version id, null uses information from data
   * @return the URI, not null
   */
  public static URI uri(final WebSecuritiesData data, final UniqueIdentifier overrideVersionId) {
    String securityId = data.getBestSecurityUriId(null);
    String versionId = StringUtils.defaultString(overrideVersionId != null ? overrideVersionId.getVersion() : data.getUriVersionId());
    return data.getUriInfo().getBaseUriBuilder().path(WebSecurityVersionResource.class).build(securityId, versionId);
  }

}