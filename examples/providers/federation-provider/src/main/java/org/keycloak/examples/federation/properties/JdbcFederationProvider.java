package org.keycloak.examples.federation.properties;

import org.keycloak.models.*;

import java.util.*;

public class JdbcFederationProvider implements UserFederationProvider {
    protected static final Set<String> supportedCredentialTypes = new HashSet<String>();
    protected KeycloakSession session;
    protected Driver driver;
    protected UserFederationProviderModel model;

    public JdbcFederationProvider(KeycloakSession session, UserFederationProviderModel model, Driver driver) {
        this.session = session;
        this.model = model;
        this.driver = driver;
    }

    static
    {
        supportedCredentialTypes.add(UserCredentialModel.PASSWORD);
    }

    @Override
    public UserModel validateAndProxy(RealmModel realm, UserModel local) {
        return null;
    }

    @Override
    public boolean synchronizeRegistrations() {
        return true;
    }

    @Override
    public UserModel register(RealmModel realm, UserModel user){
        user.setFederationLink(null);

        String username = user.getUsername();
        System.out.println("username : " + username);
        DBUserCredential pass =  driver.getPassword(username);

        String dbPassword = pass.getDbPassword();
        String salt = pass.getSalt();

        System.out.println("salt : " + salt);
        System.out.println("dbPassword : " + dbPassword);

        if (dbPassword != null && salt != null) {

            UserCredentialValueModel credentials = new UserCredentialValueModel();
            credentials.setAlgorithm("sha256");
            credentials.setType(UserCredentialModel.PASSWORD);
            credentials.setSalt(salt.getBytes());
            credentials.setHashIterations(1);
            credentials.setValue(dbPassword);

            user.updateCredentialDirectly(credentials);

            System.out.println("salt : " + salt);
            System.out.println("dbPassword : " + dbPassword);
        }

        return user;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user){return false;}

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {



        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return null;
    }

    @Override
    public List<UserModel> searchByAttributes(Map<String, String> attributes, RealmModel realm, int maxResults) {
        return Collections.emptyList();
    }

    @Override
    public void preRemove(RealmModel realm) {
        // complete  We don't care about the realm being removed
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        // complete we dont'care if a role is removed

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        // complete we dont'care if a role is removed

    }


    /**
     * Проверить в db ли user
     *
     * @param local
     * @return
     */
    @Override
    public boolean isValid(RealmModel realm, UserModel local) {
        return true;
    }

    @Override
    public Set<String> getSupportedCredentialTypes(UserModel user) {
        return supportedCredentialTypes;
    }

    @Override
    public Set<String> getSupportedCredentialTypes() {
        return supportedCredentialTypes;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, List<UserCredentialModel> input) {
        return false;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, UserCredentialModel... input) {
        return true;
    }

    @Override
    public CredentialValidationOutput validCredentials(RealmModel realm, UserCredentialModel credential) {
        return CredentialValidationOutput.failed();
    }

    @Override
    public void close() {}
}
