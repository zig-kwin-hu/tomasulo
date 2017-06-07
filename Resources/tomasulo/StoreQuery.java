package tomasulo;

public class StoreQuery extends Query {
	
	public StoreQuery(int id, int index, double value)
	{
		super();
		this.index = index;
		this.value = value;
		this.reservationid = id;
	}
	
	public int index;
	public double value;


}
