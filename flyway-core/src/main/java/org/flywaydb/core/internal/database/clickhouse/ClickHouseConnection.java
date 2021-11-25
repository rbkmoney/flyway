/*
 * Copyright 2010-2020 Redgate Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flywaydb.core.internal.database.clickhouse;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Connection;
import org.flywaydb.core.internal.database.base.Table;

import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * ClickHouse connection.
 */
public class ClickHouseConnection extends Connection<ClickHouseDatabase> {

    private final Configuration configuration;

    ClickHouseConnection(
            Configuration configuration,
            ClickHouseDatabase database,
            java.sql.Connection connection) {
        super(database, connection);
        this.configuration = configuration;
    }

    @Override
    protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
        return getJdbcTemplate().getConnection().getCatalog();
    }

    @Override
    public void doChangeCurrentSchemaOrSearchPathTo(String schema) throws SQLException {
        getJdbcTemplate().getConnection().setCatalog(schema);
    }

    @Override
    public ClickHouseSchema getSchema(String name) {
        return new ClickHouseSchema(jdbcTemplate, database, name);
    }

    @Override
    public <T> T lock(Table table, Callable<T> callable) {
        if (configuration.getZookeeperUrl() != null && !configuration.getZookeeperUrl().isEmpty()) {
            return new ZookeeperDistributedLockImpl(configuration.getZookeeperUrl(), table.getSchema().getName())
                    .execute(callable);
        }
        return super.lock(table, callable);
    }
}
