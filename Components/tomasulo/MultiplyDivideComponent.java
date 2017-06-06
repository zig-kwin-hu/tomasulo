package tomasulo;

import java.util.ArrayList;

public class MultiplyDivideComponent extends ComputeComponent {

	public MultiplyDivideComponent(int numReservations, RegFile regFile,
			Resource resource, Resource resource2) {
		super(numReservations, regFile, resource);
		this.divideResource = resource2;
	}

	@Override
	protected boolean ifAccept(Instruction instruction) {
		return (instruction.type == Instruction.Type.MULD || instruction.type == Instruction.Type.DIVD);
	}

	@Override
	protected Query createQuery(Reservation reservation) {
		return new ComputeQuery(reservation.srcData1.value, reservation.srcData2.value, reservation.instruction.type == Instruction.Type.DIVD);
	}

	@Override
	public ArrayList<TupleDataIns> countinueExecute()
	{
		if ((!resource.occupied || resource.owner!=this) && (!divideResource.occupied || divideResource.owner!=this))
		{
			return null;
		}
		if(resource.occupied) {
			resource.execute();
		}
		if (!resource.occupied)
		{
			this.runningReservation.destData.value = resource.getResult();
			this.runningReservation.occupied = false;

			Logger.Info("Instruction " + this.runningReservation.instruction + " finished, return value = " + this.runningReservation.destData.value);
			ArrayList<TupleDataIns> a = new ArrayList<TupleDataIns>();
			a.add(new TupleDataIns(this.runningReservation.destData, this.runningReservation.instruction));
			return a;
		}

		if(divideResource.occupied){
			divideResource.execute();
		}

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

	@Override
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

	public Resource divideResource;
}
