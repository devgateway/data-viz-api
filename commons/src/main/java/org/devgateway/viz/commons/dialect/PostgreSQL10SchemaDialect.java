package org.devgateway.viz.commons.dialect;

import org.hibernate.dialect.PostgreSQLDialect;

public class PostgreSQL10SchemaDialect extends PostgreSQLDialect {

    @Override
    public String getQuerySequencesString() {
        return "select * from information_schema.sequences where sequence_schema is null";
    }

    public String getCreateSequenceString(final String sequenceName) {
        return "create sequence if not exists " + sequenceName;
    }

}
