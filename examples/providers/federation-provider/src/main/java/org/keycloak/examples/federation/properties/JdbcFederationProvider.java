package org.keycloak.examples.federation.properties;

import org.keycloak.hash.Sha256PasswordHashProvider;
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

    public KeycloakSession getSession() {
        return session;
    }
    public UserFederationProviderModel getModel() {
        return model;
    }

    public UserModel validateAndProxy(RealmModel realm, UserModel local) {
        return null;
    }

    public boolean synchronizeRegistrations() {
        return false;
    }

    public UserModel register(RealmModel realm, UserModel user){return null;}
    public boolean removeUser(RealmModel realm, UserModel user){return false;}

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        DBUserCredential pass =  driver.getPassword(username);

        if (pass != null) {
            UserModel userModel = session.userStorage().addUser(realm, username);
            userModel.setEnabled(true);
            userModel.setFederationLink(model.getId());
            return userModel;
        }
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
        for (UserCredentialModel cred : input) {
            if (cred.getType().equals(UserCredentialModel.PASSWORD)) {
                return savePassword(realm, user, cred);
            } else {
                return false; // invalid cred type
            }
        }
        return false;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, UserCredentialModel... input) {
        for (UserCredentialModel cred : input) {
            if (cred.getType().equals(UserCredentialModel.PASSWORD)) {
                return savePassword(realm, user, cred);
            } else {
                return false; // invalid cred type
            }
        }
        return true;
    }

    private boolean savePassword(RealmModel realm, UserModel user, UserCredentialModel cred) {
        System.out.println("--------------------------------------------------------------------------------------------");
        DBUserCredential pass =  driver.getPassword(user.getUsername());

        String dbPassword = pass.getDbPassword();
        String salt = pass.getSalt();
        System.out.println("--------------------------------------------------------------------------------------------");
        if ( dbPassword == null) return false;

        String HashedPassword = Sha256PasswordHashProvider.encodePassword(cred.getValue(), salt);
        boolean check = dbPassword.equals(HashedPassword);
        if (check) {
            session.userStorage().getUserById(user.getId(), realm).setFederationLink(null);
            UserCredentialValueModel hashCred = Sha256PasswordHashProvider.encode(cred.getValue(), 1, salt);
            session.userStorage().getUserById(user.getId(), realm).updateCredentialDirectly(hashCred);
        }
        return check;
    }

    @Override
    public CredentialValidationOutput validCredentials(RealmModel realm, UserCredentialModel credential) {
        return CredentialValidationOutput.failed();
    }

    @Override
    public void close() {}
}
