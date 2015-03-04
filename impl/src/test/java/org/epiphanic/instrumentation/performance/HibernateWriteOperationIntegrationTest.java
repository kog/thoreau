/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.Date;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Provides a database-connected integration test demonstrating that our write operation works as intended, and also
 * that our Hibernate mappings work.
 *
 * @author Greg Feigenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class HibernateWriteOperationIntegrationTest
{
	/**
	 * Holds an instance of the class under test. This is injected from our Spring context set up in our beans file for the
	 * test.
	 */
	@Autowired
	private HibernateWriteOperationFactory _writeOperationFactory;

	/**
	 * Holds a {@link org.hibernate.SessionFactory} that we can use for creating a session for our unit test. This is
	 * injected from our Spring context set up in our beans file for the test.
	 */
	@Autowired
	private SessionFactory _sessionFactory;

	/**
	 * Holds a {@link org.hibernate.Session} that is set up, and torn down, per each test. This allows us to do operations
	 * against our test database to verify our results.
	 */
	private Session _session;

	/**
	 * Holds an injected instance of our {@link javax.sql.DataSource} so that we can query our backing store.
	 */
	@Autowired
	private DataSource _dataSource;

	/**
	 * Holds an instance of {@link org.springframework.jdbc.core.JdbcTemplate} that we create upon test setup, based on our
	 * injected {@link javax.sql.DataSource}. Useful for querying things.
	 */
	private JdbcTemplate _jdbcTemplate;


	/**
	 * Runs any set up for our unit tests. In this case, we create a hibernate {@link org.hibernate.Session} that we can
	 * use for grabbing mapped entities, as well as creating a {@link org.springframework.jdbc.core.JdbcTemplate} for
	 * running queries as needed.
	 */
	@Before
	public void setUp() throws Exception
	{
		_session = _sessionFactory.openSession();
		_jdbcTemplate = new JdbcTemplate(_dataSource);
	}


	/**
	 * Runs any tear-down for our unit tests. In this case we destroy our {@link org.hibernate.Session} that we've been
	 * using in our unit tests, then close our {@link org.hibernate.SessionFactory} we've injected.
	 */
	@After
	public void tearDown() throws Exception
	{
		_session.close();
		_sessionFactory.close();
	}


	/**
	 * Tests to make sure that when we call our metric callable that we write our statistic to the database.
	 */
	@Test
	public void testCallWrite() throws Exception
	{
		// Create our statistic entity to persist later.
		final MethodCallStatistic stat = new MethodCallStatistic();
		stat.setMetaData("arf");
		stat.setOperationCompletion(new Date());
		stat.setOperationStart(new Date());
		stat.setOperationName(HibernateWriteOperationIntegrationTest.class.getName());
		stat.setUserId(1L);

		// Attempt to write our statistic to the database.
		_writeOperationFactory.createWriteOperation(stat).call();

		// Nuke our session cache.
		_session.clear();

		// Demonstrate that our eviction worked and we're not getting a cached value.
		_jdbcTemplate.execute("update method_performance set metadata = 'asdfasdfasdf'");
		final MethodCallStatistic mutatedStat = (MethodCallStatistic)_session.load(stat.getClass(), 1L);
		Assert.assertNotSame(stat.getMetaData(), mutatedStat.getMetaData());
		_jdbcTemplate.execute("update method_performance set metadata = 'arf'");

		// Clear our session again, and then verify that we persisted what we thought.
		_session.clear();
		assertMethodCallStatisticEqualityByValue(stat, (MethodCallStatistic)_session.load(stat.getClass(), 1L));
	}


	/**
	 * A convenience method, makes sure that our {@link org.epiphanic.instrumentation.performance.MethodCallStatistic} we
	 * just fetched from the database matches what we think it should be - namely the entity that we've saved, which has
	 * been detached from our session cache.
	 *
	 * @param knownGoodStat The reference {@link org.epiphanic.instrumentation.performance.MethodCallStatistic}.
	 * @param statToCompare The persisted {@link org.epiphanic.instrumentation.performance.MethodCallStatistic}.
	 */
	private void assertMethodCallStatisticEqualityByValue(@NotNull final MethodCallStatistic knownGoodStat,
														  @NotNull final MethodCallStatistic statToCompare)
	{
		Assert.assertEquals(knownGoodStat.getId(), statToCompare.getId());
		Assert.assertEquals(knownGoodStat.getMetaData(), statToCompare.getMetaData());
		Assert.assertEquals(knownGoodStat.getOperationCompletion(), statToCompare.getOperationCompletion());
		Assert.assertEquals(knownGoodStat.getOperationStart(), statToCompare.getOperationStart());
		Assert.assertEquals(knownGoodStat.getOperationName(), statToCompare.getOperationName());
		Assert.assertEquals(knownGoodStat.getUserId(), statToCompare.getUserId());
	}
}