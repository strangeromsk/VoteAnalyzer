import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "tasklib";
    private static String dbUser = "root";
    private static String dbPass = "dfgIU987fmS";

    private static StringBuilder insertQuery = new StringBuilder();

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/" + dbName +
                                "?user=" + dbUser + "&password=" + dbPass + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE NOT NULL, " +
                        "PRIMARY KEY(id))");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void insertVoter(String name, String birthDate) throws SQLException {
        birthDate = birthDate.replace('.', '-');
        insertQuery.append(insertQuery.length() > 0 ? "," : "")
                .append("(' ").append(name).append("', '")
                .append(birthDate).append("')");
        if (insertQuery.length() > 2000) {
            String sql = "INSERT INTO voter_count(name, birthDate)" +
                    "VALUES " + insertQuery.toString();
            DBConnection.getConnection().createStatement().execute(sql);
//            ForkJoinPool.commonPool().execute(() -> {
//                try {
//                    DBConnection.getConnection().createStatement().execute(sql);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
            insertQuery = new StringBuilder();
        }
    }

    public static void printVoterCounts() throws SQLException {
        String sql = "SELECT name, birthDate, `count` FROM voter_count WHERE `count` > 1";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " (" +
                    rs.getString("birthDate") + ") - " + rs.getInt("count"));
        }
    }

    public static int customSelect() throws SQLException {
        String sql = "SELECT id FROM voter_count WHERE name='Исаичев Эмилиан'";
        ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
        if (!rs.next()) {
            return -1;
        } else {
            return rs.getInt("id");
        }
    }

    public static void closeConnection() throws SQLException {
        connection.close();
    }
}
