package br.com.depgm.springkeycloak.controller;

import br.com.depgm.springkeycloak.dto.RoleDTO;
import br.com.depgm.springkeycloak.dto.UserDTO;
import br.com.depgm.springkeycloak.security.utils.KeycloakSecurityUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/keycloak")
@SecurityRequirement(name = "Keycloak")
public class UserController {

    @Value("${realm}")
    private String realm;
    private final KeycloakSecurityUtil keycloakSecurityUtil;

    public UserController(KeycloakSecurityUtil keycloakSecurityUtil) {
        this.keycloakSecurityUtil = keycloakSecurityUtil;
    }

    @GetMapping("/users")
    public List<UserDTO> getUsers() {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        List<UserRepresentation> userRepresentation = keycloak.realm(realm).users().list();
        return mapUsers(userRepresentation);
    }

    @GetMapping("/user/{id}")
    public UserDTO getUser(@PathVariable String id) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        return mapUser(keycloak.realm(realm).users().get(id).toRepresentation());
    }

    @PostMapping("/user")
    public Response createUser(UserDTO userDTO) {
        UserRepresentation userRep = mapUserRep(userDTO);
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).users().create(userRep);
        return Response.ok(userDTO).build();
    }

    @PutMapping("/user")
    public Response updateUser(UserDTO userDTO) {
        UserRepresentation userRep = mapUserRep(userDTO);
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).users().get(userDTO.id()).update(userRep);
        return Response.ok(userDTO).build();
    }

    @DeleteMapping("/user/{id}")
    public Response deleteUser(@PathVariable String id) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).users().delete(id);
        return Response.ok().build();

    }

    @GetMapping("/users/{id}/roles")
    public List<RoleDTO> getRoles(@PathVariable String id) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        return RoleController.mapRoles(keycloak.realm(realm).users().get(id).roles().realmLevel().listAll());
    }

    // private methods
    private List<UserDTO> mapUsers(List<UserRepresentation> userRepresentations) {
        List<UserDTO> users = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(userRepresentations)) {
            userRepresentations.forEach(userRep -> users.add(mapUser(userRep)));
        }
        return users;
    }

    private UserDTO mapUser(UserRepresentation userRep) {
        return new UserDTO(
                userRep.getId(),
                userRep.getFirstName(),
                userRep.getLastName(),
                userRep.getEmail(),
                userRep.getUsername(), null);
    }
    private UserRepresentation mapUserRep(UserDTO userDTO) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setId(userDTO.id());
        userRep.setUsername(userDTO.username());
        userRep.setFirstName(userDTO.firstName());
        userRep.setLastName(userDTO.lastName());
        userRep.setEmail(userDTO.email());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setValue(userDTO.password());
        creds.add(cred);
        userRep.setCredentials(creds);

        return userRep;
    }
}
