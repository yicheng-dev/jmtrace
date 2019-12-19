package agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import utils.JMTraceClassAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MainAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception{
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> clazz,
                                    ProtectionDomain protectionDomain,
                                    byte[] byteCode) throws IllegalClassFormatException {
                ClassReader cr = new ClassReader(byteCode);
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
                ClassVisitor cv = new JMTraceClassAdapter(cw);
                cr.accept(cv, 0);
                return cw.toByteArray();
            }
        });
    }
}