/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.concurrent.ExecutorService;

import org.jetbrains.annotations.NotNull;


/**
 * Given an entity of type T, unbounded, wires together all the pieces necessary to log it to a persistent context: when
 * {@link #writeStatistic(Object)} is called, it creates a write operation via the injected {@link
 * org.epiphanic.instrumentation.performance.IWriteOperationFactory} and immediately passes it to the message processor
 * {@link java.util.concurrent.ExecutorService} that is also injected.<p/>
 *
 * The effect of this is that a delayed write operation is queued and persisted at a nondeterministic point in time, in
 * a non-blocking manner.
 *
 * @author Greg Feigenson
 */
public class GenericAsynchronousLogger<T> implements IStatisticsLogger<T>
{
	/**
	 * Holds our {@link java.util.concurrent.ExecutorService} that will consume our callables created by our {@link
	 * org.epiphanic.instrumentation.performance.IWriteOperationFactory}.
	 */
	private ExecutorService _messageProcessor;

	/**
	 * Holds our {@link org.epiphanic.instrumentation.performance.IWriteOperationFactory} that creates asynchronous write
	 * operations for our given statistic.
	 */
	private IWriteOperationFactory<T> _writeOperationFactory;


	/**
	 * Gets our injected {@link java.util.concurrent.ExecutorService} for consuming our write operation callables.
	 *
	 * @return An instance of {@link java.util.concurrent.ExecutorService} to consume callables. Must not be
	 *         <code>null</code>.
	 */
	@NotNull
	public ExecutorService getMessageProcessor()
	{
		return _messageProcessor;
	}


	/**
	 * Sets the {@link java.util.concurrent.ExecutorService} implementation to use when consuming our write operation
	 * callables.
	 *
	 * @param messageProcessor A non-<code>null</code> implementation of {@link java.util.concurrent.ExecutorService}.
	 */
	public void setMessageProcessor(@NotNull final ExecutorService messageProcessor)
	{
		_messageProcessor = messageProcessor;
	}


	/**
	 * Gets the injected instance of {@link org.epiphanic.instrumentation.performance.IWriteOperationFactory} to use when
	 * generating asynchronous write operations. Must not be <code>null</code>.
	 *
	 * @return A non-<code>null</code> implementation of {@link org.epiphanic.instrumentation.performance.IWriteOperationFactory}.
	 */
	@NotNull
	public IWriteOperationFactory<T> getWriteOperationFactory()
	{
		return _writeOperationFactory;
	}


	/**
	 * Sets the {@link org.epiphanic.instrumentation.performance.IWriteOperationFactory} implementation to use when
	 * creating asynchronous callbables containing our write operations.
	 *
	 * @param writeOperationFactory A non-<code>null</code> implementation of {@link org.epiphanic.instrumentation.performance.IWriteOperationFactory}.
	 */
	public void setWriteOperationFactory(@NotNull final IWriteOperationFactory<T> writeOperationFactory)
	{
		_writeOperationFactory = writeOperationFactory;
	}


	@Override
	public void writeStatistic(@NotNull final T statistic)
	{
		getMessageProcessor().submit(getWriteOperationFactory().createWriteOperation(statistic));
	}
}
