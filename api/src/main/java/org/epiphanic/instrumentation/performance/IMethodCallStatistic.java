/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;

import java.util.Date;

/**
 * Provides an interface for a method call statistic POJO. Stores information about the amount of time it took to run
 * a given method, as well as whether or not the operation was successful. Metadata and user information is also
 * available, if optional.<p/>
 *
 * @author Greg Feigenson
 */
public interface IMethodCallStatistic
{
	/**
	 * Gets the unique identifier for the statistic.
	 *
	 * @return The unique identifier for the statistic item.
	 */
	Long getId();

	/**
	 * Gets an optional user ID of the user that called the method.
	 *
	 * @return The ID of the user calling the method, if known and available, else <code>null</code>.
	 */
	Long getUserId();

	/**
	 * Gets the name of the operation.
	 *
	 * @return The name of the operation. Will not be <code>null</code>.
	 */
	String getOperationName();

	/**
	 * Gets the starting {@link java.util.Date} for the operation.
	 *
	 * @return The {@link java.util.Date} the operation started. Will not be <code>null</code>.
	 */
	Date getOperationStart();

	/**
	 * Gets the ending {@link java.util.Date} for the operation.
	 *
	 * @return The {@link java.util.Date} the operation completed. Will not be <code>null</code>.
	 */
	Date getOperationCompletion();

	/**
	 * Gets an optional string of metadata about the operation. This may contain information such as an amalgamated
	 * stack trace (if {@link #isOperationSuccessful()} indicates failure) or custom metadata.
	 *
	 * @return An optional bit of metadata, else <code>null</code>. May contain a stack trace if the method under
	 * instrumentation failed, as indicated by {@link #isOperationSuccessful()}.
	 */
	String getMetaData();

	/**
	 * Indicates whether or not the operation was successful, as determined by whether or not an exception was thrown
	 * during the execution of the method under instrumentation.
	 *
	 * @return <code>True</code> if the method call under instrumentation succeeded, else <code>false</code>.
	 */
	boolean isOperationSuccessful();
}
