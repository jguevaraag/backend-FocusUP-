package com.focusup.backend.service;

import com.focusup.backend.model.Recordatori;
import com.focusup.backend.model.Usuari;
import com.focusup.backend.repository.RecordatoriRepository;
import com.focusup.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CastigoService {

    @Autowired
    private RecordatoriRepository recordatoriRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    // Se ejecuta cada día a las 00:00 exactas
    @Scheduled(cron = "0 0 0 * * *")
    public void aplicarCastigoNocturno() {
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Buscamos a los infractores
        List<Recordatori> recordatoriosOlvidados = recordatoriRepository
                .findByDataHoraBeforeAndCompletatFalseAndPenalitzatFalse(ahora);

        // 2. Aplicamos el castigo uno por uno
        for (Recordatori rec : recordatoriosOlvidados) {
            Usuari usuari = rec.getUsuari();

            int puntosActuales = usuari.getPunts();
            int puntosDeMulta = 10; // Le quitamos 10 puntos por tarea olvidada

            // Usamos Math.max para que si tiene 5 puntos, no se quede en -5 (se queda en 0)
            usuari.setPunts(Math.max(0, puntosActuales - puntosDeMulta));
            usuariRepository.save(usuari);

            // 3. Marcamos la tarea como "multada" para no volver a cobrarle mañana
            rec.setPenalitzat(true);
            recordatoriRepository.save(rec);
        }

        System.out.println("Castigo nocturno completado: " + recordatoriosOlvidados.size() + " tareas penalizadas.");
    }
}
