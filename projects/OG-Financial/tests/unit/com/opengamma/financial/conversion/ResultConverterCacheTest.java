/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.conversion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.opengamma.financial.model.interestrate.curve.DiscountCurve;
import com.opengamma.math.curve.ConstantDoublesCurve;
import com.opengamma.math.matrix.DoubleMatrix1D;
import com.opengamma.math.matrix.DoubleMatrix2D;
import com.opengamma.util.timeseries.date.ArrayDateDoubleTimeSeries;

/**
 * 
 */
public class ResultConverterCacheTest {
  
  @Test
  public void get() {
    ResultConverterCache cache = new ResultConverterCache();
    
    ResultConverter<?> converter = cache.getConverter(new Double(5.5));
    assertNotNull(converter);
    assertTrue(converter instanceof DoubleConverter);
    
    converter = cache.getConverter(new DoubleMatrix1D(new double[0]));
    assertNotNull(converter);
    assertTrue(converter instanceof DoubleMatrix1DConverter);
    
    converter = cache.getConverter(new DoubleMatrix2D(new double[0][0]));
    assertNotNull(converter);
    assertTrue(converter instanceof DoubleMatrix2DConverter);
    
    converter = cache.getConverter(new ArrayDateDoubleTimeSeries());
    assertNotNull(converter);
    assertTrue(converter instanceof TimeSeriesConverter);
    
    converter = cache.getConverter(new DiscountCurve(new ConstantDoublesCurve(2.5)));
    assertNotNull(converter);
    assertTrue(converter instanceof YieldAndDiscountCurveConverter);
  }

}