package tomasulo;

import java.util.ArrayList;

/**
 * Created by huzikun on 2017/6/8.
 */
public class MemComponent extends Component {
    public MemComponent(int numReservations, RegFile regFile, Resource resource)
    {
        super(numReservations, regFile, resource);
        this.storeReservations = new ArrayList<Reservation>();
        for (int i=0; i<numReservations; ++i)
        {
            this.storeReservations.add(new Reservation());
        }
        this.memInstSeqs = new ArrayList<MemInstSeq>();
    }
    @Override
    protected Query createQuery(Reservation reservation) {
        if(reservation.instruction.type == Instruction.Type.LD) {
            return new LoadQuery(reservation.destData.reference, reservation.address);
        }else if(reservation.instruction.type == Instruction.Type.ST){
            return new StoreQuery(reservation.destData.reference, reservation.address, reservation.srcData1.value);
        }else{
            Logger.Fatal("Mem reservation incorrect instruction" + reservation.instruction);
        }
        return null;
    }

    @Override
    protected void accept(Reservation reservation) {
        reservation.occupied = true;
        reservation.address = reservation.instruction.addr;
        if(reservation.instruction.type == Instruction.Type.LD) {
            reservation.srcData1 = Data.normal(0);
            reservation.srcData2 = Data.normal(0);
            this.regFile.data[reservation.instruction.reg].waitFor(reservation.destData);
        }else if(reservation.instruction.type == Instruction.Type.ST){
            reservation.srcData1 .waitFor(this.regFile.data[reservation.instruction.reg]);
            reservation.srcData2 = Data.normal(0);
        }
    }
    @Override
    protected boolean ifAccept(Instruction instruction) {
        if (instruction.type == Instruction.Type.LD){
            for (Reservation reservation : this.reservations)
            {
                if (!reservation.isOccupied())
                {
                    return true;
                }
            }
        }else if (instruction.type == Instruction.Type.ST){
            for (Reservation reservation : this.storeReservations)
            {
                if (!reservation.isOccupied())
                {
                    return true;
                }
            }
        }

        return false;
    }
    @Override
    public Boolean isFull()
    {
        for (Reservation reservation : this.reservations)
        {
            if (!reservation.isOccupied())
            {
                return false;
            }
        }
        for (Reservation reservation : this.storeReservations)
        {
            if (!reservation.isOccupied())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void accept(Instruction instruction)
    {
        Logger.Info("Component: get instruction " + instruction);
        if(instruction.type == Instruction.Type.LD) {
            for (Reservation reservation : this.reservations) {
                if (!reservation.isOccupied()) {
                    reservation.instruction = instruction;

                    this.accept(reservation);
                    //this.tryExecute(reservation);
                    this.memInstSeqs.add(new MemInstSeq(this.time, reservation.destData.reference, false, instruction.addr));
                    this.time++;
                    return;
                }
            }
            Logger.Fatal("Component: no available LD reservations");
        }else if(instruction.type == Instruction.Type.ST){
            for (Reservation reservation : this.storeReservations) {
                if (!reservation.isOccupied()) {
                    reservation.instruction = instruction;

                    this.accept(reservation);
                    //this.tryExecute(reservation);
                    this.memInstSeqs.add(new MemInstSeq(this.time, reservation.destData.reference, true, instruction.addr));
                    this.time++;
                    return;
                }
            }
            Logger.Fatal("Component: no available ST reservations");
        }
    }
    @Override
    public ArrayList<TupleDataIns> countinueExecute()
    {
        if (!resource.isOccupied() || resource.owner!=this)
        {
            return null;
        }

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
                for (Reservation r : this.storeReservations){
                    running = r;
                    if (running.destData.reference == result.reservationid){
                        break;
                    }
                }
            }
            if (running.destData.reference != result.reservationid){
                Logger.Fatal("result reservation not found" + result);
            }
            running.destData.value = result.result;
            running.occupied = false;
            running.running = false;

            Logger.Info("Instruction " + running.instruction + " finished, return value = " + running.destData.value);
            ArrayList<TupleDataIns> a = new ArrayList<TupleDataIns>();
            a.add(new TupleDataIns(running.destData, running.instruction));
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
        for(Reservation reservation : this.storeReservations){
            reservation.acceptMessage(q,v);
        }
        MemInstSeq todelete = null;
        for(MemInstSeq seq : this.memInstSeqs){
            if(seq.reservationId == q){
                todelete = seq;
            }
        }
        this.memInstSeqs.remove(todelete);
    }

    public void executeOnTick(){
        for (Reservation reservation : this.reservations){
            if (!reservation.running && reservation.isReady()) {
                boolean noConflict = true;
                for(MemInstSeq seq : this.memInstSeqs){
                    if(seq.reservationId == reservation.destData.reference){
                        break;
                    }
                    if(seq.address == reservation.instruction.addr){
                        if(seq.isStore){
                            noConflict = false;
                        }else if(reservation.instruction.type == Instruction.Type.ST){
                            noConflict = false;
                        }
                    }
                }
                if(noConflict) {
                    this.tryExecute(reservation);
                }
            }
        }
        for (Reservation reservation : this.storeReservations){
            if (!reservation.running && reservation.isReady()) {
                boolean noConflict = true;
                for(MemInstSeq seq : this.memInstSeqs){
                    if(seq.reservationId == reservation.destData.reference){
                        break;
                    }
                    if(seq.address == reservation.instruction.addr){
                        if(seq.isStore){
                            noConflict = false;
                        }else if(reservation.instruction.type == Instruction.Type.ST){
                            noConflict = false;
                        }
                    }
                }
                if(noConflict) {
                    this.tryExecute(reservation);
                }
            }
        }
    }

    public ArrayList<Reservation> getReservations(){
        ArrayList<Reservation> temp = new ArrayList<Reservation>();
        for (Reservation r : this.reservations){
            temp.add(r);
        }
        for(Reservation r : this.storeReservations){
            temp.add(r);
        }
        return temp;
    }

    public void tryExecute(Reservation reservation) {
        if (reservation.isReady())//正在执行的也要？
        {
            Query query = this.createQuery(reservation);
            query.source = this;
            if(this.resource.tryQuery(query)) {
                reservation.running = true;
                //this.runningReservation = reservation;//不应该是tryquery成功后才更该吗？
            }
        }
    }
    protected ArrayList<Reservation> storeReservations;
    public ArrayList<MemInstSeq> memInstSeqs;
    static public int time = 0;

}
