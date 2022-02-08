package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.CreateLineRequest;
import de.hswhameln.timetablemanager.dto.ModifyLineRequest;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.services.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/")
    public Collection<Line> getLines() {
        return this.lineService.getLines();
    }

    @PostMapping("/")
    public Line createLine(@RequestBody CreateLineRequest createLineRequest) {
        return this.lineService.createLine(createLineRequest.getName());
    }

    @PutMapping("/:id")
    public Line modifyLine(@RequestParam long id, @RequestBody ModifyLineRequest modifyLineRequest) {
        return this.lineService.modifyLine(id, modifyLineRequest.getName());
    }

    @DeleteMapping("/:id")
    public void createLine(@RequestParam long id) {
        this.lineService.deleteLine(id);
    }
}
