package com.prismhealth.Controllers;

import com.prismhealth.services.SosService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("SoS")
public class SosController {
    private final SosService sosService;

    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSos(@RequestParam String[] position, Principal principal){
        return sosService.sendSos(position,principal);
    }

}
