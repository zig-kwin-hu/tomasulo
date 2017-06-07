package tomasulo;

import java.util.ArrayList;

public class OpQuene {
	public OpQuene(ArrayList<Component> components)
	{
		this.components = components;	
		this.pendingQ = new ArrayList<Instruction>();
		this.runningQ = new ArrayList<Instruction>();
		this.finishedQ = new ArrayList<Instruction>();
		this.writebackedQ = new ArrayList<Instruction>();
	}
	
	public void EnQueue(Instruction instruction)
	{
		pendingQ.add(instruction);
	}
	
	public void onTick()
	{
		this.tryIssue();
	}
	
	private void tryIssue()
	{		
		for (Instruction inst : this.pendingQ)
		{
			Logger.Debug("Trying to issue " + inst);
			for (Component component : this.components)
			{				
				if (component.ifAccept(inst) && !component.isFull())
				{
					component.accept(inst);
					this.pendingQ.remove(inst);
					this.runningQ.add(inst);
					
					return;
				}
			}
		}	
	}
	public void finishInstruction(Instruction finished){
		boolean found = false;
		for (Instruction running : this.runningQ){
			if (running.id == finished.id){
				found = true;
				this.runningQ.remove(running);
				this.finishedQ.add(running);
				break;
			}
		}

		if (!found){
			Logger.Fatal(finished+"not Found in running");
		}
	}

	public void writeBackInstruction(Instruction writeBacked){
		boolean found = false;
		for (Instruction finished : this.finishedQ){
			if (finished.id == writeBacked.id){
				found = true;
				this.finishedQ.remove(finished);
				this.writebackedQ.add(finished);
				break;
			}
		}
		if(!found){
			Logger.Fatal(writeBacked + "not found in finished");
		}
	}

	public void writedbackInstruction(){

	}
	public ArrayList<Component> components;
	public ArrayList<Instruction> pendingQ;
	public ArrayList<Instruction> runningQ;
	public ArrayList<Instruction> finishedQ;
	public ArrayList<Instruction> writebackedQ;
}
