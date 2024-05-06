package hwtests.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hw.prince.controller.FacultyController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import hw.prince.model.Faculty;
import hw.prince.repository.FacultyRepository;
import hw.prince.repository.StudentRepository;
import hw.prince.service.FacultyService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private FacultyService facultyService;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;


    @Test
    public void updatePositiveTest() throws Exception {
        long id = 1;
        String newName = "Gryffindor";
        String newColor = "red";

        Faculty oldFaculty = new Faculty();
        oldFaculty.setId(id);
        oldFaculty.setName("Slytherin");
        oldFaculty.setColor("grin");

        Faculty newFaculty = new Faculty();
        newFaculty.setId(id);
        newFaculty.setName(newName);
        newFaculty.setColor(newColor);

        when(facultyRepository.findById(any())).thenReturn(Optional.of(oldFaculty));
        when(facultyRepository.save(any())).thenReturn(newFaculty);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/faculty/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFaculty))
        ).andExpect(result -> {
            MockHttpServletResponse response = result.getResponse();
            Faculty responseFaculty = objectMapper.readValue(response.getContentAsString(StandardCharsets.UTF_8), Faculty.class);
            assertThat(responseFaculty).usingRecursiveComparison().isEqualTo(newFaculty);
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        });
    }
}
