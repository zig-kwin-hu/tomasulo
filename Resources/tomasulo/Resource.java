package tomasulo;

import com.sun.org.apache.xpath.internal.operations.Bool;

public abstract class Resource {
	
	public Resource(int phasenum, int[] phasecycles)
	{
		this.occupied = 0;
		this.owner = null;
		this.phasenum = phasenum;
		this.phases = new PhaseData[phasenum];
		for (int i = 0; i < phasenum; i++){
			this.phases[i] = new PhaseData(phasecycles[i]);
		}
		this.resultPhase = new PhaseData(0);
	}
	
	public boolean isOccupied()
	{
		return this.occupied > 0;
	}
	
	public boolean tryQuery(Query query)
	{		
		if (this.phases[0].working)
			return false;
		
		Logger.Info("Begin to process query " + query);
		
		this.owner = query.source;
		//this.result = this.computeResult(query);
		this.phases[0].reservationid = query.reservationid;
		this.phases[0].result = this.computeResult(query);
		this.phases[0].countdown = this.phases[0].processCycle;
		this.phases[0].working = true;
		this.occupied ++;
		
		return true;
	}
	
	public PhaseData execute()
	{
		if (this.occupied <= 0)
		{
			return null;
		}
		
		for(int i = 0; i < this.phasenum; i++){
			if(this.phases[i].working){
				if(this.phases[i].countdown > 0){
					this.phases[i].countdown--;
				}
			}
		}
		Boolean isreturn = false;
		if(this.phases[this.phasenum-1].working && (this.phases[this.phasenum-1].countdown==0)){
			isreturn = true;
			this.resultPhase.reservationid = this.phases[this.phasenum-1].reservationid;
			this.resultPhase.result = this.phases[this.phasenum-1].result;
			this.phases[this.phasenum-1].working = false;
			this.phases[this.phasenum-1].countdown = 0;
			this.occupied--;
		}
		for (int i = this.phasenum-2; i >= 0; i--){
			if(this.phases[i].working && (this.phases[i].countdown==0)){
				if(!this.phases[i+1].working){
					this.phases[i+1].working = true;
					this.phases[i+1].reservationid  = this.phases[i].reservationid;
					this.phases[i+1].result = this.phases[i].result;
					this.phases[i+1].countdown = this.phases[i+1].processCycle;
					this.phases[i].working = false;
					this.phases[i].countdown = 0;
				}
			}
		}

		if(isreturn){
			return this.resultPhase;
		}

		return null;
	}	
	
	public double getResult()
	{
		return this.result;
	}
	
	protected abstract double computeResult(Query query);
	
	protected int occupied;
	protected int processCycles;
	protected int countDown;
	protected double result;
	public Component owner;
	public int phasenum;
	public PhaseData[] phases;
	public PhaseData resultPhase;
}
