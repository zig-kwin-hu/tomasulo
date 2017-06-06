package tomasulo;

import java.util.ArrayList;

public abstract class Component {
	
	public Component(int numReservations, RegFile regFile, Resource resource)
	{
		this.reservations = new ArrayList<Reservation>();
		
		for (int i=0; i<numReservations; ++i)
		{
			this.reservations.add(new Reservation());
		}
		
		this.regFile = regFile;
		this.resource = resource;
	}
		
	public Boolean isFull()
	{
		for (Reservation reservation : this.reservations)
		{
			if (!reservation.isOccupied())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public void accept(Instruction instruction)
	{
		Logger.Info("Component: get instruction " + instruction);
		
		for (Reservation reservation : this.reservations)
		{
			if (!reservation.isOccupied())
			{
				reservation.instruction = instruction;
								
				this.accept(reservation);
				//this.tryExecute(reservation);
				
				return;
			}
		}
		
		Logger.Fatal("Component: no available reservations");
	}
	
	public ArrayList<TupleDataIns> countinueExecute()
	{	
		if (!resource.occupied || resource.owner!=this)
		{
			return null;
		}
			
		resource.execute();
		
		if (!resource.occupied)
		{
			this.runningReservation.destData.value = resource.getResult();
			this.runningReservation.occupied = false;
			
			Logger.Info("Instruction " + this.runningReservation.instruction + " finished, return value = " + this.runningReservation.destData.value);
			ArrayList<TupleDataIns> a = new ArrayList<TupleDataIns>();
			a.add(new TupleDataIns(this.runningReservation.destData, this.runningReservation.instruction));
			return a;
		}
		
		return null;
	}		
	
	public void acceptMessage(int q, double v)
	{
		for (Reservation reservation : this.reservations)
		{
			reservation.acceptMessage(q, v);
			
			//this.tryExecute(reservation);//正在执行的也要？
		}
	}

	public void executeOnTick(){
		for (Reservation reservation : this.reservations){
			this.tryExecute(reservation);
		}
	}
	public ArrayList<Reservation> getReservations(){
		return this.reservations;
	}

	public void tryExecute(Reservation reservation) {
		if (reservation.isReady())//正在执行的也要？
		{
			Query query = this.createQuery(reservation);
			query.source = this;
			if(this.resource.tryQuery(query)) {
				this.runningReservation = reservation;//不应该是tryquery成功后才更该吗？
			}
		}		
	}
	
	protected abstract Query createQuery(Reservation reservation);	
	protected abstract void accept(Reservation reservation);
	protected abstract boolean ifAccept(Instruction instruction);
	
	protected ArrayList<Reservation> reservations;
	protected Reservation runningReservation;
	protected RegFile regFile;
	protected Resource resource;
}
