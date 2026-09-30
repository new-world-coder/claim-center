package com.claimcenter.document.api;

import com.claimcenter.document.service.DocumentApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentApplicationService service;

    public DocumentController(DocumentApplicationService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentResponse> list() {
        return service.list();
    }

    @PostMapping
    public DocumentResponse create(@Valid @RequestBody DocumentRequest request) {
        return service.create(request);
    }
}
