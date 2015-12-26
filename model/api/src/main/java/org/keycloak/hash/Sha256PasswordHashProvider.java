package org.keycloak.hash;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserCredentialValueModel;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class Sha256PasswordHashProvider implements PasswordHashProviderFactory, PasswordHashProvider {

    public static final String ID = "sha256";


    public UserCredentialValueModel encode(String rawPassword, int iterations) {
        String stringSalt = new String(getSalt());
        return encode(rawPassword, iterations, stringSalt);
    }

    public static UserCredentialValueModel encode(String rawPassword, int iterations, String salt) {

        String encodedPassword = encodePassword(rawPassword, salt);
        UserCredentialValueModel credentials = new UserCredentialValueModel();
        credentials.setAlgorithm(ID);
        credentials.setType(UserCredentialModel.PASSWORD);
        credentials.setSalt(salt.getBytes());
        credentials.setHashIterations(iterations);
        credentials.setValue(encodedPassword);
        return credentials;
    }

    public boolean verify(String rawPassword, UserCredentialValueModel credential) {
        String stringSalt = new String(credential.getSalt());
        return encodePassword(rawPassword, stringSalt).equals(credential.getValue());
    }

    @Override
    public PasswordHashProvider create(KeycloakSession session) {
        return this;
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }

    private static String encodePassword(String rawPassword, String salt) {
        String md5PlusSalt = alg(rawPassword, "MD5") + salt;
        return alg(md5PlusSalt, "sha-256");
    }

    private static String alg(String data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte [] messageDigest = md.digest(data.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private byte[] getSalt() {
        byte[] buffer = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(buffer);
        return buffer;
    }

}
