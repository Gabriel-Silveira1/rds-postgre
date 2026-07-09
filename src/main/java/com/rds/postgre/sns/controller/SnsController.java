package com.rds.postgre.sns.controller;

import com.rds.postgre.sns.service.SnsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sns")
public class SnsController {

    private final SnsService snsService;

    public SnsController(SnsService snsService) {
        this.snsService = snsService;
    }

    @PostMapping("/publicar")
    public ResponseEntity<String> publicar(@RequestBody String message) {
        String messageId = snsService.publish(message);
        return ResponseEntity.ok("Publicado! MessageId: " + messageId);
    }

    @GetMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestParam String email) {
        return ResponseEntity.ok(snsService.subscribeEmail(email));
    }
}