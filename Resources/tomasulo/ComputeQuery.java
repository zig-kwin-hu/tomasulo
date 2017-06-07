package tomasulo;

public class ComputeQuery extends Query {
	
	public ComputeQuery(int id, double a, double b)
	{
		super();
		this.a = a;
		this.b = b;
		this.reservationid = id;
	}
	
	public ComputeQuery(int id, double a, double b, boolean isDiv)
	{
		super();
		this.a = a;
		this.b = b;
		this.isDiv = isDiv;
		this.reservationid = id;
	}
	
	public double a, b;
	public boolean isDiv;

}
