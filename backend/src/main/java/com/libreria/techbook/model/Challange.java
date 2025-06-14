package com.libreria.techbook.model;

import java.time.LocalDate;


public class Challange {

    private int idRecord;
    private LocalDate dataInizio;
    private String nomePartecipante;
    private int punteggio;
    
    public int getIdRecord() {
        return idRecord;
    }
    public void setIdRecord(int idRecord) {
        this.idRecord = idRecord;
    }
    public LocalDate getDataInizio() {
        return dataInizio;
    }
    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }
    public String getNomePartecipante() {
        return nomePartecipante;
    }
    public void setNomePartecipante(String nomePartecipante) {
        this.nomePartecipante = nomePartecipante;
    }
    public int getPunteggio() {
        return punteggio;
    }
    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
    
   
    


}
