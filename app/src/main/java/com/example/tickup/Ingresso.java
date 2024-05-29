package com.example.tickup;

import com.google.gson.annotations.SerializedName;

public class Ingresso {
    @SerializedName("idIngresso")
    private String idIngresso;

    @SerializedName("nomeEvento")
    private String nomeEvento;

    public String getIdIngresso() {
        return idIngresso;
    }

    public void setIdIngresso(String idIngresso) {
        this.idIngresso = idIngresso;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }
}