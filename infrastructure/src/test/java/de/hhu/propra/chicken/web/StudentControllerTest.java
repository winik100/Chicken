package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.BuchungsService;
import de.hhu.propra.chicken.aggregates.KlausurService;
import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.StudentService;
import de.hhu.propra.chicken.web.controllers.StudentController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static de.hhu.propra.chicken.util.KlausurTemplates.PK_10_11;
import static de.hhu.propra.chicken.util.KlausurTemplates.PK_12_13;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    BuchungsService buchungsService;

    @MockBean
    KlausurService klausurService;

    @MockBean
    StudentService studentService;

    @AfterAll
    static void logLoeschen() {
        File file = new File("auditlog.txt");
        file.delete();
    }

    OAuth2AuthenticationToken buildPrincipal(String role, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", name);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
        return new OAuth2AuthenticationToken(user, authorities, "whatever");
    }

    @Test
    @DisplayName("Falls noch kein Benutzer mit gegebenem Handle in der Datenbank existiert, wird dieser beim " +
            "Aufruf von / angelegt.")
    void test_1() throws Exception {
        when(studentService.findeStudentMitHandle(any())).thenReturn(null);
        OAuth2AuthenticationToken principal = buildPrincipal("user", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        MvcResult mvcResult = mvc.perform(get("/").session(session)).andReturn();

        verify(studentService, times(1))
                .studentHinzufuegen(new Student(any(),"testhandle"));
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("0");
        assertThat(contentAsString).contains("240");
    }

    @Test
    @DisplayName("Falls ein Benutzer mit gegebenem Handle in der Datenbank existiert, wird dieser beim " +
            "Aufruf von / geladen und seine Klausuren aus der Datenbank geholt.")
    void test_2() throws Exception {
        Student student = mock(Student.class);
        when(student.getResturlaubInMin()).thenReturn(60L);
        when(student.summeBisherigenUrlaubs()).thenReturn(180L);
        when(student.getKlausurAnmeldungen()).thenReturn(Set.of(111111L,222222L));
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(klausurService.findeKlausurenMitIds(Set.of(111111L,222222L))).thenReturn(Set.of(PK_10_11, PK_12_13));
        OAuth2AuthenticationToken principal = buildPrincipal("user", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        MvcResult mvcResult = mvc.perform(get("/").session(session)).andReturn();

        verify(klausurService, times(1)).findeKlausurenMitIds(Set.of(111111L,222222L));
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("180");
        assertThat(contentAsString).contains("60");
        assertThat(contentAsString).contains("PK_10_11");
        assertThat(contentAsString).contains("PK_12_13");
    }

/*    @Test
    @DisplayName("Falls ein Benutzer einen gueltigen Urlaub buchen möchte, wird BuchungsService.urlaubbuchen " +
            "korrekt aufgerufen.")
    void test_3() throws Exception {
        Student student = new Student(10L, "testhandle");
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        OAuth2AuthenticationToken principal = buildPrincipal("user", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mvc.perform(post("/urlaubsbuchung")
                .param("datum", "2022-03-25")
                .param("von", "11:00")
                .param("bis", "11:30").session(session));

        verify(buchungsService, times(1))
                .urlaubBuchen(student,
                        LocalDateTime.of(2022, 3, 25, 10, 30),
                        LocalDateTime.of(2022, 3, 25, 11, 30));
    }*/

/*    @Test
    @DisplayName("Beim Aufruf von /klausuranmeldung werden mit Klausurservice alle registrierten Klausuren aus der " +
            "Datenbank geholt.")
    void test_4() throws Exception {
        when(klausurService.alleKlausuren()).thenReturn(Set.of(PK_10_11, PK_12_13));
        OAuth2AuthenticationToken principal = buildPrincipal("user", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        MvcResult mvcResult = mvc.perform(get("/klausuranmeldungen").session(session)).andReturn();

        verify(klausurService, times(1)).alleKlausuren();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).contains("PK_10_11");
        assertThat(contentAsString).contains("PK_12_13");
    }*/
}
