package de.hswhameln.timetablemanager.controller;

import de.hswhameln.timetablemanager.dto.requests.CreateLineRequest;
import de.hswhameln.timetablemanager.dto.requests.ModifyLineRequest;
import de.hswhameln.timetablemanager.dto.responses.LineDetailDto;
import de.hswhameln.timetablemanager.dto.responses.LineOverviewDto;
import de.hswhameln.timetablemanager.entities.Line;
import de.hswhameln.timetablemanager.exceptions.DeletionForbiddenException;
import de.hswhameln.timetablemanager.exceptions.LineNotFoundException;
import de.hswhameln.timetablemanager.exceptions.NameAlreadyTakenException;
import de.hswhameln.timetablemanager.exceptions.NotFoundException;
import de.hswhameln.timetablemanager.mapping.LineToDtoMapper;
import de.hswhameln.timetablemanager.services.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final LineToDtoMapper lineToDtoMapper;

    @Autowired
    public LineController(LineService lineService, LineToDtoMapper lineToDtoMapper) {
        this.lineService = lineService;
        this.lineToDtoMapper = lineToDtoMapper;
    }

    @GetMapping
    public Collection<LineOverviewDto> getLines() {
        return this.lineService.getLines()
                .stream()
                .map(this.lineToDtoMapper::mapToLineOverviewDto)
                .toList();
    }

    @PostMapping
    public LineDetailDto createLine(@RequestBody CreateLineRequest createLineRequest) throws NameAlreadyTakenException {
        Line line = this.lineService.createLine(createLineRequest.getName());
        return this.lineToDtoMapper.mapToLineDetailDto(line);
    }

    @GetMapping("/{lineId}")
    public LineDetailDto getLine(@PathVariable long lineId) throws LineNotFoundException {
        Line line = this.lineService.getLine(lineId);
        return this.lineToDtoMapper.mapToLineDetailDto(line);
    }

    @PatchMapping("/{lineId}")
    public LineDetailDto modifyLine(@PathVariable long lineId, @RequestBody ModifyLineRequest modifyLineRequest) throws NotFoundException, NameAlreadyTakenException {
        Line line =  this.lineService.modifyLine(lineId, modifyLineRequest.getName());
        return this.lineToDtoMapper.mapToLineDetailDto(line);
    }

    @DeleteMapping("/{lineId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLine(@PathVariable long lineId) throws NotFoundException, DeletionForbiddenException {
        this.lineService.deleteLine(lineId);
    }
}
