package de.hswhameln.timetablemanager;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        // handle data on a test case basis to have better control and avoid tests breaking on data change
        properties = {"spring.datasource.initialization-mode=never", "spring.jpa.hibernate.ddl-auto=none"})
@Sql("/schema.sql")
@Sql(value = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE) // allow additional method-level @Sql statements
public abstract class IntegrationTest  {
}
