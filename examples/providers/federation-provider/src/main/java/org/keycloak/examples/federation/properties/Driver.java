package org.keycloak.examples.federation.properties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

public class Driver {
    private HikariDataSource ds;

    public Driver(String host, String login, String password) {
        System.out.println("Подключеник к DB");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(host);
        config.setUsername(login);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        ds = new HikariDataSource(config);
        System.out.println("Драйвер подключен");
    }

    public DBUserCredential getPassword(String username) {

        try (Connection connection = ds.getConnection()) {
            // use the connection
            Statement statement = connection.createStatement();
//            ResultSet result = statement.executeQuery("SELECT id,hash,login FROM relive.main.member where login='" + username + "'");
            ResultSet result = statement.executeQuery("SELECT username,id FROM user_entity where username='" + username + "'");
            String hashPassword = null;
            String salt = null;
            while (result.next()) {
//                salt = result.getString("id");
//                hashedPassword = result.getString("hash");
                salt = "1";
                hashPassword = "80ebda59183efebc1bea12e289a4b3e0c41ef431dea10ca179e00c6f41dddd18";
            }

            return new DBUserCredential(hashPassword, salt);

        } catch (Exception ex) {
            System.out.println("\nЯ Запрос шатал\n");
            throw new RuntimeException(ex);
        }

    }
}
