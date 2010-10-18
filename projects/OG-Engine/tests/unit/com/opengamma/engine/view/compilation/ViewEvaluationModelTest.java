/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.engine.view.compilation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.time.Instant;
import javax.time.InstantProvider;

import org.junit.Test;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.depgraph.DependencyGraph;
import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.function.CompiledFunctionDefinition;
import com.opengamma.engine.function.EmptyFunctionParameters;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.function.FunctionDefinition;
import com.opengamma.engine.function.FunctionInvoker;
import com.opengamma.engine.function.FunctionParameters;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;

/**
 * Tests the behavior of ViewEvaluationModel.
 */
public class ViewEvaluationModelTest {

  private final Instant _time0 = Instant.nowSystemClock();
  private final Instant _time1 = _time0.plusMillis(1);
  private final Instant _time2 = _time0.plusMillis(2);
  private final Instant _time3 = _time0.plusMillis(3);
  private final Instant _time4 = _time0.plusMillis(4);
  private final Instant _time5 = _time0.plusMillis(5);

  private DependencyNode createDependencyNode(final Instant functionStart, final Instant functionEnd) {
    final DependencyNode node = new DependencyNode(new ComputationTarget("Foo"));
    node.setFunction(new CompiledFunctionDefinition() {

      @Override
      public boolean canApplyTo(FunctionCompilationContext context, ComputationTarget target) {
        return false;
      }

      @Override
      public Instant getEarliestInvocationTime() {
        return functionStart;
      }

      @Override
      public FunctionDefinition getFunctionDefinition() {
        return new FunctionDefinition() {

          @Override
          public CompiledFunctionDefinition compile(FunctionCompilationContext context, InstantProvider atInstant) {
            return null;
          }

          @Override
          public FunctionParameters getDefaultParameters() {
            return new EmptyFunctionParameters();
          }

          @Override
          public String getShortName() {
            return null;
          }

          @Override
          public String getUniqueIdentifier() {
            return null;
          }

          @Override
          public void init(FunctionCompilationContext context) {
          }

        };
      }

      @Override
      public FunctionInvoker getFunctionInvoker() {
        return null;
      }

      @Override
      public Instant getLatestInvocationTime() {
        return functionEnd;
      }

      @Override
      public Set<ValueSpecification> getRequiredLiveData() {
        return Collections.emptySet ();
      }

      @Override
      public Set<ValueRequirement> getRequirements(FunctionCompilationContext context, ComputationTarget target) {
        return null;
      }

      @Override
      public Set<ValueSpecification> getResults(FunctionCompilationContext context, ComputationTarget target) {
        return null;
      }

      @Override
      public ComputationTargetType getTargetType() {
        return ComputationTargetType.PRIMITIVE;
      }

    });
    return node;
  }

  private DependencyGraph graphNoStartEndTimes() {
    final DependencyGraph graph = new DependencyGraph("no start/end");
    graph.addDependencyNode(createDependencyNode(null, null));
    graph.addDependencyNode(createDependencyNode(null, null));
    return graph;
  }

  private DependencyGraph graphOneEndTime(final Instant end) {
    final DependencyGraph graph = new DependencyGraph("one end");
    graph.addDependencyNode(createDependencyNode(null, null));
    graph.addDependencyNode(createDependencyNode(null, end));
    return graph;
  }

  private DependencyGraph graphTwoEndTimes(final Instant end1, final Instant end2) {
    final DependencyGraph graph = new DependencyGraph("two ends");
    graph.addDependencyNode(createDependencyNode(null, null));
    graph.addDependencyNode(createDependencyNode(null, end1));
    graph.addDependencyNode(createDependencyNode(null, end2));
    return graph;
  }

  private DependencyGraph graphOneStartTime(final Instant start) {
    final DependencyGraph graph = new DependencyGraph("one start");
    graph.addDependencyNode(createDependencyNode(null, null));
    graph.addDependencyNode(createDependencyNode(start, null));
    return graph;
  }

  private DependencyGraph graphTwoStartTimes(final Instant start1, final Instant start2) {
    final DependencyGraph graph = new DependencyGraph("two starts");
    graph.addDependencyNode(createDependencyNode(null, null));
    graph.addDependencyNode(createDependencyNode(start1, null));
    graph.addDependencyNode(createDependencyNode(start2, null));
    return graph;
  }

  private ViewEvaluationModel buildViewEvaluationModel(final DependencyGraph... graphs) {
    final Map<String, DependencyGraph> map = new HashMap<String, DependencyGraph>();
    for (DependencyGraph graph : graphs) {
      map.put(graph.getCalcConfName(), graph);
    }
    return new ViewEvaluationModel(map, null);
  }

  @Test
  public void testNoValidityTimes() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes());
    assertTrue(model.isValidFor(Long.MIN_VALUE));
    assertTrue(model.isValidFor(Long.MAX_VALUE));
  }

  @Test
  public void testNoStartTime1() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes(), graphOneEndTime(_time0), graphTwoEndTimes(_time1, _time2));
    assertTrue(model.isValidFor(Long.MIN_VALUE));
    assertTrue(model.isValidFor(_time0.toEpochMillisLong()));
    assertFalse(model.isValidFor(_time1.toEpochMillisLong()));
  }

  @Test
  public void testNoStartTime2() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes(), graphOneEndTime(_time1), graphTwoEndTimes(_time0, _time2));
    assertTrue(model.isValidFor(Long.MIN_VALUE));
    assertTrue(model.isValidFor(_time0.toEpochMillisLong()));
    assertFalse(model.isValidFor(_time1.toEpochMillisLong()));
  }

  @Test
  public void testNoEndTime1() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes(), graphOneStartTime(_time2), graphTwoStartTimes(_time0, _time1));
    assertFalse(model.isValidFor(_time1.toEpochMillisLong()));
    assertTrue(model.isValidFor(_time2.toEpochMillisLong()));
    assertTrue(model.isValidFor(Long.MAX_VALUE));
  }

  @Test
  public void testNoEndTime2() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes(), graphOneStartTime(_time1), graphTwoStartTimes(_time0, _time2));
    assertFalse(model.isValidFor(_time1.toEpochMillisLong()));
    assertTrue(model.isValidFor(_time2.toEpochMillisLong()));
    assertTrue(model.isValidFor(Long.MAX_VALUE));
  }

  @Test
  public void testStartEndTime() {
    final ViewEvaluationModel model = buildViewEvaluationModel(graphNoStartEndTimes(), graphOneStartTime(_time0), graphTwoStartTimes(_time1, _time2), graphOneEndTime(_time3), graphTwoEndTimes(
        _time4, _time5));
    assertFalse(model.isValidFor(_time1.toEpochMillisLong()));
    assertTrue(model.isValidFor(_time2.toEpochMillisLong()));
    assertTrue(model.isValidFor(_time3.toEpochMillisLong()));
    assertFalse(model.isValidFor(_time4.toEpochMillisLong()));
  }

}