package tomasulo;

import java.util.ArrayList;
import java.util.HashMap;

public class CPU {
	
	public CPU()
	{
		// Resources
		Adder adder = new Adder(2);
		MultiplyDivider divider1 = new MultiplyDivider(10, 40);
		this.memory = new Memory(2, 4096);
		
		// Reg
		this.regFile = new RegFile(16);
		
		// Component

		this.components = new ArrayList<Component>();
		this.components.add(new AddSubtractComponent(3, regFile, adder));
		this.components.add(new MultiplyDivideComponent(2, regFile, divider1));
		this.components.add(new LoadComponent(3, this.regFile, memory));
		this.components.add(new StoreComponent(3, this.regFile, memory));

		// OpQueue
		this.opQueue = new OpQuene(components);
		this.changes = new ArrayList<Data>();
		this.finishedInsts = new ArrayList<Instruction>();
	}
	
	/**
	 * Get a list of pending instructions.
	 * 
	 * @return A list of Instruction, from old to new.
	 * @see	Instruction
	 */
	public ArrayList<Instruction> getPendingInstructionQueue()
	{
		return this.opQueue.pendingQ;
	}
	
	/**
	 * Get a list of running instructions.
	 * 
	 * @return A list of Instruction, from old to new.
	 * @see	Instruction
	 */
	public ArrayList<Instruction> getRunningInstructionQueue()
	{
		return this.opQueue.runningQ;
	}

	//Huzikun 新增接口
	public ArrayList<Instruction> getFinishedInstructionQueue()
	{
		return this.opQueue.finishedQ;
	}

	public ArrayList<Instruction> getWriteBackedInstructionQueue()
	{
		return this.opQueue.writebackedQ;
	}

	/**
	 * Add an instruction to pending instruction queue.
	 * 
	 * @param The instruction to be added.
	 */
	public void addInstruction(Instruction instruction)
	{
		this.opQueue.EnQueue(instruction);
	}

	/**
	 * Make the CPU time elapse by 1 cycle.
	 */
	public void onTick()
	{
		Logger.Info("Tick");
		
		// Issue an instruction
		this.opQueue.onTick();
		for (Component component: this.components){
			component.executeOnTick();
		}

		//Huzikun writeback
		for (Data data : this.changes)
		{
			for (Component component : this.components)
			{
				component.acceptMessage(data.reference, data.value);
			}
			for (Data data2 : this.regFile.data)
			{
				Logger.Debug(""+data);
				data2.acceptMessage(data.reference, data.value);
			}
		}
		for (Instruction inst : finishedInsts){
			this.opQueue.writeBackInstruction(inst);
		}

		// Let components run
		this.changes.clear();
		this.finishedInsts.clear();
		for (Component component : this.components)
		{
			ArrayList<TupleDataIns> tupleArray = component.countinueExecute();
			
			if (tupleArray!=null)
			{
				for (TupleDataIns tuple : tupleArray) {
					this.changes.add(tuple.data);
					this.finishedInsts.add(tuple.instruction);
				}
			}
		}

		//修改执行完成队列
		for (Instruction inst : finishedInsts){
			this.opQueue.finishInstruction(inst);
		}

	}
	
	public double getMemory(int index)
	{
		return this.memory.data[index];
	}
	
	public void setMemory(int index, double value)
	{
		this.memory.data[index] = value;
	}
	
	public ArrayList<Reservation> getReservations(){
		ArrayList<Reservation> reservations = new ArrayList<Reservation>();
		for (Component component : this.components)
		{
			reservations.addAll(component.getReservations());
		}
		return reservations;
	}
	
	public Data getReg(int index)
	{
		return this.regFile.data[index];
	}
	
	public void setReg(int index, Data data)
	{
		this.regFile.data[index] = data;
	}
	
	public void setReg(int index, double value)
	{
		this.regFile.data[index].value = value;
	}
	
	public static void main(String args[])
	{
		CPU cpu = new CPU();

		cpu.setMemory(1, 1992.23);
		cpu.setMemory(2, 3217.0);
		cpu.addInstruction(Instruction.mem(Instruction.Type.LD, 1, 1));
		cpu.addInstruction(Instruction.mem(Instruction.Type.LD, 2, 2));
		cpu.addInstruction(Instruction.reg(Instruction.Type.ADDD, 3, 1, 2));

		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();
		cpu.onTick();

		Logger.Debug("Load result (should be ???) = " + cpu.getReg(3));


	}
	
	private OpQuene opQueue;
	private ArrayList<Component> components;
	private RegFile regFile;
	private Memory memory;
	public ArrayList<Data> changes;
	public ArrayList<Instruction> finishedInsts;
}
