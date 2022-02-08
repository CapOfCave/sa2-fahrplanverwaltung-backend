package de.hswhameln.timetablemanager.services;

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
        String name = "77";
        this.objectUnderTest.createLine(name);

        assertEquals(2, lineRepository.count());
    }

    @Test
    void testGetLines() {
        Collection<Line> lines = this.objectUnderTest.getLines();
        assertThat(lines)
                .hasSize(1)
                .first()
                .extracting(Line::getName)
                .isEqualTo("S56");
    }

    @Test
    void testDeleteLine() {
        Line stop = this.lineRepository.save(new Line("81"));
        assertEquals(2, this.lineRepository.count());
        this.objectUnderTest.deleteLine(stop.getId());
        assertEquals(1, this.lineRepository.count());
    }

    @Test
    void testModifyLine() {
        this.objectUnderTest.modifyLine(1L, "N22");
        Line newLine = this.lineRepository.findById(1L).orElseThrow();
        assertEquals("N22", newLine.getName());
    }
}