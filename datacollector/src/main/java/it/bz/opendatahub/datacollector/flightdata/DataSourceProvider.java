package it.bz.opendatahub.datacollector.flightdata;

import org.postgresql.ds.PGSimpleDataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

/**
 * Simple provider for data sources.
 */
@ApplicationScoped
public class DataSourceProvider {
    private final DatabaseConfig databaseConfig;

    public DataSourceProvider(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Build and return a {@link DataSource} for a Postgres database with
     * the parameters specified by the {@link DatabaseConfig}.
     *
     * @return A {@link DataSource} that can be used to communicate to a
     * Postgres instance.
     */
    public DataSource setupDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(databaseConfig.url());
        ds.setDatabaseName(databaseConfig.name());
        ds.setUser(databaseConfig.user());
        ds.setPassword(databaseConfig.pass());
        System.out.println("-------DATASOURCE------------");
        System.out.println("URL: " + databaseConfig.url());
        System.out.println("NAME: " + databaseConfig.name());
        System.out.println("USER: " + databaseConfig.user());
        System.out.println("PASS: " + databaseConfig.pass());
        System.out.println("-------DATASOURCE-END--------");
        return ds;
    }
}
