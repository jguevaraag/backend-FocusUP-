package com.focusup.backend.controller;


import com.focusup.backend.model.Item; 
import com.focusup.backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private ItemRepository itemRepository;

    // Crear item(solo para admins).
    @PostMapping("/items")
    public ResponseEntity<Item> crearItem(@RequestBody Item item) {
        Item nuevoItem = itemRepository.save(item);
        return ResponseEntity.ok(nuevoItem);
    }
}
