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

import java.io.File;
import java.util.*;

import static de.hhu.propra.chicken.util.KlausurTemplates.PK_10_11;
import static de.hhu.propra.chicken.util.KlausurTemplates.PK_12_13;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @DisplayName("Student kann Startseite erreichen und Werte für neu angelegten " +
            "Studenten werden ins Model geschrieben.")
    void test_1() throws Exception {
        when(studentService.findeStudentMitHandle(any())).thenReturn(null);
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mvc.perform(get("/").session(session))
                .andExpectAll(status().isOk(),
                        model().attribute("klausuren", Collections.emptySet()),
                        model().attribute("urlaube", Collections.emptySet()),
                        model().attribute("urlaubssumme", 0L),
                        model().attribute("resturlaub", 240L));
    }

    @Test
    @DisplayName("Falls ein Benutzer mit gegebenem Handle in der Datenbank existiert, wird dieser beim " +
            "Aufruf von / geladen und seine Klausuren aus der Datenbank ins Model geschrieben.")
    void test_2() throws Exception {
        Student student = mock(Student.class);
        when(student.getResturlaubInMin()).thenReturn(60L);
        when(student.summeBisherigenUrlaubs()).thenReturn(180L);
        when(student.getKlausurAnmeldungen()).thenReturn(Set.of(111111L,222222L));
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(klausurService.findeKlausurenMitIds(Set.of(111111L,222222L))).thenReturn(Set.of(PK_10_11, PK_12_13));
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mvc.perform(get("/").session(session))
                .andExpectAll(status().isOk(),
                        model().attribute("klausuren", Set.of(PK_10_11, PK_12_13)),
                        model().attribute("urlaube", Collections.emptySet()),
                        model().attribute("urlaubssumme", 180L),
                        model().attribute("resturlaub", 60L));
    }

    @Test
    @DisplayName("Student kann Klausurregistrierungsseite aufrufen.")
    void test_3() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        mvc.perform(get("/klausurregistrierung").session(session)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student kann Urlaubbuchungsseite aufrufen.")
    void test_4() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        mvc.perform(get("/urlaubsbuchung").session(session)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Student kann Klausuranmeldungsseite aufrufen.")
    void test_5() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        when(klausurService.alleKlausuren()).thenReturn(Set.of(PK_10_11, PK_12_13));
        mvc.perform(get("/klausuranmeldung").session(session))
                .andExpectAll(status().isOk(),
                        model().attribute("klausuren", Set.of(PK_10_11, PK_12_13)));
    }

    @Test
    @DisplayName("Student kann versuchen eine Klausur registrieren und wird bei Erfolg zur Klausuranmeldung geschickt.")
    void test_6() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        when(klausurService.klausurHinzufuegen(any())).thenReturn("");
        mvc.perform(post("/klausurregistrierung").with(csrf())
                        .param("veranstaltung", "testklausur")
                        .param("lsfid", "111111")
                        .param("vor_ort", "true")
                        .param("datum", "2022-03-20")
                        .param("von", "10:30")
                        .param("bis", "11:30").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Student kann versuchen eine Klausur registrieren und erhält bei Misserfolg eine Fehlernachricht.")
    void test_7() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        when(klausurService.klausurHinzufuegen(any())).thenReturn("testerror");
        mvc.perform(post("/klausurregistrierung").with(csrf())
                        .param("veranstaltung", "testklausur")
                        .param("lsfid", "111111")
                        .param("vor_ort", "true")
                        .param("datum", "2022-03-20")
                        .param("von", "10:30")
                        .param("bis", "11:30").session(session))
                .andExpectAll(status().isOk(),
                        model().attribute("klausurregistrierungserror", "testerror"));
    }

    @Test
    @DisplayName("Student kann versuchen einen Urlaub zu buchen und wird bei Erfolg zur Indexseite geschickt.")
    void test_8() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(buchungsService.urlaubBuchen(any(), any(), any())).thenReturn("");
        mvc.perform(post("/urlaubsbuchung").with(csrf())
                        .param("datum", "2022-03-20")
                        .param("von", "10:30")
                        .param("bis", "11:30").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Student kann versuchen einen Urlaub zu buchen und erhält bei Misserfolg eine Fehlernachricht.")
    void test_9() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(buchungsService.urlaubBuchen(any(), any(), any())).thenReturn("testerror");
        mvc.perform(post("/urlaubsbuchung").with(csrf())
                        .param("datum", "2022-03-20")
                        .param("von", "10:30")
                        .param("bis", "11:30").session(session))
                .andExpectAll(status().isOk(),
                        model().attribute("urlaubbuchungserror", "testerror"));
    }

    @Test
    @DisplayName("Student kann versuchen eine angemeldete Klausur zu stornieren und wird bei" +
            " Erfolg zum Index zurückgeschickt.")
    void test_10() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(klausurService.findeKlausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsService.klausurStornieren(any(), any())).thenReturn("");

        mvc.perform(post("/klausurstornierung").with(csrf())
                        .param("lsfId", "111111")
                        .session(session))
                .andExpectAll(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Student kann versuchen gebuchten Urlaub zu stornieren und wird bei" +
            " Erfolg zum Index zurückgeschickt.")
    void test_11() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("student", "testhandle");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(buchungsService.urlaubStornieren(any(), any(), any())).thenReturn("");

        mvc.perform(post("/urlaubsstornierung").with(csrf())
                        .param("datum", "2022-03-20")
                        .param("von", "10:30")
                        .param("bis", "11:30")
                        .session(session))
                .andExpectAll(status().is3xxRedirection());
    }
}
