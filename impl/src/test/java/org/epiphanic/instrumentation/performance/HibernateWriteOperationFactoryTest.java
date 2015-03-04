/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import org.hibernate.SessionFactory;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;


/**
 * Tests our {@link org.epiphanic.instrumentation.performance.HibernateWriteOperationFactory}<p/>
 *
 * @author Greg Feigenson
 */
public final class HibernateWriteOperationFactoryTest
{
	@Test
	public void testCreateWriteOperation()
	{
		// Create our mocks.
		final SessionFactory sessionFactory = mock(SessionFactory.class);
		final HibernateWriteOperationFactory writeOperationFactory = spy(new HibernateWriteOperationFactory());
		final MethodCallStatistic stat = new MethodCallStatistic();

		// Wire them up and call our method under test.
		writeOperationFactory.setSessionFactory(sessionFactory);
		writeOperationFactory.createWriteOperation(stat);

		// Verify the interactions.
		verifyZeroInteractions(sessionFactory);
		verify(writeOperationFactory, times(1)).createWriteOperation(stat);
	}
}
