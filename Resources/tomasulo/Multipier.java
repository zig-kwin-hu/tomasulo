package tomasulo;

/**
 * Created by huzikun on 2017/6/6.
 */
public class Multipier extends Resource{
    public Multipier(int processCycles) {
        super(processCycles);
    }

    @Override
    protected double computeResult(Query query) {
        ComputeQuery cquery = (ComputeQuery)query;
        return cquery.a * cquery.b;
    }


}
