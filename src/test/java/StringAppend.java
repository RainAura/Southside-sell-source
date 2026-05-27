import java.util.Objects;

public class StringAppend {
    public static void main(String[] args) {
        System.out.println("1" + new People("imfl0wow", 1) + "3");
        System.out.println(get(1) + "2" + get(3) + "4");
    }

    public static int get(int i) {
        return i;
    }

    static class People {
        private String name;
        private int age;

        public People(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "People{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
