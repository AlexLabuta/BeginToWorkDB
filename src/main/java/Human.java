@Table(title = "humans")
public class Human {
    @Column
    int id;
    @Column
    String name;
    @Column
    int age;

    public Human(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
