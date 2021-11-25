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

import org.flywaydb.core.internal.database.base.Table;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;

import java.sql.SQLException;

/**
 * ClickHouse-specific table.
 */
public class ClickHouseTable extends Table<ClickHouseDatabase, ClickHouseSchema> {

    /**
     * Creates a new table.
     *
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param database     The database-specific support.
     * @param schema       The schema this table lives in.
     * @param name         The name of the table.
     */
    public ClickHouseTable(
            JdbcTemplate jdbcTemplate,
            ClickHouseDatabase database,
            ClickHouseSchema schema,
            String name) {
        super(jdbcTemplate, database, schema, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        int count = jdbcTemplate.queryForInt(
                "SELECT COUNT() FROM system.tables WHERE database = ? AND name = ?",
                schema.getName(), name);
        return count > 0;
    }

    @Override
    protected void doLock() throws SQLException {
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.executeStatement("DROP TABLE " + database.quote(schema.getName(), name));
    }
}
