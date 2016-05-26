package com.novoda.github.reports.data.db;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.Record;

@FunctionalInterface
interface UpdateOrInsertGenerator<T, R extends Record> {

    InsertOnDuplicateSetMoreStep<R> getQuery(DSLContext context, T element);

}
