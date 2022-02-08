package de.hswhameln.timetablemanager.services;

import de.hswhameln.timetablemanager.entities.BusStop;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.repositories.LineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LineServiceTest {

    private final LineRepository lineRepository;

    private final LineService objectUnderTest;

    @Autowired
    LineServiceTest(LineRepository lineRepository, LineService objectUnderTest) {
        this.lineRepository = lineRepository;
        this.objectUnderTest = objectUnderTest;
    }

    @Test
    void testCreateLine() {
        long countBefore = this.lineRepository.count();
        String name = "77";
        Line returnedBusStop = this.objectUnderTest.createLine(name);

        Line actualLine = lineRepository.findById(returnedBusStop.getId()).orElseThrow();
        assertEquals(name, actualLine.getName());
        assertEquals(countBefore + 1, lineRepository.count());
    }

    @Test
    void testGetLines() {
        int count = (int) this.lineRepository.count();
        Collection<Line> lines = this.objectUnderTest.getLines();
        assertThat(lines)
                .hasSizeGreaterThan(1)
                .hasSize(count);
    }
    @Test
    void testGetLine() {
        Line line = this.objectUnderTest.getLine(1).orElseThrow();
        assertEquals("1", line.getName());
    }

    @Test
    void testDeleteLine() {
        long countBefore = lineRepository.count();
        this.objectUnderTest.deleteLine(1);
        assertEquals(countBefore - 1, this.lineRepository.count());
        assertThat(this.lineRepository.findById(1L)).isEmpty();
    }

    @Test
    void testModifyLine() {
        this.objectUnderTest.modifyLine(1L, "N22");
        Line newLine = this.lineRepository.findById(1L).orElseThrow();
        assertEquals("N22", newLine.getName());
    }
}