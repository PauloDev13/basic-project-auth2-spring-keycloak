package br.com.depgm.springkeycloak.security.auth;

import br.com.depgm.springkeycloak.security.dto.SignInDTO;
import br.com.depgm.springkeycloak.security.dto.SignOutDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("auth")
@SecurityRequirement(name = "Keycloak")
public class AuthenticateKeycloak {

    @Value("${tokenUrl}")
    private String baseUrl;

    @Value("${clientId}")
    private String clientID;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${grant-type}")
    private String grantType;


    private final HttpHeaders headers = new HttpHeaders();
    private final RestTemplate rt = new RestTemplate();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody SignInDTO user) {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var urlLogin = baseUrl + "/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("client_id", clientID);
        formData.add("client_secret", clientSecret);
        formData.add("username", user.username());
        formData.add("password", user.password());
        formData.add("grant_type", grantType);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        return rt.postForEntity(urlLogin, entity, String.class);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody SignOutDTO signOutDTO) {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var urlLogout = baseUrl + "/logout";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("client_id", clientID);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", signOutDTO.refreshToken());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        rt.postForEntity(urlLogout, entity, String.class);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
