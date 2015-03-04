/*
 * org-epiphanic-libraries-performance
 * Copyright 2010 Epiphanic Networks
 *
 * This source is licensed under the terms of the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import org.jetbrains.annotations.NotNull;


/**
 * An abstract class that allows injection of a {@link org.epiphanic.instrumentation.performance.IStatisticsLogger}
 * implementation. Can be extended and wired up in any number of ways, and is the class that you use for instrumenting
 * your target application.<p/>
 *
 * @author Greg Feigenson
 */
public abstract class AbstractMetricGatherer<T>
{
	/**
	 * Holds an instance of {@link org.epiphanic.instrumentation.performance.IStatisticsLogger} to use when writing
	 * statistics to our persistent data store.
	 */
	private IStatisticsLogger<T> _logger;


	/**
	 * Gets our injected {@link org.epiphanic.instrumentation.performance.IStatisticsLogger} to use when logging
	 * statistics. Will not be <code>null</code>.
	 *
	 * @return A non-<code>null</code> instance of {@link org.epiphanic.instrumentation.performance.IStatisticsLogger}.
	 */
	@NotNull
	public IStatisticsLogger<T> getStatisticsLogger()
	{
		return _logger;
	}


	/**
	 * Sets an instance of {@link org.epiphanic.instrumentation.performance.IStatisticsLogger} to use when logging our
	 * statistics to a persistent store. Must not be <code>null</code>.
	 *
	 * @param logger A valid, non-<code>null</code> implementation of {@link org.epiphanic.instrumentation.performance.IStatisticsLogger}.
	 */
	public void setStatisticsLogger(@NotNull final IStatisticsLogger<T> logger)
	{
		_logger = logger;
	}
}
