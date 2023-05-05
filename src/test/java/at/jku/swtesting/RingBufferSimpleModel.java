package at.jku.swtesting;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import nz.ac.waikato.modeljunit.RandomTester;
import nz.ac.waikato.modeljunit.StopOnFailureListener;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.CoverageMetric;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;

public class RingBufferSimpleModel implements FsmModel {

	protected static final int CAPACITY = 3;
	protected int size;

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
		size++;
	}	

	public boolean dequeueGuard() {
		return size > 0;
	}

	@Action
	public void dequeue() {
		size--;
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
		// does not change the state
	}

	public boolean dequeFromEmptyBufferGuard(){
		return size == 0;
	}

	@Action
	public void peekFromEmptyBuffer(){
		// does not change the state
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
		Tester tester = new RandomTester(new RingBufferSimpleModel());
		
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
