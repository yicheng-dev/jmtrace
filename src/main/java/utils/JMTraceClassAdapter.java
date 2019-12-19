package utils;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JMTraceClassAdapter extends ClassVisitor {
    private ClassVisitor cv;
    public JMTraceClassAdapter(ClassVisitor cv) {
        super(Opcodes.ASM7, cv);
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.startsWith("java") || name.startsWith("sun")) return cv.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        mv = new JMTraceMethodAdapter(mv);
        return mv;
    }
}
