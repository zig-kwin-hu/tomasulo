package tomasulo;

public class Adder extends Resource {

	public Adder(int phasenum, int[] phasecycles) {
		super(phasenum,phasecycles);
	}

	@Override
	protected double computeResult(Query query) {
		ComputeQuery cquery = (ComputeQuery)query;
		return cquery.a + cquery.b;
	}

}
