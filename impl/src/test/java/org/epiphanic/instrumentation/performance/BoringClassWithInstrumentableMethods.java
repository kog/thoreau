/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


import java.util.Date;


/**
 * This rather boring class has a few methods on it to show-case the instrumentation.<p/>
 *
 * @author Greg Feigenson
 */
public class BoringClassWithInstrumentableMethods implements IBoringClassWithInstrumentableMethods
{
	@Override
	public void voidReturningMethodThatDoesNothing()
	{
		// This method is about as useful as a chocolate fire-guard.
		return;
	}

	// [GregF 04/11/2010] - I shamelessly stole these fibonacci algorithms off the Literate Programming wiki and did a
	// [GregF 04/11/2010] - little bit of cleanup. These are not intended to be the most optimal implementations of the
	// [GregF 04/11/2010] - algorithms - in fact, it helps that they're not... need to generate run time somehow ;)

	@Override
	public int fibonacciSequenceRecursive(final int n)
	{
		return n < 2 ? n : fibonacciSequenceRecursive(n - 1) + fibonacciSequenceRecursive(n - 2);
	}


	@Override
	public int fibonacciSequenceIterative(final int n)
	{
		int prev1 = 0, prev2 = 1;

		for (int i = 0; i < n; i++)
		{
			final int savePrev1 = prev1;
			prev1 = prev2;
			prev2 = savePrev1 + prev2;
		}

		return prev1;
	}


	@Override
	public Object longRunningObjectCreatingMethod() throws Exception
	{
		final Date object = new Date();
		Thread.sleep(5000);
		return object;
	}


	@Override
	public void exceptionThrowingMethod() throws Exception
	{
		throw new Exception("I am the very modern model of a modern Major-General.");
	}
}
