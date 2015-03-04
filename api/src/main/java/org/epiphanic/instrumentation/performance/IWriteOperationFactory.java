/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;

import java.util.concurrent.Callable;

/**
 * Provides a factory to create write operations. Given a statistic of type T (unbounded), spits out a {@link
 * java.util.concurrent.Callable} wrapped in whatever logic needs to occur to write the statistic to its data store.<p/>
 *
 * @author Greg Feigenson
 */
public interface IWriteOperationFactory<T>
{
	/**
	 * Given an entity to persist, create a {@link java.util.concurrent.Callable} representing the write operation.
	 *
	 * @param statisticsEntity The entity to log.
	 * 
	 * @return A {@link java.util.concurrent.Callable} wrapped around whatever logic it takes to write the given entity.
	 * Will not be <code>null</code>.
	 */
	Callable<Void> createWriteOperation(T statisticsEntity);
}
