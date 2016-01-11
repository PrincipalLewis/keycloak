package org.keycloak.examples.federation.properties;


public class DBUserCredential {
    protected String dbPassword;
    protected String salt;

    DBUserCredential(String hashPassword, String salt) {
        this.dbPassword = hashPassword;
        this.salt = salt;
    }

    public String getDbPassword() {
        return dbPassword;
    }
    public String getSalt() {
        return salt;
    }
}
