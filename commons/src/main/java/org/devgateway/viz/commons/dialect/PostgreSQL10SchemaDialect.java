package org.devgateway.viz.commons.dialect;

import org.hibernate.dialect.PostgreSQL10Dialect;

public class PostgreSQL10SchemaDialect extends PostgreSQL10Dialect {

    @Override
    public String getQuerySequencesString() {
        return "select * from information_schema.sequences where sequence_schema is null";
    }

    @Override
    public String getCreateSequenceString(final String sequenceName) {
        return "create sequence if not exists " + sequenceName;
    }

}
