package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.BuchungsService;
import de.hhu.propra.chicken.aggregates.KlausurService;
import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.StudentenService;
import de.hhu.propra.chicken.web.configuration.MethodSecurityConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@Import({MethodSecurityConfiguration.class})
public class SecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    StudentenService studentenService;

    @MockBean
    KlausurService klausurService;

    @MockBean
    BuchungsService buchungsService;

    OAuth2AuthenticationToken buildPrincipal(String role, String name) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("login", name);

        List<GrantedAuthority> authorities = Collections.singletonList(
                new OAuth2UserAuthority("ROLE_" + role.toUpperCase(), attributes));
        OAuth2User user = new DefaultOAuth2User(authorities, attributes, "login");
        return new OAuth2AuthenticationToken(user, authorities, "whatever");
    }

//    @Test
//    void UnauthorizedWithoutLogin() throws Exception {
//        mockMvc.perform(get("/")).andExpect(status().isUnauthorized());
//    }

    @Test
    @DisplayName("User kann auf Startseite zugreifen")
    void test_2() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
        when(studentenService.findeStudentMitHandle(any())).thenReturn(new Student(1L, "blabla"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User kann nicht auf Tutorenseite zugreifen")
    void test_3() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/tutor")
                        .session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User kann nicht auf Adminseite zugreifen")
    void test_4() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/admin")
                        .session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Tutor kann auf Startseite zugreifen")
    void test_5() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("tutor", "Max Mustermann");
        when(studentenService.findeStudentMitHandle(any())).thenReturn(new Student(1L, "blabla"));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tutor kann auf Tutorseite zugreifen")
    void test_6() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("tutor", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/tutor")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Tutor kann nicht auf Adminseite zugreifen")
    void test_7() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("tutor", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/admin")
                        .session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin kann auf Startseite zugreifen")
    void test_8() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("admin", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        when(studentenService.findeStudentMitHandle(any())).thenReturn(new Student(1L, "blabla"));
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin kann auf Tutorseite zugreifen")
    void test_9() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("admin", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/tutor")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin kann auf Adminseite zugreifen")
    void test_10() throws Exception {
        OAuth2AuthenticationToken principal = buildPrincipal("admin", "Max Mustermann");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(principal));

        mockMvc.perform(get("/admin")
                        .session(session))
                .andExpect(status().isOk());
    }
}
