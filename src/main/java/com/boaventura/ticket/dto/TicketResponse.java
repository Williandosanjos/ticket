package com.boaventura.ticket.dto;

import com.boaventura.ticket.entity.Ticket;
import com.boaventura.ticket.enums.Prioridade;
import com.boaventura.ticket.enums.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(

        UUID id,
        String titulo,
        String descricao,
        String emailUsuario,
        Prioridade prioridade,
        Status status,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm

) {
    public static TicketResponse de(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescricao(),
                ticket.getEmailUsuario(),
                ticket.getPrioridade(),
                ticket.getStatus(),
                ticket.getCriadoEm(),
                ticket.getAtualizadoEm()
        );
    }
}
