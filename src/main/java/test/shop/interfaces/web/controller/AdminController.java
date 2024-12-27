package test.shop.interfaces.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.shop.InitDb;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final InitDb initDb;

    @PostMapping("/reinitialize-db")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> reinitializeDb() {
        initDb.reinitialize();
        return ResponseEntity.ok("Reinitialized DB");
    }
}
