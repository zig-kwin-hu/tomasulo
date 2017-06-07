package tomasulo;

import java.util.ArrayList;

public class MultiplyDivideComponent extends ComputeComponent {

	public MultiplyDivideComponent(int numReservations, RegFile regFile,
			Resource resource) {
		super(numReservations, regFile, resource);
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
}
