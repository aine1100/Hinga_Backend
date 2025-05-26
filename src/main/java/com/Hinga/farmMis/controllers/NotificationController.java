package com.Hinga.farmMis.controllers;

import com.Hinga.farmMis.Model.Notification;
import com.Hinga.farmMis.services.NotificationService;
import com.Hinga.farmMis.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtService jwtService;

    public NotificationController(NotificationService notificationService, JwtService jwtService) {
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getUserNotifications(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = jwtService.extractId(jwtToken);
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadNotifications(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = jwtService.extractId(jwtToken);
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadNotificationCount(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = jwtService.extractId(jwtToken);
            long count = notificationService.getUnreadNotificationCount(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@RequestHeader("Authorization") String token, @PathVariable Long notificationId) {
        try {
            String jwtToken = token.substring(7);
            Long userId = jwtService.extractId(jwtToken);
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.substring(7);
            Long userId = jwtService.extractId(jwtToken);
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
} 