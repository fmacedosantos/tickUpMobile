package com.example.tickup;

import com.google.gson.annotations.SerializedName;

public class Ticket {
    @SerializedName("idIngresso")
    private String ticketId;

    @SerializedName("nomeEvento")
    private String eventName;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}