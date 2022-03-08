package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import de.hswhameln.timetablemanager.test.SpringAssistedUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineServiceTest extends SpringAssistedUnitTest {

    private final LineRepository lineRepository;

    private final LineService objectUnderTest;

    @Autowired
    LineServiceTest(LineRepository lineRepository, LineService objectUnderTest) {
        this.lineRepository = lineRepository;
        this.objectUnderTest = objectUnderTest;
    }

    @Test
    void testCreateLine() throws NameAlreadyTakenException {
        long countBefore = this.lineRepository.count();
        String name = "77";
        Line returnedBusStop = this.objectUnderTest.createLine(name);

        Line actualLine = lineRepository.findById(returnedBusStop.getId()).orElseThrow();
        assertEquals(name, actualLine.getName());
        assertEquals(countBefore + 1, lineRepository.count());
    }

    @Test
    @Sql("/data-test.sql")
    void testCreateLineNameTaken() {
        long countBefore = this.lineRepository.count();
        String name = "S65"; // already taken
        assertThrows(NameAlreadyTakenException.class, () -> this.objectUnderTest.createLine(name));
        assertEquals(countBefore, lineRepository.count());
    }

    @Test
    @Sql("/data-test.sql")
    void testGetLines() {
        int count = (int) this.lineRepository.count();
        Collection<Line> lines = this.objectUnderTest.getLines();
        assertThat(lines)
                .hasSizeGreaterThan(1)
                .hasSize(count);
    }

    @Test
    @Sql("/data-test.sql")
    void testGetLine() {
        Line line = this.objectUnderTest.getLine(1).orElseThrow();
        assertEquals("1", line.getName());
    }

    @Test
    @Sql("/data-test.sql")
    void testDeleteLine() {
        long countBefore = lineRepository.count();
        this.objectUnderTest.deleteLine(1);
        assertEquals(countBefore - 1, this.lineRepository.count());
        assertThat(this.lineRepository.findById(1L)).isEmpty();
    }

    @Test
    @Sql("/data-test.sql")
    void testModifyLine() {
        this.objectUnderTest.modifyLine(1L, "N22");
        Line newLine = this.lineRepository.findById(1L).orElseThrow();
        assertEquals("N22", newLine.getName());
    }
}