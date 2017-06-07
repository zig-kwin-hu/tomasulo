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
		return new ComputeQuery(reservation.destData.reference, reservation.srcData1.value, reservation.srcData2.value, reservation.instruction.type == Instruction.Type.DIVD);
	}

	@Override
	public ArrayList<TupleDataIns> countinueExecute()
	{
		if ((!resource.isOccupied() || resource.owner!=this) && (!divideResource.isOccupied() || divideResource.owner!=this))
		{
			return null;
		}
		ArrayList<TupleDataIns> a = new ArrayList<TupleDataIns>();
		if(resource.isOccupied() && resource.owner == this) {
			PhaseData result = resource.execute();
			if (result != null)
			{
				Reservation running = null;
				for (Reservation r : this.reservations){
					running = r;
					if (running.destData.reference == result.reservationid){
						break;
					}
				}

				if (running.destData.reference != result.reservationid){
					Logger.Fatal("result's reservation not found" + result);
				}

				running.destData.value = result.result;
				running.occupied = false;
				running.running = false;

				Logger.Info("Instruction " + running.instruction + " finished, return value = " + running.destData.value);
				a.add(new TupleDataIns(running.destData, running.instruction));
			}
		}
		if(this.divideResource.isOccupied() && this.divideResource.owner == this) {
			PhaseData result = this.divideResource.execute();
			if (result != null)
			{
				Reservation running = null;
				for (Reservation r : this.reservations){
					running = r;
					if (running.destData.reference == result.reservationid){
						break;
					}
				}

				if (running.destData.reference != result.reservationid){
					Logger.Fatal("result's reservation not found" + result);
				}

				running.destData.value = result.result;
				running.occupied = false;
				running.running = false;

				Logger.Info("Instruction " + running.instruction + " finished, return value = " + running.destData.value);
				a.add(new TupleDataIns(running.destData, running.instruction));
			}
		}
		if (a.size() > 0){
			return a;
		}
		return null;
	}

	@Override
	public void tryExecute(Reservation reservation) {
		if (reservation.isReady())//正在执行的也要？
		{
			ComputeQuery query = (ComputeQuery) this.createQuery(reservation);
			query.source = this;
			if(query.isDiv){
				if(this.divideResource.tryQuery(query)) {
					reservation.running = true;//不应该是tryquery成功后才更该吗？
				}
			}else{
				if(this.resource.tryQuery(query)) {
					reservation.running = true;//不应该是tryquery成功后才更该吗？
				}
			}

		}
	}

	public Resource divideResource;
}
