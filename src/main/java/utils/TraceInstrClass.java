package utils;

public class TraceInstrClass {

    public static int READ = 1;
    public static int WRITE = 2;

    public static void log(int index, Object object, int rw, String name, String arrayType) {
        if (rw == TraceInstrClass.READ) System.out.print("R "); else System.out.print("W ");
        System.out.printf("%d %016x ", Thread.currentThread().getId(), System.identityHashCode(object));
        if (index != -1) {
            System.out.print(convert(arrayType) + "[" + index + "]");
        }
        else {
            System.out.print(name.replace("/", "."));
        }
        System.out.println();
    }

    private static String convert(String type) {
        switch (type) {
            case "I": return "int";
            case "Z": return "boolean";
            case "C": return "char";
            case "B": return "byte";
            case "S": return "short";
            case "F": return "float";
            case "J":
            case "L": return "long";
            case "D": return "double";
            default: return type;
        }
    }
}
