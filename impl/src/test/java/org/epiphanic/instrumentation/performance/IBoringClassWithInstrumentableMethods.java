/**
 * Thoreau: a demonstration library for performance instrumentation.
 *
 * This source is licensed under the MIT license. Please see the distributed license.txt for details.
 */
package org.epiphanic.instrumentation.performance;


/**
 * Provides an interface for {@link org.epiphanic.instrumentation.performance.BoringClassWithInstrumentableMethods} so
 * we can proxy by interface for our interception.<p/>
 *
 * @author Greg Feigenson
 */
public interface IBoringClassWithInstrumentableMethods
{
	/**
	 * A simple, no-op method that does... absolutely nothing. Should be the simplest possible case.
	 */
	void voidReturningMethodThatDoesNothing();

	/**
	 * A recursive implementation of the fibonacci sequence. There are two implementations (recursive and iterative) to
	 * ensure that our instrumentation doesn't record lots of spam on recursive methods - both solutions should look the
	 * same in terms of our instrumentation.
	 *
	 * @param n The index of the element within the fibonacci sequence to compute to.
	 *
	 * @return The computation of index n within the fibonacci sequence.
	 */
	int fibonacciSequenceRecursive(int n);

	/**
	 * An iterative implementation of the fibonacci sequence. This contrasts with our recursive solution at {@link
	 * #fibonacciSequenceRecursive(int)}.
	 *
	 * @param n The index of the element within the fibonacci sequence to compute to.
	 *
	 * @return The computation of index n within the fibonacci sequence.
	 */
	int fibonacciSequenceIterative(int n);

	/**
	 * A method that takes about 5 seconds to run and returns some random object, in this case a {@link java.util.Date}.
	 *
	 * @return Some random object.
	 *
	 * @throws Exception If the sleep call in the method is aborted. It won't be.
	 */
	Object longRunningObjectCreatingMethod() throws Exception;

	/**
	 * A method that throws an exception during execution, allowing us to test what happens when our instrumentation runs
	 * across something that fails.
	 *
	 * @throws Exception Every execution is guaranteed to throw an Exception.
	 */
	void exceptionThrowingMethod() throws Exception;
}
