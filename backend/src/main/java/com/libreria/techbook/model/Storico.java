package com.libreria.techbook.model;

import java.time.LocalDate;


public class Storico {

    private int idChallange;
    private LocalDate data;
    private String nomeChallange;
    private int condizione;
    private String nomeVincitore;
    private int punti;
    private int stato;

   
    
    
    public int getIdChallange() {
        return idChallange;
    }
    public void setIdChallange(int idChallange) {
        this.idChallange = idChallange;
    }
    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }
    public String getNomeChallange() {
        return nomeChallange;
    }
    public void setNomeChallange(String nomeChallange) {
        this.nomeChallange = nomeChallange;
    }
    public int getCondizione() {
        return condizione;
    }
    public void setCondizione(int condizione) {
        this.condizione = condizione;
    }
    public String getNomeVincitore() {
        return nomeVincitore;
    }
    public void setNomeVincitore(String nomeVincitore) {
        this.nomeVincitore = nomeVincitore;
    }
    public int getPunti() {
        return punti;
    }
    public void setPunti(int punti) {
        this.punti = punti;
    }
     public int getStato() {
        return stato;
    }
    public void setStato(int stato) {
        this.stato = stato;
    }
    
    
    
    

}
