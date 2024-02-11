package br.com.depgm.springkeycloak.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/visitors")
@SecurityRequirement(name = "Keycloak")
public class VisitorController {

    @GetMapping("/all") // public API
    public String getVisitors() {
        return "Response Get Visitors";
    }

    @PostMapping("/add") // admin can access
    public String createVisitor() {
        return "Response Create Visitor";
    }

    @PutMapping("/upd") // manager can access
    public String updateVisitor() {
        return "Response Update Visitor";
    }

    @DeleteMapping("/del") // owner can access
    public String deleteVisitor() {
        return "Response Delete Visitor";
    }
}
