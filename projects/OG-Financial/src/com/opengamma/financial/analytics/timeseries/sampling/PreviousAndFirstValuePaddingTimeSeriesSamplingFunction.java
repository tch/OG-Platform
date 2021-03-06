/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.timeseries.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.time.calendar.LocalDate;

import org.apache.commons.lang.Validate;

import com.opengamma.util.timeseries.DoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.ArrayLocalDateDoubleTimeSeries;
import com.opengamma.util.timeseries.localdate.LocalDateDoubleTimeSeries;

/**
 * 
 */
public class PreviousAndFirstValuePaddingTimeSeriesSamplingFunction implements TimeSeriesSamplingFunction {

  @Override
  public DoubleTimeSeries<?> getSampledTimeSeries(final DoubleTimeSeries<?> ts, final LocalDate[] schedule) {
    Validate.notNull(ts, "time series");
    Validate.notNull(schedule, "schedule");
    final LocalDateDoubleTimeSeries localDateTS = ts.toLocalDateDoubleTimeSeries();
    final List<LocalDate> tsDates = localDateTS.times();
    final List<LocalDate> scheduledDates = new ArrayList<LocalDate>();
    final List<Double> scheduledData = new ArrayList<Double>();
    final TreeSet<LocalDate> sortedTSDates = new TreeSet<LocalDate>(tsDates);
    final double firstValue = localDateTS.getEarliestValue();
    final LocalDate firstDate = localDateTS.getEarliestTime();
    for (final LocalDate localDate : schedule) {
      scheduledDates.add(localDate);
      if (sortedTSDates.contains(localDate)) {
        scheduledData.add(localDateTS.getValue(localDate));
      } else if (firstDate.isAfter(localDate)) {
        scheduledData.add(firstValue);
      } else {
        LocalDate temp = localDate.minusDays(1);
        if (firstDate.isAfter(temp)) {
          scheduledData.add(firstValue);
        } else {
          while (!sortedTSDates.contains(temp)) {
            temp = temp.minusDays(1);
            if (temp.isBefore(schedule[0].toLocalDate()) || temp.isBefore(tsDates.get(0))) {
              scheduledData.add(firstValue);
              break;
            }
          }
        }
        scheduledData.add(localDateTS.getValue(temp));
      }
    }
    return new ArrayLocalDateDoubleTimeSeries(scheduledDates, scheduledData);
  }
}
