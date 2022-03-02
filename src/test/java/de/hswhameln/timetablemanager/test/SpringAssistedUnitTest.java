package de.hswhameln.timetablemanager.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@SpringBootTest(
        // handle data on a test case basis to have better control and avoid tests breaking on data change
        properties = {"spring.datasource.initialization-mode=never", "spring.jpa.hibernate.ddl-auto=none"})
@Sql(value = "/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE) // allow additional method-level @Sql statements
public abstract class SpringAssistedUnitTest {
}
