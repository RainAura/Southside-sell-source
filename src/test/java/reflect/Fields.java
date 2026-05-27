package reflect;

public class Fields {
    public static void main(String[] args) {
        System.out.println(Long.class.isAssignableFrom(Long.class));
        System.out.println(ClassA.class.getFields().length);
    }
}
