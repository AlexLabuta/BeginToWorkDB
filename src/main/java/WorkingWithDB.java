import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class WorkingWithDB {
    private static Connection connection;
    private static Statement statement;

    public static void main(String[] args) {
        try {
            connect();
            buildTable(Human.class);
            addObject(new Human(1, "Misha", 25));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            disconnect();
        }
    }

    public static void addObject(Human human) throws SQLException {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        stringBuilder.append(human.getClass().getAnnotation(Table.class).title());
        stringBuilder.append("(");
        Field[] fields = human.getClass().getDeclaredFields();
        for (Field o : fields) {
            stringBuilder.append(o.getName())
                    .append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        stringBuilder.append(") VALUES('")
                .append(human.id)
                .append("', '")
                .append(human.name)
                .append("', '")
                .append(human.age)
                .append("')");

        statement.executeUpdate(stringBuilder.toString());
    }

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Unable to connect");
        }
    }

    public static void buildTable(Class cl) throws SQLException {
        if (!cl.isAnnotationPresent(Table.class)) {
            throw new RuntimeException(" @Table is not found");
        }
        Map<Class, String> map = new HashMap<>();
        map.put(int.class, "INTEGER");
        map.put(String.class, "TEXT");
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(((Table) cl.getAnnotation(Table.class)).title());
        sb.append(" (");
        Field[] fields = cl.getDeclaredFields();
        for (Field o : fields) {
            if (o.isAnnotationPresent(Column.class)) {
                sb.append(o.getName())
                        .append(" ")
                        .append(map.get(o.getType()))
                        .append(", ");
            }
        }
        sb.setLength(sb.length() - 2);
        sb.append(");");

        statement.executeUpdate(sb.toString());
    }

    public static void disconnect() {
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
