package br.com.depgm.springkeycloak.dto;

public record UserDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String username,
        String password
) {
}
