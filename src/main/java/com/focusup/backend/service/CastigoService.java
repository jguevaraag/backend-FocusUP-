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

    @Scheduled(cron = "0 0 0 * * *")
    public void aplicarCastigoNocturno() {
        LocalDateTime ahora = LocalDateTime.now();

      
        List<Recordatori> recordatoriosOlvidados = recordatoriRepository
                .findByDataHoraBeforeAndCompletatFalseAndPenalitzatFalse(ahora);

     
        for (Recordatori rec : recordatoriosOlvidados) {
            Usuari usuari = rec.getUsuari();

            int puntosActuales = usuari.getPunts();
            int puntosDeMulta = 10;

            usuari.setPunts(Math.max(0, puntosActuales - puntosDeMulta));
            usuariRepository.save(usuari);
            rec.setPenalitzat(true);
            recordatoriRepository.save(rec);
        }

        System.out.println("Castigo nocturno completado: " + recordatoriosOlvidados.size() + " tareas penalizadas.");
    }
}
