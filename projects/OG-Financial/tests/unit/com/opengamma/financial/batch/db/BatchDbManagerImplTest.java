/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.batch.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.time.Instant;
import javax.time.calendar.OffsetTime;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.opengamma.config.DefaultConfigDocument;
import com.opengamma.engine.ComputationTargetSpecification;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.view.ViewDefinition;
import com.opengamma.financial.ViewTestUtils;
import com.opengamma.financial.batch.BatchJob;
import com.opengamma.financial.batch.BatchJobRun;
import com.opengamma.financial.batch.LiveDataValue;
import com.opengamma.id.Identifier;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.util.db.DbDateUtils;
import com.opengamma.util.test.TransactionalHibernateTest;

/**
 * 
 */
public class BatchDbManagerImplTest extends TransactionalHibernateTest {
    
  private BatchDbManagerImpl _dbManager;
  private BatchJob _batchJob;
  private BatchJobRun _batchJobRun;
  
  public BatchDbManagerImplTest(String databaseType, final String databaseVersion) {
    super(databaseType, databaseVersion);
  }
  
  @Override
  public Class<?>[] getHibernateMappingClasses() {
    return BatchDbManagerImpl.getHibernateMappingClasses();
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    
    _dbManager = new BatchDbManagerImpl();
    _dbManager.initialize(getDbTool(), getSessionFactory());
    
    _batchJob = new BatchJob();
    _batchJob.setBatchDbManager(_dbManager);
    _batchJob.setViewName("test_view");
    _batchJob.setView(ViewTestUtils.getMockView());
    _batchJob.setViewDefinitionConfig(new DefaultConfigDocument<ViewDefinition>(
        "1", "1", 1, "Name", Instant.EPOCH, Instant.EPOCH, _batchJob.getView().getDefinition()));
    
    _batchJobRun = new BatchJobRun(_batchJob);
    _batchJobRun.init();
    _batchJob.addRun(_batchJobRun);
  }
    
  @Test
  public void getVersion() {
    // create
    OpenGammaVersion version1 = _dbManager.getOpenGammaVersion(_batchJob);
    assertNotNull(version1);
    assertEquals(_batchJob.getOpenGammaVersion(), version1.getVersion());
    assertEquals(_batchJob.getOpenGammaVersionHash(), version1.getHash());
    
    // get
    OpenGammaVersion version2 = _dbManager.getOpenGammaVersion(_batchJob);
    assertEquals(version1, version2);
  }
  
  @Test
  public void getObservationTime() {
    // create
    ObservationTime time1 = _dbManager.getObservationTime(_batchJobRun);
    assertNotNull(time1);
    assertEquals(_batchJobRun.getObservationTime(), time1.getLabel());
    
    // get
    ObservationTime time2 = _dbManager.getObservationTime(_batchJobRun);
    assertEquals(time1, time2);
  }
  
  @Test
  public void getObservationDateTime() {
    // create
    ObservationDateTime datetime1 = _dbManager.getObservationDateTime(_batchJobRun);
    assertNotNull(datetime1);
    assertEquals(DbDateUtils.toSqlDate(_batchJobRun.getObservationDate()), datetime1.getDate());
    assertEquals(_batchJobRun.getObservationTime(), datetime1.getObservationTime().getLabel());
    
    // get
    ObservationDateTime datetime2 = _dbManager.getObservationDateTime(_batchJobRun);
    assertEquals(datetime1, datetime2);
  }
  
  @Test
  public void getLocalComputeHost() throws UnknownHostException {
    // create
    ComputeHost host1 = _dbManager.getLocalComputeHost();
    assertNotNull(host1);
    assertEquals(InetAddress.getLocalHost().getHostName(), host1.getHostName());
    
    // get
    ComputeHost host2 = _dbManager.getLocalComputeHost();
    assertEquals(host1, host2);
  }
  
  @Test
  public void getLocalComputeNode() throws UnknownHostException {
    // create
    ComputeNode node1 = _dbManager.getLocalComputeNode();
    assertNotNull(node1);
    assertEquals(_dbManager.getLocalComputeHost(), node1.getComputeHost());
    assertEquals("UNDEFINED", node1.getConfigOid());
    assertEquals(1, node1.getConfigVersion());
    assertEquals(InetAddress.getLocalHost().getHostName(), node1.getNodeName());
    
    // get
    ComputeNode node2 = _dbManager.getLocalComputeNode();
    assertEquals(node1, node2);
  }
  
  @Test
  public void getNonExistentRiskRunFromDb() {
    RiskRun run = _dbManager.getRiskRunFromDb(_batchJobRun);
    assertNull(run);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void tryToGetNonExistentLiveDataSnapshot() {
    _dbManager.getLiveDataSnapshot(_batchJobRun);
  }
  
  @Test
  public void markSnapshotComplete() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId()); 
    
