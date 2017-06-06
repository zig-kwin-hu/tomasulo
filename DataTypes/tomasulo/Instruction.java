package tomasulo;

public class Instruction {
	public enum Type 
	{
		ADDD, SUBD, MULD, DIVD, LD, ST
	}
	
	static public Instruction reg(Type type, int dest, int src1, int src2)
	{
		Instruction ret = new Instruction();
		ret.type = type;
		ret.dest = dest;
		ret.src1 = src1;
		ret.src2 = src2;
		ret.id = count;
		count ++;
		return ret;
	}
	
	static public Instruction mem(Type type, int reg, int addr)
	{
		Instruction ret = new Instruction();
		ret.type = type;
		ret.reg = reg;
		ret.addr = addr;
		ret.id = count;
		count ++;
		return ret;
	}
	
	private Instruction() {}
	
	@Override
	public String toString()
	{
		if (type==Type.LD || type==Type.ST)
		{
			return "Instruction { id=" + id + " type=" + type +" reg=F" + reg + " addr=" + addr + "}";
		}
		else
		{
			return "Instruction { id=" + id + " type=" + type +" dest=F" + dest + " src1=F" + src1 + " src2=F" + src2 + "}";
		}
	}
	
	public Type type;
	public int dest, src1, src2;
	public int reg, addr;
	//new change Huzikun
	static private int count = 0;
	public int id;
}
