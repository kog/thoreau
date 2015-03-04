/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.concurrent.Callable;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;


/**
 * Produces instances of {@link org.epiphanic.instrumentation.performance.HibernateWriteOperation} callables. This
 * factory takes a injected {@link org.hibernate.SessionFactory} and, given a {@link org.epiphanic.instrumentation.performance.MethodCallStatistic},
 * creates a Hibernate-based write operation.<p/>
 *
 * Think of this like a cross between a DAO and a traditional factory.
 *
 * @author Greg Feigenson
 */
public class HibernateWriteOperationFactory implements IWriteOperationFactory<MethodCallStatistic>
{
	/**
	 * Holds our injected {@link org.hibernate.SessionFactory} to use with our write operations we'll produce.
	 */
	private SessionFactory _sessionFactory;


	/**
	 * Gets our injected {@link org.hibernate.SessionFactory} to use for interacting with our persistence context.
	 *
	 * @return A non-<code>null</code> implementation of {@link org.hibernate.SessionFactory}.
	 */
	@NotNull
	public SessionFactory getSessionFactory()
	{
		return _sessionFactory;
	}


	/**
	 * Sets an implementation of {@link org.hibernate.SessionFactory} to use with our write operations.
	 *
	 * @param sessionFactory A non-<code>null</code> implementation of {@link org.hibernate.SessionFactory}.
	 */
	public void setSessionFactory(@NotNull final SessionFactory sessionFactory)
	{
		_sessionFactory = sessionFactory;
	}


	@Override
	public Callable<Void> createWriteOperation(final MethodCallStatistic statisticsEntity)
	{
		final HibernateWriteOperation<MethodCallStatistic> writeOperation =
				new HibernateWriteOperation<MethodCallStatistic>();
		
		writeOperation.setEntityToWrite(statisticsEntity);
		writeOperation.setSessionFactory(getSessionFactory());

		return writeOperation;
	}
}
