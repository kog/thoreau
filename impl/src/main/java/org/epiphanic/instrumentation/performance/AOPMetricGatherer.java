/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;


/**
 * An AOP based implementation of {@link org.epiphanic.instrumentation.performance.AbstractMetricGatherer}, uses the
 * Agile Alliance {@link org.aopalliance.intercept.MethodInterceptor} interface to intercept a given method that is
 * being called and to log performance metrics about it, as well as information such as possible failures (as
 * represented by stack traces).<p/>
 *
 * Wire this class into your application via Spring or other IoC configuration to provide pure runtime instrumentation,
 * no modification of source required.<p/>
 *
 * This class is non-final for testing purposes.
 *
 * @author Greg Feigenson
 */
public class AOPMetricGatherer extends AbstractMetricGatherer<MethodCallStatistic> implements MethodInterceptor
{
	/**
	 * Intercepts a method call and computes some statistics about it. If we catch an exception in our interception we log
	 * it and re-throw it, so as to preserve the line numbers for debugging purposes.
	 *
	 * @param methodInvocation The {@link org.aopalliance.intercept.MethodInvocation} to be invoked.
	 *
	 * @return an object representing the result of the method invocation.
	 *
	 * @throws Throwable if there's an exception in the underlying operation.
	 */
	@Override
	public Object invoke(@NotNull final MethodInvocation methodInvocation) throws Throwable
	{
		// We need these for our context.
		Object result = null;
		Exception ex = null;

		final MethodCallStatistic metric = createMethodCallStatistic();

		try
		{
			metric.setOperationName(getMethodName(methodInvocation));
			metric.setOperationStart(getCurrentDate());

			// Call the operation that we've intercepted.
			result = methodInvocation.proceed();

		}
		catch (final Exception e)
		{
			ex = e;
		}
		finally
		{
			metric.setOperationCompletion(getCurrentDate());
			metric.setOperationSuccessful(true);

			// If we caught an exception, make sure we log the failure.
			if (ex != null)
			{
				metric.setOperationSuccessful(false);
				metric.setMetaData(ex.toString());
			}

			// Persist our entity.
			getStatisticsLogger().writeStatistic(metric);

			// Re-throw our exception, if any was caught.
			if (ex != null)
			{
				// It's usually bad practice to throw inside a finally block. But we need this since we're doing some
				// logging on our exceptions via AOP.
				throw ex;
			}
		}

		return result;
	}


	/**
	 * A convenience method to aid in testing - {@link java.lang.reflect.Method} is final and our interceptor returns
	 * a concrete class so we can't mock by interface. <p/>
	 *
	 * This is package-protected for unit testing.
	 *
	 * @param methodInvocation Our {@link org.aopalliance.intercept.MethodInvocation} to get the name of.
	 * @return The name of the method being invoked. Will not be <code>null</code>.
	 */
	@NotNull 
	String getMethodName(@NotNull final MethodInvocation methodInvocation)
	{
		return methodInvocation.getMethod().getName();
	}


	/**
	 * Another convenience method to aid in testing. Creates an instance of our {@link org.epiphanic.instrumentation.performance.MethodCallStatistic}
	 * so that we can instrument it with mocks.<p/>
	 *
	 * This is package-protected for unit testing. 
	 *
	 * @return A non-<code>null</code> {@link org.epiphanic.instrumentation.performance.MethodCallStatistic} instance.
	 */
	@NotNull
	MethodCallStatistic createMethodCallStatistic()
	{
		return new MethodCallStatistic();		
	}


	/**
	 * Another convenience method to aid in testing. Returns the current {@link java.util.Date}.<p/>
	 *
	 * This is package-protected for unit testing.
	 *
	 * @return The current date. Will not be <code>null</code>.
	 */
	@NotNull
	Date getCurrentDate()
	{
		return new Date();	
	}
}
