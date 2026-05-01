package com.focusup.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventari_usuari") 
public class Inventari {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "data_compra")
    private LocalDateTime dataCompra;

    @Column(name = "equipado")
    private Boolean equipado = false; 


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; 
}
