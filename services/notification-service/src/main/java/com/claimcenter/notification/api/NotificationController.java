package com.claimcenter.notification.api;

import com.claimcenter.notification.service.NotificationApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationApplicationService service;

    public NotificationController(NotificationApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return service.list();
    }

    @PostMapping
    public NotificationResponse create(@Valid @RequestBody NotificationRequest request) {
        return service.create(request);
    }
}
