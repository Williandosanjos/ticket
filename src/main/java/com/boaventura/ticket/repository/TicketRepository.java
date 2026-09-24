package com.boaventura.ticket.repository;

import com.boaventura.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    Optional<Ticket> findByEmailUsuario(String emailUsuario);

    boolean existsByEmailUsuario(String emailUsuario);

}
