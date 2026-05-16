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
import java.util.ArrayList;
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
    public ResponseEntity<List<Map<String, Object>>> obtenerItemsTienda(Principal principal) {
        
        Usuari usuari = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Item> todosLosItems = itemRepository.findAll();
        List<Inventari> miInventario = inventariRepository.findByUsuariId(usuari.getId());

        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Item item : todosLosItems) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", item.getId());
            dto.put("nom", item.getNom());
            dto.put("descripcio", item.getDescripcio());
            dto.put("preu", item.getPreu());
            dto.put("tipus", item.getTipus());

            boolean comprat = false;
            boolean equipat = false;

            for (Inventari inv : miInventario) {
                if (inv.getItem().getId().equals(item.getId())) {
                    comprat = true;
                    equipat = inv.getEquipado() != null ? inv.getEquipado() : false; 
                    break;
                }
            }

            dto.put("comprat", comprat);
            dto.put("equipat", equipat);
            
            respuesta.add(dto);
        }

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/comprar/{itemId}")
    public ResponseEntity<?> comprarItem(@PathVariable Long itemId, Principal principal) {

        Usuari usuari = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        Optional<Inventari> inventariExistente = inventariRepository.findByUsuariIdAndItemId(usuari.getId(), item.getId());
        
        if (inventariExistente.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ya tienes este item en tu inventario"));
        }

        if (usuari.getPunts() < item.getPreu()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No tienes suficientes puntos para comprar este item"));
        }

        usuari.setPunts(usuari.getPunts() - item.getPreu());
        usuariRepository.save(usuari);

        Inventari nuevoInventari = new Inventari();
        nuevoInventari.setUsuari(usuari);
        nuevoInventari.setItem(item);
        nuevoInventari.setDataCompra(LocalDateTime.now());
        nuevoInventari.setEquipado(false);
        inventariRepository.save(nuevoInventari);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Item comprado correctamente");
        response.put("nouSaldo", usuari.getPunts());

        return ResponseEntity.ok(response);
    }
}
