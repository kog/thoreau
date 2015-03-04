/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests that our {@link org.epiphanic.instrumentation.performance.GenericAsynchronousLogger} works as expected.<p/>
 *
 * @author Greg Feigenson
 */
public final class GenericAsynchronousLoggerTest
{
	/**
	 * Tests the meat of our asynchronous logger:
	 * {@link org.epiphanic.instrumentation.performance.GenericAsynchronousLogger#writeStatistic(Object)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testWriteStatistic()
	{
		// Create our mocks and objects to test.
		final ExecutorService executorService = mock(ExecutorService.class);
		final IWriteOperationFactory<MethodCallStatistic> writeOperationFactory = mock(IWriteOperationFactory.class);
		final GenericAsynchronousLogger testClass = spy(new GenericAsynchronousLogger<>());
		final MethodCallStatistic stat = new MethodCallStatistic();

		// Wire up our class under test.
		testClass.setMessageProcessor(executorService);
		testClass.setWriteOperationFactory(writeOperationFactory);

		// Do what we came here to do: test writing a stat.
		testClass.writeStatistic(stat);

		// Verify our interactions.
		verify(executorService, times(1)).submit(any(Callable.class));
		verify(writeOperationFactory, times(1)).createWriteOperation(stat);
		verify(testClass, times(1)).writeStatistic(stat);
	}
}
