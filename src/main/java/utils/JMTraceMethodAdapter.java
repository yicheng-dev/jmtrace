package utils;

import org.objectweb.asm.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JMTraceMethodAdapter extends MethodVisitor {
    private MethodVisitor mv;

    private boolean inStatic = false;
    private String curOwner;
    private String arrayType;


    public JMTraceMethodAdapter(MethodVisitor mv) {
        super(Opcodes.ASM7, mv);
        this.mv = mv;
    }

    private void convertToObject(String desc) {
        switch (desc) {
            case "I": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false); break;
            case "Z": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false); break;
            case "C": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false); break;
            case "B": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false); break;
            case "S": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false); break;
            case "F": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false); break;
            case "L": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(L)Ljava/lang/Long;", false); break;
            case "D": mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false); break;
        }
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case Opcodes.IASTORE:
            case Opcodes.CASTORE:
            case Opcodes.BASTORE:
            case Opcodes.SASTORE:
            case Opcodes.LASTORE:
            case Opcodes.FASTORE:
            case Opcodes.DASTORE:
            case Opcodes.AASTORE:
                /*
                 * [member index value] -> [member index value index value] -> [member index value index] -> ... -> [member index value index object rw name]
                 * long/double: [member index value1 value2] -> [member index value1 value2
                 */
                if (opcode == Opcodes.LASTORE || opcode == Opcodes.DASTORE) {
                    mv.visitInsn(Opcodes.DUP2_X2);
                    mv.visitInsn(Opcodes.POP2);
                    mv.visitInsn(Opcodes.DUP);
                }
                else {
                    mv.visitInsn(Opcodes.DUP2);
                    mv.visitInsn(Opcodes.POP);
                }
                if (inStatic) mv.visitLdcInsn(curOwner);
                else mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_2);
                mv.visitLdcInsn("");
                mv.visitLdcInsn(arrayType);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getInternalName(TraceInstrClass.class),
                        "log",
                        "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                        false);
                if (opcode == Opcodes.LASTORE || opcode == Opcodes.DASTORE) {
                    mv.visitInsn(Opcodes.DUP2_X2);
                    mv.visitInsn(Opcodes.POP2);
                }
                break;
            case Opcodes.IALOAD:
            case Opcodes.CALOAD:
            case Opcodes.BALOAD:
            case Opcodes.SALOAD:
            case Opcodes.LALOAD:
            case Opcodes.FALOAD:
            case Opcodes.DALOAD:
            case Opcodes.AALOAD:
                /*
                 * [member index] -> [member index index] -> ... -> [member index index object rw name]
                 */
                mv.visitInsn(Opcodes.DUP);
                if (inStatic) mv.visitLdcInsn(curOwner);
                else mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitInsn(Opcodes.ICONST_1);
                mv.visitLdcInsn("");
                mv.visitLdcInsn(arrayType);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getInternalName(TraceInstrClass.class),
                        "log",
                        "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                        false);
                break;
        }
        mv.visitInsn(opcode);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (owner.startsWith("java") || owner.startsWith("sun") || owner.startsWith("com")) {
            mv.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        // System.out.println("opcode: " + opcode + "\towner: " + owner + "\tname: " + name + "\tdesc: " + desc);
        switch (opcode) {
            case Opcodes.GETFIELD:
            case Opcodes.GETSTATIC:
            case Opcodes.PUTFIELD:
            case Opcodes.PUTSTATIC:
                if (desc.startsWith("[")) {
                    if (opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC) inStatic = true;
                    else inStatic = false;
                    curOwner = owner;
                    arrayType = desc.substring(desc.indexOf("[") + 1).replace("/", ".");
                }
                mv.visitInsn(Opcodes.ICONST_M1);
                if (opcode == Opcodes.GETFIELD || opcode == Opcodes.PUTFIELD) mv.visitVarInsn(Opcodes.ALOAD, 0);
                else mv.visitLdcInsn(owner);
                if (opcode == Opcodes.GETFIELD || opcode == Opcodes.GETSTATIC) mv.visitInsn(Opcodes.ICONST_1);
                else mv.visitInsn(Opcodes.ICONST_2);
                mv.visitLdcInsn(owner + "." + name);
                mv.visitLdcInsn("");
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        Type.getInternalName(TraceInstrClass.class),
                        "log",
                        "(ILjava/lang/Object;ILjava/lang/String;Ljava/lang/String;)V",
                        false);
                break;
        }
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
}
