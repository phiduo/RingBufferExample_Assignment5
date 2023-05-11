package at.jku.swtesting;

import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RingBufferSimpleModelWithAdapter implements FsmModel {

	protected static final int CAPACITY = 3;
	protected int size;
	private RingBuffer<String> ringBuffer = new RingBuffer<>(CAPACITY);

	public Object getState() {
		if (size == 0) {
			return "EMPTY";
		} else if (size == CAPACITY) {
			return "FULL";
		} else if ((size > 0) && (size < CAPACITY)) {
			return "FILLED";
		} else return "ERROR_UNEXPECTED_MODEL_STATE";
	}

	public void reset(boolean testing) {
		size = 0;
	}

	public boolean enqueueGuard() {
		return size < CAPACITY;
	}

	@Action
	public void enqueue() {

		ringBuffer.enqueue("test #" + size );
		size++;
	}	

	public boolean dequeueGuard() {
		return size > 0;
	}

	@Action
	public void dequeue() {
		ringBuffer.dequeue();
		size--;
		assertEquals(size , ringBuffer.size());
	}

	@Action
	public void peek(){
		// returns the first element, but does not change the state
	}

	public boolean peekGuard(){
		return size > 0;
	}

	@Action
	public void dequeFromEmptyBuffer(){
		Exception exception = assertThrows(RuntimeException.class, () -> ringBuffer.dequeue());

		String expectedMessage = "Empty ring buffer.";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));	}

	public boolean dequeFromEmptyBufferGuard(){
		return size == 0;
	}

	@Action
	public void peekFromEmptyBuffer(){
		Exception exception = assertThrows(RuntimeException.class, () -> ringBuffer.peek());

		String expectedMessage = "Empty ring buffer.";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	public boolean peekFromEmptyBufferGuard(){
		return size == 0;
	}

	/* These actions do not change the state nor need a guard, so they have been omitted
	@Action
	public void size(){
		// returns the size, but does not change the state
		// no guard needed
	}

	@Action
	public void capacity(){
		// returns the capacity, but does not change the state
		// no guard needed
	}

	@Action
	public void isEmpty(){
		// does not change the state
		// no guard needed
	}

	@Action
	public void isFull(){
		// does not change the state
		// no guard needed
	}
	 */

	public static void main(String[] args) {
		Tester tester = new RandomTester(new RingBufferSimpleModelWithAdapter());
		
		tester.buildGraph();
		tester.addListener(new VerboseListener());
		tester.addListener(new StopOnFailureListener());
        tester.addCoverageMetric(new ActionCoverage());
        tester.addCoverageMetric(new StateCoverage());
		tester.addCoverageMetric(new TransitionCoverage());
		
		tester.generate(10);

		tester.printCoverage();
	}

}
