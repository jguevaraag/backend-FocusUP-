package com.focusup.backend.controller;

import com.focusup.backend.model.Inventari;
import com.focusup.backend.model.Item;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.InventariRepository;
import com.focusup.backend.repository.ItemRepository;
import com.focusup.backend.repository.UsuariRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/botiga")
public class BotigaController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private InventariRepository inventariRepository;

    @GetMapping
    public ResponseEntity<List<Item>> obtenerItemsTienda() {
        List<Item> items = itemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/comprar/{itemId}")
    public ResponseEntity<?> comprarItem(@PathVariable Long itemId, Principal principal) {

        Usuari usuari = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        Optional<Inventari> inventariExistente = inventariRepository.findByUsuariIdAndItemId(usuari.getId(), item.getId());
        
        if (inventariExistente.isPresent()) {
            // Devolvemos un JSON de error
            return ResponseEntity.badRequest().body(Map.of("error", "Ya tienes este item en tu inventario"));
        }

        if (usuari.getPunts() < item.getPreu()) {
            // Devolvemos un JSON de error
            return ResponseEntity.badRequest().body(Map.of("error", "No tienes suficientes puntos para comprar este item"));
        }

        // Transacción
        usuari.setPunts(usuari.getPunts() - item.getPreu());
        usuariRepository.save(usuari);

        Inventari nuevoInventari = new Inventari();
        nuevoInventari.setUsuari(usuari);
        nuevoInventari.setItem(item);
        nuevoInventari.setDataCompra(LocalDateTime.now());
        nuevoInventari.setEquipado(false);
        inventariRepository.save(nuevoInventari);

        // AQUÍ LA MAGIA: Le devolvemos un mensaje y el nuevo saldo en un JSON
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Item comprado correctamente");
        response.put("nouSaldo", usuari.getPunts());

        return ResponseEntity.ok(response);
    }
}
