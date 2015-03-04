/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


/**
 * Tests the algorithm of our {@link org.epiphanic.instrumentation.performance.AOPMetricGatherer}, making sure that we
 * interact with our collaborators as we expect. This is a true unit test, see {@link
 * org.epiphanic.instrumentation.performance.AOPMetricGathererIntegrationTest} for a full-on integration test.<p/>
 *
 * @author Greg Feigenson
 */
public final class AOPMetricGathererTest
{
	/**
	 * Test the hopefully average case: we've instrumented a method and the method succeeds in execution.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testInvokeSucceeded() throws Throwable
	{
		// Create our mocks necessary for testing.
		final Date now = new Date();
		final AOPMetricGatherer aopMetricGatherer = spy(new AOPMetricGatherer());
		final MethodInvocation invocation = mock(MethodInvocation.class);
		final MethodCallStatistic methodStatistic = mock(MethodCallStatistic.class);
		final IStatisticsLogger<MethodCallStatistic> logger = mock(IStatisticsLogger.class);

		doReturn("ASDF").when(aopMetricGatherer).getMethodName(invocation);
		doReturn(methodStatistic).when(aopMetricGatherer).createMethodCallStatistic();
		doReturn(now).when(aopMetricGatherer).getCurrentDate();
		doReturn(logger).when(aopMetricGatherer).getStatisticsLogger();

		// Run our method under test.
		aopMetricGatherer.invoke(invocation);

		// Verify our method call stack.
		verify(aopMetricGatherer, times(1)).invoke(any(MethodInvocation.class));
		verify(aopMetricGatherer, times(1)).createMethodCallStatistic();
		verify(methodStatistic, times(1)).setOperationName("ASDF");
		verify(aopMetricGatherer, times(1)).getMethodName(invocation);
		verify(methodStatistic, times(1)).setOperationStart(now);
		verify(invocation, times(1)).proceed();
		verify(methodStatistic, times(1)).setOperationCompletion(now);
		verify(aopMetricGatherer, times(2)).getCurrentDate();
		verify(methodStatistic, times(1)).setOperationSuccessful(true);
		verify(aopMetricGatherer, times(1)).getStatisticsLogger();
		verify(logger, times(1)).writeStatistic(any(MethodCallStatistic.class));

		// Make sure nothing else happened.
		verifyNoMoreInteractions(aopMetricGatherer, methodStatistic, invocation, logger);
	}


	/**
	 * Tests the unfortunate test when our method under instrumentation fails.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void testInvokeFailed() throws Throwable
	{
		// Create our mocks necessary for testing.
		final Date now = new Date();
		final Exception exception = new Exception("Danger Will Robinson.");
		final AOPMetricGatherer aopMetricGatherer = spy(new AOPMetricGatherer());
		final MethodInvocation invocation = mock(MethodInvocation.class);
		final MethodCallStatistic methodStatistic = mock(MethodCallStatistic.class);
		final IStatisticsLogger<MethodCallStatistic> logger = mock(IStatisticsLogger.class);

		doReturn("ASDF").when(aopMetricGatherer).getMethodName(invocation);
		doReturn(methodStatistic).when(aopMetricGatherer).createMethodCallStatistic();
		doReturn(now).when(aopMetricGatherer).getCurrentDate();
		doReturn(logger).when(aopMetricGatherer).getStatisticsLogger();

		Mockito.doThrow(exception).when(invocation).proceed();

		// Run our method under test. This should fail, so let's verify the exception is what we expect.
		try
		{
			aopMetricGatherer.invoke(invocation);
		}
		catch (Exception ex)
		{
			Assert.assertEquals(ex, exception);
		}

		// Verify our method call stack.
		verify(aopMetricGatherer, times(1)).invoke(any(MethodInvocation.class));
		verify(aopMetricGatherer, times(1)).createMethodCallStatistic();
		verify(methodStatistic, times(1)).setOperationName("ASDF");
		verify(aopMetricGatherer, times(1)).getMethodName(invocation);
		verify(methodStatistic, times(1)).setOperationStart(now);
		verify(invocation, times(1)).proceed();
		verify(methodStatistic, times(1)).setOperationCompletion(now);
		verify(aopMetricGatherer, times(2)).getCurrentDate();
		verify(methodStatistic, times(1)).setOperationSuccessful(true);
		verify(aopMetricGatherer, times(1)).getStatisticsLogger();
		verify(methodStatistic, times(1)).setOperationSuccessful(false);
		verify(methodStatistic, times(1)).setMetaData(anyString());
		verify(logger, times(1)).writeStatistic(any(MethodCallStatistic.class));

		// Make sure nothing else happened.
		verifyNoMoreInteractions(aopMetricGatherer, methodStatistic, invocation, logger);
	}
}
