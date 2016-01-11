package org.keycloak.examples.federation.properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

public class Driver {
    protected String host;
    protected String login;
    protected String password;

    public Driver(String host, String login, String password) {
        this.host = host;
        this.login = login;
        this.password = password;
    }

    public DBUserCredential getPassword(String username) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Подключеник к DB");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(host);
        config.setUsername(login);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        HikariDataSource ds = new HikariDataSource(config);
        System.out.println("Драйвер подключен");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        try (Connection connection = ds.getConnection()) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            // use the connection
            Statement statement = connection.createStatement();
            ResultSet result1 = statement.executeQuery("SELECT user FROM users where name=" + username);
            while (result1.next()) {
                String userid = result1.getString("id");
                String usernam = result1.getString("username");

                System.out.println("userid : " + userid);
                System.out.println("username : " + usernam);
            }

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            return new DBUserCredential(result1.getString("hashPassword"), result1.getString("salt"));

        } catch (Exception ex) {
            System.out.println("\nЯ Запрос шатал\n");
            throw new RuntimeException(ex);
        }



//        Connection connection = null;
//        try {
//            System.out.println("Подключеник к DB");
//            Class.forName("org.postgresql.Driver");
//
//
//            System.out.println("Драйвер подключен");
//            connection = DriverManager.getConnection(this.host, this.login, this.password);
//            System.out.println("Соединение установлено");
//
//            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            Statement statement = connection.createStatement();
//            ResultSet result1 = statement.executeQuery("SELECT user FROM users where name=" + username);
//
//
//
//        } catch (Exception ex) {
//            System.out.println("\nЯ Запрос шатал\n");
//            throw new RuntimeException(ex);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException ex) {
//                    System.out.println("Хрен я тебе закроюсь!!!!");
//                    System.err.println(ex);
//                }
//            }
//        }
    }
}
