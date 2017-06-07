package tomasulo;

public class LoadQuery extends Query {
	
	public LoadQuery(int id, int index)
	{
		super();
		this.index = index;
		this.reservationid = id;
	}
	
	public int index;
}
