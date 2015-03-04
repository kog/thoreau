/*
 * org-epiphanic-libraries-performance
 * Copyright 2010 Epiphanic Networks
 *
 * This source is licensed under the terms of the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * A full-blown, wired up, Springtastic integration test of our {@link org.epiphanic.instrumentation.performance.AOPMetricGatherer},
 * including method interception and a Hibernate configuration. You can more or less take this test and it's
 * configuration and transfer it to your project to use the artifacts from this project.<p/>
 *
 * @author Greg Feigenson
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class AOPMetricGathererIntegrationTest
{
	/**
	 * Holds an autowired-by-name instance of our class under test. We've set up all our object graph and some AOP advice
	 * on this class, telling our interceptor {@link org.epiphanic.instrumentation.performance.AOPMetricGatherer} to be
	 * intercept our method.
	 */
	@Resource
	private IBoringClassWithInstrumentableMethods instrumentedClass;

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
	 * Runs any set up for our unit tests. In this case we create a {@link org.springframework.jdbc.core.JdbcTemplate} for
	 * testing our database state later.
	 */
	@Before
	public void setUp() throws Exception
	{
		_jdbcTemplate = new JdbcTemplate(_dataSource);
	}


	/**
	 * Given our instrumented class {@link org.epiphanic.instrumentation.performance.BoringClassWithInstrumentableMethods},
	 * runs through a set of methods and gathers a set of performance metrics. When done, verifies that we've got what we
	 * think we've got.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testInstrumentation() throws Exception
	{
		// Run through the methods we've instrumented. 
		instrumentedClass.fibonacciSequenceRecursive(4);
		instrumentedClass.fibonacciSequenceIterative(4);
		instrumentedClass.voidReturningMethodThatDoesNothing();
		instrumentedClass.longRunningObjectCreatingMethod();

		try
		{
			// We should get some metadata off this method - the stack trace information.
			instrumentedClass.exceptionThrowingMethod();
			Assert.fail();
		}
		catch (final Exception ex)
		{
		}

		// Make sure our writers have a chance to finish.
		Thread.sleep(5000);

		// Pull back our entities from the database then sort it by ID.
		final List<MethodCallStatistic> stats = _jdbcTemplate.query("select * from method_performance", new RowMapper<MethodCallStatistic>()
		{
			@Override
			public MethodCallStatistic mapRow(final ResultSet rs, final int rowNum) throws SQLException
			{
				final MethodCallStatistic methodCallStatistic = new MethodCallStatistic();

				methodCallStatistic.setId(rs.getLong("metric_id"));
				methodCallStatistic.setUserId(rs.getLong("user_id"));
				methodCallStatistic.setOperationName(rs.getString("operation_name"));
				methodCallStatistic.setOperationStart(rs.getDate("start_time"));
				methodCallStatistic.setOperationCompletion(rs.getDate("end_time"));
				methodCallStatistic.setMetaData(rs.getString("metadata"));
				methodCallStatistic.setOperationSuccessful(rs.getBoolean("success"));

				return methodCallStatistic;
			}
		});

		Collections.sort(stats, new Comparator<MethodCallStatistic>()
		{
			@Override
			public int compare(@NotNull final MethodCallStatistic lhs, @NotNull final MethodCallStatistic rhs)
			{
				return lhs.getId().compareTo(rhs.getId());
			}
		});

		// Verify we've got what we think.
		Assert.assertEquals(5, stats.size());

		verifyEntity(stats.get(0), 1L, "fibonacciSequenceRecursive", null, true);
		verifyEntity(stats.get(1), 2L, "fibonacciSequenceIterative", null, true);
		verifyEntity(stats.get(2), 3L, "voidReturningMethodThatDoesNothing", null, true);
		verifyEntity(stats.get(3), 4L, "longRunningObjectCreatingMethod", null, true);
		verifyEntity(stats.get(4), 5L, "exceptionThrowingMethod", "java.lang.Exception: I am the very modern model of a modern Major-General.", false);
	}


	/**
	 * A utility method to verify an extracted object vs. what we think should be set on it. In this case we can skip all
	 * the dates set on our metric, as well as our user ID.
	 *
	 * @param statisticToVerify The {@link org.epiphanic.instrumentation.performance.MethodCallStatistic} to verify.
	 * @param expectedId What we expect {@link MethodCallStatistic#getId()} to be.
	 * @param expectedOperation What we expect {@link MethodCallStatistic#getOperationName()} to be.
	 * @param expectedMetadata What we expect {@link MethodCallStatistic#getMetaData()} to be.
	 * @param expectedSuccessIndicator What we expect {@link MethodCallStatistic#isOperationSuccessful()} to be.
	 */
	private void verifyEntity(final MethodCallStatistic statisticToVerify,
							  final long expectedId,
							  final String expectedOperation,
							  final String expectedMetadata,
							  final boolean expectedSuccessIndicator)
	{
		// This hits both assertEquals(object, object) and assertEquals(long, long) - cast so we can resolve which method
		// to call.
		Assert.assertEquals(expectedId, (long)statisticToVerify.getId());
		Assert.assertEquals(expectedOperation, statisticToVerify.getOperationName());
		Assert.assertEquals(expectedMetadata, statisticToVerify.getMetaData());
		Assert.assertEquals(expectedSuccessIndicator, statisticToVerify.isOperationSuccessful());
	}
}
