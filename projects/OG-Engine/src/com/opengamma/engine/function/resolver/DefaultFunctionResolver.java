/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.function.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.depgraph.UnsatisfiableDependencyGraphException;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionDefinition;
import com.opengamma.engine.function.FunctionRepository;
import com.opengamma.engine.function.ParameterizedFunction;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.tuple.Pair;

/**
 * 
 */
public class DefaultFunctionResolver implements FunctionResolver {
  
  /**
   * The map is sorted from highest to lowest priority
   */
  private Map<Integer, Collection<ResolutionRule>> _priority2Rules = new TreeMap<Integer, Collection<ResolutionRule>>(
      new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
          return -o1.compareTo(o2);
        }
      });
  
  public DefaultFunctionResolver() {
  }
  
  public DefaultFunctionResolver(FunctionRepository repository) {
    Collection<ResolutionRule> resolutionRules = new ArrayList<ResolutionRule>();
    for (FunctionDefinition function : repository.getAllFunctions()) {
      ResolutionRule rule = new ResolutionRule(
          new ParameterizedFunction(function, function.getDefaultParameters()),
          new ApplyToAllTargets(),
          0);
      resolutionRules.add(rule);
    }
    addRules(resolutionRules);
  }

  public DefaultFunctionResolver(Collection<ResolutionRule> resolutionRules) {
    addRules(resolutionRules);
  }

  public void addRule(ResolutionRule rule) {
    addRules(Collections.singleton(rule));    
  }
  
  public void addRules(Collection<ResolutionRule> resolutionRules) {
    for (ResolutionRule resolutionRule : resolutionRules) {
      Collection<ResolutionRule> rules = _priority2Rules.get(resolutionRule.getPriority());
      if (rules == null) {
        rules = new ArrayList<ResolutionRule>();
        _priority2Rules.put(resolutionRule.getPriority(), rules);
      }
      rules.add(resolutionRule);     
    }
  }
  
  @Override
  public Pair<ParameterizedFunction, ValueSpecification> resolveFunction(
      ValueRequirement requirement, DependencyNode atNode, FunctionCompilationContext context) {
    
    // the idea is to consider highest priority rules first, then the ones with priority
    // just below the highest, and so on until the lowest
    
    for (Map.Entry<Integer, Collection<ResolutionRule>> entry : _priority2Rules.entrySet()) {
      Integer priority = entry.getKey();
      Collection<ResolutionRule> rules = entry.getValue();
      
      Collection<ResolutionRule> applicableRules = new ArrayList<ResolutionRule>();
      ValueSpecification validResult = null;
      for (ResolutionRule rule : rules) {
        ValueSpecification result = rule.getResult(requirement, atNode, context);
        if (result != null) {
          applicableRules.add(rule);
          validResult = result;
        }
      }
      
      if (!applicableRules.isEmpty()) {
        if (applicableRules.size() > 1) {
          throw new UnsatisfiableDependencyGraphException("There is more than 1 rule with priority " 
              + priority 
              + " that can satisfy requirement " 
              + requirement + " for target " + 
              atNode.getComputationTarget() + ". The rules are: " +
              applicableRules);          
        } else {
          // we can quit here because the map is sorted from highest to lowest priority
          ResolutionRule onlyApplicableRule = applicableRules.iterator().next();
          return Pair.of(onlyApplicableRule.getFunction(), validResult);
        }
        
      }
    }
    
    throw new UnsatisfiableDependencyGraphException("There is no rule that can satisfy requirement " 
        + requirement + " for target " + atNode.getComputationTarget()); 
  }

}