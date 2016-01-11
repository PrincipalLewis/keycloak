package org.keycloak.examples.federation.properties;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Driver {
    protected String host;
    protected String login;
    protected String password;

    public Driver(String host, String login, String password) {
        this.host = host;
        this.login = login;
        this.password = password;
    }

    public Map<String,String> getPassword(String username) {
        try {
            System.out.println("Подключеник к DB");
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер подключен");
            Connection connection = DriverManager.getConnection(this.host, this.login, this.password);
            System.out.println("Соединение установлено");

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Statement statement = connection.createStatement();
            ResultSet result1 = statement.executeQuery("SELECT user FROM users where name=" + username);

            while (result1.next()) {
                String userid = result1.getString("id");
                String usernam = result1.getString("username");

                System.out.println("userid : " + userid);
                System.out.println("username : " + usernam);
            }

//            System.out.println(dbPassword);
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

            Map<String, String> hm = new HashMap<>();
            hm.put("dbPassword", result1.getString("hashPassword"));
            hm.put("salt", result1.getString("salt"));
            return hm;

        } catch (Exception ex) {
            System.out.println("\nЯ Запрос шатал\n");
            throw new RuntimeException(ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println("Хрен я тебе закроюсь!!!!");
                    System.err.println(ex);
                }
            }
        }
    }
}
