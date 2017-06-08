package tomasulo;

/**
 * Created by huzikun on 2017/6/8.
 */
public class MemInstSeq {
    public int time;
    public int reservationId;
    public boolean isStore;
    public int address;
    public MemInstSeq(){
        time = -1;
        reservationId = -1;
        isStore = false;
        address = -1;
    }
    public MemInstSeq(int time, int reservationId, boolean isStore, int address){
        this.time = time;
        this.reservationId = reservationId;
        this.isStore = isStore;
        this.address = address;
    }

}
