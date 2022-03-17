package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.web.configuration.MethodSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.test.web.servlet.MockMvc;
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
//        mockMvc.perform(get("/admin")).andExpect(status().isUnauthorized());
//    }

// @Test
// void userLogin() throws Exception {
// OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
// MockHttpSession session = new MockHttpSession();
// session.setAttribute(
// HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
// new SecurityContextImpl(principal));
//
// mockMvc.perform(get("/game")
// .session(session))
// .andExpect(status().isOk());
// }
//
// @Test
// void userForbiddenOnAdmin() throws Exception {
// OAuth2AuthenticationToken principal = buildPrincipal("user", "Max Mustermann");
// MockHttpSession session = new MockHttpSession();
// session.setAttribute(
// HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
// new SecurityContextImpl(principal));
//
// mockMvc.perform(get("/admin")
// .session(session))
// .andExpect(status().isOk());
// }
//
// @Test
// void AdminIsOk() throws Exception {
// OAuth2AuthenticationToken principal = buildPrincipal("admin", "Max Mustermann");
// MockHttpSession session = new MockHttpSession();
// session.setAttribute(
// HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
// new SecurityContextImpl(principal));
//
// mockMvc.perform(get("/admin")
// .session(session))
// .andExpect(status().isOk());
// }
//
// @Test
// void AdminNameIsWritten() throws Exception {
// OAuth2AuthenticationToken principal = buildPrincipal("admin", "Ariane");
// MockHttpSession session = new MockHttpSession();
// session.setAttribute(
// HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
// new SecurityContextImpl(principal));
//
// var result = mockMvc.perform(get("/admin")
// .session(session))
// .andExpect(model().attribute("user", "Ariane"))
// .andReturn();
// assertThat(result.getResponse().getContentAsString()).contains("Ariane");
// }

}
