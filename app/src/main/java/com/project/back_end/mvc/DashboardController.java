package com.project.back_end.mvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.back_end.services.TokenService;
import java.util.*;

@Controller
public class DashboardController {

    @Autowired
    private TokenService service;

    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        boolean success = service.validateToken(token, "admin");
        if (success) {
            return "admin/adminDashboard"; 
        } 
        else {
            return "redirect:/";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        boolean success = service.validateToken(token, "doctor");
        if (success) {
            return "doctor/doctorDashboard";
        } 
        else {
            return "redirect:/";
        }
    }
}


