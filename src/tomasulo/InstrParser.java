package tomasulo;

/**
 * Created by guotata on 2017/6/6.
 */
public class InstrParser {
    static public Instruction parseInstruction(String textInstruction) throws Exception{
        String[] dest_src = textInstruction.split(" ");
        String[] regs;
        switch (dest_src[0]) {
                case "ADDD":case "SUBD":case "MULD":case "DIVD":
                    regs = dest_src[1].split(",");
                    if (regs.length == 3) {
                        return Instruction.reg(Instruction.Type.valueOf(dest_src[0]),
                                regname2Int(regs[0]), regname2Int(regs[1]), regname2Int(regs[2]));
                    }
                    else{
                        throw new Exception();
                    }
                case "LD":case "ST":
                    regs = dest_src[1].split(",");
                    if (regs.length == 2){
                        return Instruction.mem(Instruction.Type.valueOf(dest_src[0]),
                                regname2Int(regs[0]), Integer.parseInt(regs[1]));
                    }
                    else{
                        throw new Exception();
                    }
                default:
                    throw new Exception();
        }
    }

    protected static int regname2Int(String name){
        return Integer.parseInt(name.substring(1));
    }

}

