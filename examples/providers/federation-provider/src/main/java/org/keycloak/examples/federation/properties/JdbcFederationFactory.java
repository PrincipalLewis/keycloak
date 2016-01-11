package org.keycloak.examples.federation.properties;

import org.keycloak.Config;
import org.keycloak.models.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class JdbcFederationFactory implements UserFederationProviderFactory {
    public static final String PROVIDER_NAME = "my-test";

    static final Set<String> configOptions = new HashSet<String>();


    static {
        configOptions.add("db.host");
        configOptions.add("db.login");
        configOptions.add("db.password");
    }


    @Override
    public UserFederationProvider getInstance(KeycloakSession session, UserFederationProviderModel model) {
        String host = model.getConfig().get("db.host");
        String login = model.getConfig().get("db.login");
        String password = model.getConfig().get("db.password");

        if (host == null) {
            throw new IllegalStateException("host attribute not configured for provider");
        }
        if (login == null) {
            throw new IllegalStateException("login attribute not configured for provider");
        }
        if (password == null) {
            throw new IllegalStateException("password attribute not configured for provider");
        }


        return createProvider(session, model, createDriver(host, login, password));
    }


    /**
     * You can import additional plugin configuration from keycloak-server.json here.
     *
     * @param config
     */
    @Override
    public void init(Config.Scope config) {

    }
    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }
    @Override
    public void close() {

    }
    protected Driver createDriver(String host, String login, String password) {
        return new Driver(host, login, password);
    }

    protected JdbcFederationProvider createProvider(KeycloakSession session, UserFederationProviderModel model, Driver driver) {
        return new JdbcFederationProvider(session, model, driver);
    }

//    protected abstract JdbcFederationProvider createProvider(KeycloakSession session, UserFederationProviderModel model, Connection connection);
    /**
     * List the configuration options to render and display in the admin console's generic management page for this
     * plugin
     *
     * @return
     */
    @Override
    public Set<String> getConfigurationOptions() {
        return configOptions;
    }

    @Override
    public UserFederationProvider create(KeycloakSession session) {
        return null;
    }


    /**
     * Name of the provider.  This will show up under the "Add Provider" select box on the Federation page in the
     * admin console
     *
     * @return
     */
    @Override
    public String getId() {
        return PROVIDER_NAME;
    }

    @Override
    public UserFederationSyncResult syncAllUsers(KeycloakSessionFactory sessionFactory, final String realmId, final UserFederationProviderModel model) {
        return new UserFederationSyncResult();
    }

    @Override
    public UserFederationSyncResult syncChangedUsers(KeycloakSessionFactory sessionFactory, final String realmId, final UserFederationProviderModel model, Date lastSync) {
        return syncAllUsers(sessionFactory, realmId, model);
    }
}
