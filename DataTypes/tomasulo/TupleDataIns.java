package tomasulo;

/**
 * Created by huzikun on 2017/6/6.
 */
public class TupleDataIns {
    public Data data;
    public Instruction instruction;
    public TupleDataIns(Data result, Instruction ins){
        this.data = result;
        this.instruction = ins;
    }
}
