package com.example.eventmanagement.repository;

import com.example.eventmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
}

