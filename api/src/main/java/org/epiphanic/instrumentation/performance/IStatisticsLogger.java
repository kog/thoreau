/*
 * org-epiphanic-libraries-performance
 * Copyright 2010 Epiphanic Networks
 *
 * This source is licensed under the terms of the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


/**
 * Provides an interface for the statistics logger, the meat of the performance instrumentation.<p/>
 *
 * @author Greg Feigenson
 */
public interface IStatisticsLogger<T>
{
	/**
	 * Given a type, T (unbounded), will attempt to write the statistic to some data store.
	 *
	 * @param statistic The statistic to log. Unbounded type. Must not be <code>null</code>.
	 */
	void writeStatistic(T statistic);
}
