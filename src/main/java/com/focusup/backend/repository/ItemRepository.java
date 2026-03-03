package com.focusup.backend.repository;

import com.focusup.backend.model.Item; // O ItemBotiga si lo llamaste así
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    
}
