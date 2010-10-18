/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.option;

import java.util.HashSet;
import java.util.Set;

import javax.time.calendar.Clock;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionInputs;
import com.opengamma.engine.security.SecuritySource;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.model.option.definition.SkewKurtosisOptionDataBundle;
import com.opengamma.financial.model.option.definition.StandardOptionDataBundle;
import com.opengamma.financial.security.option.OptionSecurity;
import com.opengamma.id.UniqueIdentifier;

/**
 * 
 */
public abstract class SkewKurtosisDataOptionModelFunction extends StandardOptionDataAnalyticOptionModelFunction {

  @Override
  protected SkewKurtosisOptionDataBundle getDataBundle(final SecuritySource secMaster, final Clock relevantTime, final OptionSecurity option, final FunctionInputs inputs) {
    final StandardOptionDataBundle standardData = super.getDataBundle(secMaster, relevantTime, option, inputs);
    final UniqueIdentifier uid = option.getUniqueIdentifier();
    final Object skewObject = inputs.getValue(new ValueRequirement(ValueRequirementNames.SKEW, ComputationTargetType.SECURITY, uid));
    if (skewObject == null) {
      throw new NullPointerException("Could not get skew");
    }
    final Object kurtosisObject = inputs.getValue(new ValueRequirement(ValueRequirementNames.PEARSON_KURTOSIS, ComputationTargetType.SECURITY, uid));
    if (kurtosisObject == null) {
      throw new NullPointerException("Could not get Pearson kurtosis");
    }
    final double skew = (Double) skewObject;
    final double kurtosis = (Double) kurtosisObject;
    return new SkewKurtosisOptionDataBundle(standardData, skew, kurtosis);
  }

  @Override
  public Set<ValueRequirement> getRequirements(final FunctionCompilationContext context, final ComputationTarget target) {
    if (canApplyTo(context, target)) {
      final UniqueIdentifier uid = target.getSecurity().getUniqueIdentifier();
      final Set<ValueRequirement> standardRequirements = super.getRequirements(context, target);
      final Set<ValueRequirement> result = new HashSet<ValueRequirement>();
      result.addAll(standardRequirements);
      result.add(new ValueRequirement(ValueRequirementNames.SKEW, ComputationTargetType.SECURITY, uid));
      result.add(new ValueRequirement(ValueRequirementNames.PEARSON_KURTOSIS, ComputationTargetType.SECURITY, uid));
      return result;
    }
    return null;
  }
}