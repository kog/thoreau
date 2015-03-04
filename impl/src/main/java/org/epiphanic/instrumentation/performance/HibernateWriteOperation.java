/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.concurrent.Callable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;


/**
 * A Hibernate-based write operation, to be produced by an implementation of {@link
 * org.epiphanic.instrumentation.performance.IWriteOperationFactory}. Given an entity of type T, unbounded, attempts to
 * write it to our configured Hibernate {@link org.hibernate.SessionFactory}.<p/>
 *
 * @author Greg Feigenson
 */
public final class HibernateWriteOperation<T> implements Callable<Void>
{
	/**
	 * Holds the entity that we wish to write to our persistent repository.
	 */
	private T _entityToWrite;

	/**
	 * Holds our injected {@link org.hibernate.SessionFactory} that we use for persisting our entities.
	 */
	private SessionFactory _sessionFactory;


	/**
	 * Returns our injected entity that we wish to write to a persistent source.
	 *
	 * @return An entity to persist, will not be <code>null</code>.
	 */
	@NotNull
	public T getEntityToWrite()
	{
		return _entityToWrite;
	}


	/**
	 * Sets our entity that we wish to write to a persistent source.
	 *
	 * @param entityToWrite An entity to write to a persistent source. Must not be <code>null</code>.
	 */
	public void setEntityToWrite(@NotNull final T entityToWrite)
	{
		_entityToWrite = entityToWrite;
	}


	/**
	 * Gets our injected instance of {@link SessionFactory} to use for dealing with anything we've configured for
	 * Hibernate.
	 *
	 * @return A non-<code>null</code> instance of {@link org.hibernate.SessionFactory}.
	 */
	@NotNull
	public SessionFactory getSessionFactory()
	{
		return _sessionFactory;
	}


	/**
	 * Sets an instance of {@link org.hibernate.SessionFactory} to use for our Hibernate operations.
	 *
	 * @param sessionFactory A non-<code>null</code>, fully configured instance of {@link org.hibernate.SessionFactory}.
	 */
	public void setSessionFactory(@NotNull final SessionFactory sessionFactory)
	{
		_sessionFactory = sessionFactory;
	}


	/**
	 * The meat of the class - our implementation of {@link java.util.concurrent.Callable}. In this write operation, we
	 * write our entity set in {@link #setEntityToWrite(Object)} to whatever's configured via Hibernate in our {@link
	 * org.hibernate.SessionFactory}.
	 */
	@Override
	public Void call() throws Exception
	{
		// TODO: GregF 04/11/2010 - This is wrong, fix it after preliminary commit.

		final Session session = getSessionFactory().openSession();
		session.save(getEntityToWrite());
		session.flush();
		session.close();

		return null;
	}

}
