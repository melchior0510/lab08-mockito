package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    private Student activeStudent() {
        return new Student("S1", "s1@must.edu.mn", true, List.of("CS101"));
    }

    // A1
    @Test
    void register_activeStudentNoPrereq_returnsOkSavesAndSendsEmail() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), anyString());
    }

    // A2
    @Test
    void register_unknownStudent_returnsNoStudentAndDoesNotSaveOrEmail() {
        when(repository.findStudent("S1")).thenReturn(null);

        String result = service.register("S1", "CS201");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    // A3
    @Test
    void register_inactiveStudent_returnsInactiveAndNeverTouchesEmail() {
        when(repository.findStudent("S1"))
                .thenReturn(new Student("S1", "s1@must.edu.mn", false, List.of()));

        String result = service.register("S1", "CS201");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
    }

    // A4
    @Test
    void register_missingPrerequisite_returnsPrerequisitesErrorAndDoesNotSave() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("MATH101"));

        String result = service.register("S1", "CS201");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }

    // A5
    @Test
    void register_emailSenderThrows_stillReturnsOkAndSaves() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of());
        doThrow(new RuntimeException("SMTP down"))
                .when(emailSender).send(anyString(), anyString(), anyString());

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
    }

    // A6: хоёр аргумент тус бүрийн null-ийг шалгана
    @Test
    void register_nullArgument_throwsIllegalArgumentAndNeverTouchesDependencies() {
        assertThrows(IllegalArgumentException.class, () -> service.register(null, "CS201"));
        assertThrows(IllegalArgumentException.class, () -> service.register("S1", null));

        verifyNoInteractions(repository, emailSender);
    }

    // A7
    @Test
    void register_success_emailBodyContainsCourseCode() {
        when(repository.findStudent("S1")).thenReturn(activeStudent());
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        service.register("S1", "CS201");

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), body.capture());
        assertTrue(body.getValue().contains("CS201"));
    }

    // A8: мөр бүр өөр stub ашигладаг тул lenient()
    @ParameterizedTest
    @CsvSource({
            "NO_STUDENT, ERROR_NO_STUDENT",
            "INACTIVE,   ERROR_INACTIVE_STUDENT",
            "NO_COURSE,  ERROR_NO_COURSE"
    })
    void register_errorScenarios_returnExpectedErrorAndDoNotSave(String scenario, String expected) {
        Student student = scenario.equals("NO_STUDENT")
                ? null
                : new Student("S1", "s1@must.edu.mn", !scenario.equals("INACTIVE"), List.of());
        lenient().when(repository.findStudent("S1")).thenReturn(student);
        lenient().when(repository.courseExists("CS201")).thenReturn(!scenario.equals("NO_COURSE"));

        assertEquals(expected, service.register("S1", "CS201"));
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }
}
