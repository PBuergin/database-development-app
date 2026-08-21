package ch.hftm.db2.ticketsystem.ticket;

interface TicketStatusPriorityReportProjection {

    String getStatus();

    String getPriority();

    long getTicketCount();

    long getCommentCount();
}