    LiveDataSnapshot snapshot = _dbManager.getLiveDataSnapshot(_batchJobRun);
    assertNotNull(snapshot);
    assertEquals(DbDateUtils.toSqlDate(_batchJobRun.getSnapshotObservationDate()), 
        snapshot.getSnapshotTime().getDate());
    assertEquals(_batchJobRun.getSnapshotObservationTime(), 
        snapshot.getSnapshotTime().getObservationTime().getLabel());
    assertFalse(snapshot.isComplete());
    assertTrue(snapshot.getSnapshotEntries().isEmpty());

    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());

    assertTrue(snapshot.isComplete());
  }
  
  @Test(expected=IllegalArgumentException.class) 
  public void tryToMarkNonExistentSnapshotComplete() {
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
  }
  
  @Test(expected=IllegalStateException.class) 
  public void tryToMarkSnapshotCompleteTwice() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());

    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
  }
  
  @Test 
  public void getLiveDataField() {
    // create
    LiveDataField field1 = _dbManager.getLiveDataField("test_field");
    assertNotNull(field1);
    assertEquals("test_field", field1.getName());
    
    // get
    LiveDataField field2 = _dbManager.getLiveDataField("test_field");
    assertEquals(field1, field2);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void addValuesToNonexistentSnapshot() {
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), Collections.<LiveDataValue>emptySet());
  }
  
  @Test(expected=IllegalStateException.class)
  public void addValuesToCompleteSnapshot() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
          
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    
    LiveDataValue value = new LiveDataValue(new ComputationTargetSpecification(
        Identifier.of("BUID", "EQ12345")), "BID", 11.22);
    Set<LiveDataValue> values = Collections.singleton(value);
    
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), values);
    
    Set<LiveDataValue> returnedValues = _dbManager.getSnapshotValues(_batchJobRun.getSnapshotId());
    
    assertEquals(values, returnedValues);
  }
  
  @Test
  public void addValuesToIncompleteSnapshot() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), Collections.<LiveDataValue>emptySet());
    
    LiveDataSnapshot snapshot = _dbManager.getLiveDataSnapshot(_batchJobRun);
    assertNotNull(snapshot);
    assertEquals(0, snapshot.getSnapshotEntries().size());
    
    Set<ComputationTargetSpecification> specs = Sets.newHashSet();
    specs.add(new ComputationTargetSpecification(Identifier.of("BUID", "EQ12345")));
    specs.add(new ComputationTargetSpecification(Identifier.of("BUID", "EQ12346")));
    specs.add(new ComputationTargetSpecification(Identifier.of("BUID", "EQ12347")));
    
    Set<LiveDataValue> values = new HashSet<LiveDataValue>();
    for (ComputationTargetSpecification spec : specs) {
      values.add(new LiveDataValue(spec, "field_name", 123.45));
    }
    
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), values);
    
    snapshot = _dbManager.getLiveDataSnapshot(_batchJobRun);
    assertEquals(specs.size(), snapshot.getSnapshotEntries().size());
    for (ComputationTargetSpecification spec : specs) {
      LiveDataSnapshotEntry entry = snapshot.getEntry(spec, "field_name");
      assertNotNull(entry);
      assertEquals(snapshot, entry.getSnapshot());
      assertEquals(spec, entry.getComputationTarget().toSpec());
      assertEquals("field_name", entry.getField().getName());
      assertEquals(123.45, entry.getValue(), 0.000001);
    }
    
    // should not add anything extra
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), values);
    snapshot = _dbManager.getLiveDataSnapshot(_batchJobRun);
    assertEquals(3, snapshot.getSnapshotEntries().size());
    
    // should update 1, add 1
    values = new HashSet<LiveDataValue>();
    values.add(new LiveDataValue(new ComputationTargetSpecification(Identifier.of("BUID", "EQ12347")), "field_name", 123.46));
    values.add(new LiveDataValue(new ComputationTargetSpecification(Identifier.of("BUID", "EQ12348")), "field_name", 123.45));
    
    _dbManager.addValuesToSnapshot(_batchJobRun.getSnapshotId(), values);
    snapshot = _dbManager.getLiveDataSnapshot(_batchJobRun);
    assertEquals(4, snapshot.getSnapshotEntries().size());
  }
  
  @Test
  public void fixLiveDataSnapshotTime() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    
    _dbManager.fixLiveDataSnapshotTime(_batchJobRun.getSnapshotId(),
        OffsetTime.nowSystemClock());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void tryToFixNonexistentLiveDataSnapshotTime() {
    _dbManager.fixLiveDataSnapshotTime(_batchJobRun.getSnapshotId(),
        OffsetTime.nowSystemClock());
  }
  
  @Test
  public void fixCompleteLiveDataSnapshotTime() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    
    // this is OK
    _dbManager.fixLiveDataSnapshotTime(_batchJobRun.getSnapshotId(),
        OffsetTime.nowSystemClock());
  }
  
  @Test
  public void createLiveDataSnapshotMultipleTimes() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    
    assertNotNull(_dbManager.getLiveDataSnapshot(_batchJobRun));
  }
  
  @Test
  public void createThenGetRiskRun() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    RiskRun run = _dbManager.createRiskRun(_batchJobRun);
    
    assertNotNull(run);
    assertNotNull(run.getCreateInstant());
    assertNotNull(run.getStartInstant());
    assertNull(run.getEndInstant());
    assertTrue(run.getCalculationConfigurations().isEmpty());
    assertNotNull(run.getLiveDataSnapshot());
    assertEquals(_dbManager.getLocalComputeHost(), run.getMasterProcessHost());
    assertEquals(_dbManager.getOpenGammaVersion(_batchJob), run.getOpenGammaVersion());
    assertEquals(_batchJobRun.getRunReason(), run.getRunReason());
    assertNotNull(run.getValuationTime());
    assertEquals(_batchJob.getViewOid(), run.getViewOid());
    assertEquals(_batchJob.getViewVersion(), run.getViewVersion());
    
    // get
    RiskRun run2 = _dbManager.getRiskRunFromDb(_batchJobRun);
    assertEquals(run, run2);
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void tryToStartBatchWithoutCreatingSnapshot() {
    _dbManager.startBatch(_batchJobRun);
  }
  
  @Test(expected=IllegalStateException.class)
  public void tryToStartBatchWithoutMarkingSnapshotComplete() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    _dbManager.startBatch(_batchJobRun);
  }
  
  @Test
  public void startAndEndBatch() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    _dbManager.startBatch(_batchJobRun);
    
    RiskRun run1 = _dbManager.getRiskRunFromDb(_batchJobRun);
    assertNotNull(run1);
    assertNotNull(run1.getStartInstant());
    assertNull(run1.getEndInstant());
    
    RiskRun run2 = _dbManager.getRiskRunFromHandle(_batchJobRun);
    assertEquals(run1, run2);
    
    _dbManager.endBatch(_batchJobRun);
    
    run1 = _dbManager.getRiskRunFromDb(_batchJobRun);
    run2 = _dbManager.getRiskRunFromHandle(_batchJobRun);
    
    assertNotNull(run1);
    assertNotNull(run1.getStartInstant());
    assertNotNull(run1.getEndInstant());
    
    assertEquals(run1, run2);
  }
  
  @Test
  public void startBatchTwice() {
    _dbManager.createLiveDataSnapshot(_batchJobRun.getSnapshotId());
    _dbManager.markLiveDataSnapshotComplete(_batchJobRun.getSnapshotId());
    _dbManager.startBatch(_batchJobRun);
    
    RiskRun run = _dbManager.getRiskRunFromDb(_batchJobRun);
    assertEquals(0, run.getNumRestarts());
    
    _dbManager.startBatch(_batchJobRun);
    assertEquals(1, run.getNumRestarts());
  }
  
  @Test
  public void getComputationTarget() {
    UniqueIdentifier uid = UniqueIdentifier.of("foo", "bar");
    
    ComputationTarget portfolio = _dbManager.getComputationTarget(
        new ComputationTargetSpecification(ComputationTargetType.PORTFOLIO_NODE, uid));
    assertNotNull(portfolio);
    assertEquals(ComputationTargetType.PORTFOLIO_NODE.ordinal(), portfolio.getComputationTargetType());
    assertEquals(uid.getScheme(), portfolio.getIdScheme());
    assertEquals(uid.getValue(), portfolio.getIdValue());
    
    ComputationTarget position = _dbManager.getComputationTarget(
        new ComputationTargetSpecification(ComputationTargetType.POSITION, uid));
    assertEquals(ComputationTargetType.POSITION.ordinal(), position.getComputationTargetType());
    
    ComputationTarget security = _dbManager.getComputationTarget(
        new ComputationTargetSpecification(ComputationTargetType.SECURITY, uid));
    assertEquals(ComputationTargetType.SECURITY.ordinal(), security.getComputationTargetType());
    
    ComputationTarget primitive = _dbManager.getComputationTarget(
        new ComputationTargetSpecification(ComputationTargetType.PRIMITIVE, uid));
    assertEquals(ComputationTargetType.PRIMITIVE.ordinal(), primitive.getComputationTargetType());
  }
  
  @Test
  public void getValueName() {
    // create
    RiskValueName valueName1 = _dbManager.getRiskValueName("test_name");
    assertNotNull(valueName1);
    assertEquals("test_name", valueName1.getName());
    
    // get
    RiskValueName valueName2 = _dbManager.getRiskValueName("test_name");
    assertEquals(valueName1, valueName2);
  }
    
}