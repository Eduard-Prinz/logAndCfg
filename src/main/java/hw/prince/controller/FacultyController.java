package hw.prince.controller;

import org.springframework.web.bind.annotation.*;
import hw.prince.model.Faculty;
import hw.prince.model.Student;
import hw.prince.service.FacultyService;

import java.util.List;

@RequestMapping("/faculty")
@RestController
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }



    @PostMapping
    public Faculty create(@RequestBody Faculty faculty) {
        return facultyService.create(faculty);
    }

    @PutMapping("/{id}")
    public Faculty update(@PathVariable long id, @RequestBody Faculty faculty) {
        return facultyService.update(id, faculty);
    }

    @DeleteMapping("/{id}")
    public Faculty delete(@PathVariable long id) {
        return facultyService.delete(id);
    }

    @GetMapping("/{id}")
    public Faculty get(@PathVariable long id) {
        return facultyService.get(id);
    }

    @GetMapping(params = "color")
    public List<Faculty> findByColor(@RequestParam String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping(params = "colorOrName")
    public List<Faculty> findNameOrColor(@RequestParam String colorOrName) {
        return facultyService.findNameOrColor(colorOrName);
    }

    @GetMapping("/{id}/students")
    public List<Student> findStudents(@PathVariable long id) {
        return facultyService.findStudents(id);
    }

}
