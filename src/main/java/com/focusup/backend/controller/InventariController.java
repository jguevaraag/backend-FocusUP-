package com.focusup.backend.controller;

import com.focusup.backend.model.Inventari;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.InventariRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventari")
public class InventariController {

    @Autowired
    private InventariRepository inventariRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    @PostMapping("/equipar/{itemId}")
    public ResponseEntity<?> equiparItem(@PathVariable Long itemId, Principal principal) {
        
        Usuari usuari = usuariRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Inventari itemAEquipar = inventariRepository.findByUsuariIdAndItemId(usuari.getId(), itemId)
                .orElseThrow(() -> new RuntimeException("No tienes este objeto en tu inventario"));

        String tipoItem = itemAEquipar.getItem().getTipus();

        if (!tipoItem.equals("MUSICA") && !tipoItem.equals("Personsalizacion")) {
             return ResponseEntity.badRequest().body(Map.of("error", "Este tipo de objeto no se puede equipar todavía."));
        }

        List<Inventari> equipadosDeEseTipo = inventariRepository.buscarEquipadosPorTipo(usuari.getId(), tipoItem);
        for (Inventari equipadoAnterior : equipadosDeEseTipo) {
            equipadoAnterior.setEquipado(false);
            inventariRepository.save(equipadoAnterior);
        }

        itemAEquipar.setEquipado(true);
        inventariRepository.save(itemAEquipar);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Objeto equipado correctamente",
                "item_equipado", itemAEquipar.getItem().getNom(),
                "tipo", tipoItem
        ));
    }
}
