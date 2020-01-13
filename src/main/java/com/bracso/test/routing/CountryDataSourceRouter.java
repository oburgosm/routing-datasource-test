package com.bracso.test.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import static com.bracso.test.routing.LocaleContextHolder.LOCALE_ES;

/**
 *
 * @author oburgosm
 */
public class CountryDataSourceRouter extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return LocaleContextHolder.getLocale().orElse(LOCALE_ES);
    }

}
