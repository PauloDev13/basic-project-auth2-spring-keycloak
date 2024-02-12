package br.com.depgm.springkeycloak.security.controller;

import br.com.depgm.springkeycloak.dto.RoleDTO;
import br.com.depgm.springkeycloak.security.utils.KeycloakSecurityUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/keycloak")
@SecurityRequirement(name = "Keycloak")
public class RoleController {
    @Value("${realm}")
    private String realm;
    private final KeycloakSecurityUtil keycloakSecurityUtil;

    public RoleController(KeycloakSecurityUtil keycloakSecurityUtil) {
        this.keycloakSecurityUtil = keycloakSecurityUtil;
    }

    @GetMapping("/roles")
    public List<RoleDTO> getRoles() {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        List<RoleRepresentation> roleRepresentations = keycloak.realm(realm).roles().list();
        return mapRoles(roleRepresentations);
    }

    @GetMapping("/role/{name}")
    public RoleDTO getRole(@PathVariable String name) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        return mapRole(keycloak.realm(realm).roles().get(name).toRepresentation());
    }

    @PostMapping("/role")
    public Response createRole(@RequestBody RoleDTO roleDTO) {
        RoleRepresentation roleRep = mapRoleRep(roleDTO);
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).roles().create(roleRep);
        return Response.ok(roleRep).build();
    }

    @PutMapping("/role/{roleName}")
    public ResponseEntity<?> updateRole(@PathVariable String roleName, @RequestBody RoleDTO roleDTO) {
        RoleRepresentation roleRep = mapRoleRep(roleDTO);
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).roles().get(roleName).update(roleRep);

        return ResponseEntity.status(HttpStatus.OK).body(roleRep);
    }

    @DeleteMapping("/role/{roleName}")
    public void deleteRole(@PathVariable String roleName) {
        Keycloak keycloak = keycloakSecurityUtil.getKeycloakInstance();
        keycloak.realm(realm).roles().get(roleName).remove();
    }

    private RoleRepresentation mapRoleRep(RoleDTO roleDTO) {
        RoleRepresentation roleRep = new RoleRepresentation();

        roleRep.setName(roleDTO.name());
        roleRep.setDescription(roleDTO.description());

        return roleRep;
    }

    protected static List<RoleDTO> mapRoles(List<RoleRepresentation> roleRepresentations) {
        List<RoleDTO> roles = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(roleRepresentations)) {
            roleRepresentations.forEach(role -> roles.add(mapRole(role)));
        }

        return roles;
    }

    private static RoleDTO mapRole(RoleRepresentation roleRep) {
        return new RoleDTO(roleRep.getId(), roleRep.getName(), roleRep.getDescription());
    }
}
