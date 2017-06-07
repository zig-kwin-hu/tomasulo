package tomasulo;

/**
 * Created by huzikun on 2017/6/6.
 */
public class PhaseData {
    public boolean working;
    public double result;
    public int countdown;
    public int reservationid;
    public int processCycle;
    public PhaseData(){
        this.working = false;
        this.result = 0;
        this.countdown = 0;
        this.reservationid = 0;
    }
    public PhaseData(int processcycle){
        this.working = false;
        this.result = 0;
        this.countdown = 0;
        this.reservationid = 0;
        this.processCycle = processcycle;
    }
    @Override
    public String toString(){
        return "working = " + working + " result = " + result + " reservationid = " + reservationid +
                " countdown = " + countdown + " cycle = " + processCycle;
    }
}
