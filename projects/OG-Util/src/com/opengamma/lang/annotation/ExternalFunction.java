/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.lang.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to signal that a method should be exposed through an external language such as Excel or R.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ExternalFunction {
  /**
   * The name of the function exposed to Excel. If not specified here, a name will be created from the method name
   */
  String name() default "";
  
  /**
   * The description of the function which appears in various places in the target language.  For example, in Excel, 
   * the tooltip as the function is being entered, and the 'Insert Function' dialog would use this field. 
   * <p>
   * This should be written in complete sentences, properly capitalised.
   */
  String description() default "";
}
