package com.libreria.techbook.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.libreria.techbook.model.Prodotto;
import com.libreria.techbook.model.User;

@Component
public class ProdottoJDBCTemp {

    private JdbcTemplate jdbcTemplateObject;

    /**
     * Inietta il bean JdbcTemplate necessario per eseguire query
     * sul database.
     * 
     * @param jdbcTemplateObject il bean JdbcTemplate
     */
    @Autowired
    public void setJdbcTemplateObject(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
    }   
    
    /**
     * Ritorna una lista di oggetti Prodotto contenenti i dati presenti
     * nella tabella "libri" del database.
     * 
     * @return lista di oggetti Prodotto
     */
    public ArrayList<Prodotto> ritornaProdotto() {
        try {
            String query = "SELECT * FROM libri";
            return jdbcTemplateObject.query(query, new ResultSetExtractor<ArrayList<Prodotto>>() {
                @Override
                public ArrayList<Prodotto> extractData(ResultSet rs) throws SQLException {
                    ArrayList<Prodotto> listaProd = new ArrayList<>();
                    while (rs.next()) {
                        Prodotto prodotto = new Prodotto();
                        prodotto.setId(rs.getInt("id"));
                        prodotto.setTitolo(rs.getString("titolo"));
                        prodotto.setGenere(rs.getString("genere"));
                        prodotto.setAutore(rs.getString("autore"));
                        prodotto.setCopertina(rs.getString("copertina"));
                        
                      
                        listaProd.add(prodotto);
                    }
                    return listaProd;
                }
            });
        } catch (Exception e) {
            // Gestione dell'errore
            return new ArrayList<>();
        }
    }
      
    /**
     * Crea una nuova tabella chiamata "libri" nel database 
     * se essa non esiste gia. La tabella ha 5 colonne: 
     * id, titolo, genere, autore, copertina.
     * 
     * @author Alessandro Bugatti
     */
    public void creaNuovaTabLibri() {
  

    	// Query SQL per verificare se la tabella esiste
        String checkTableQuery = "SHOW TABLES LIKE 'libri'";
     	
     	
        // Query SQL per creare una nuova tabella
        String query = "CREATE TABLE libri (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "titolo VARCHAR(50)," +
                "genere VARCHAR(50)," +
                "autore VARCHAR(50)," +
                "copertina VARCHAR(1000)" +
                ")";
        			
        
        try {
            // Esegui la query di controllo
            List<Map<String, Object>> tables = jdbcTemplateObject.queryForList(checkTableQuery);

            // Se la tabella non esiste, crea la tabella
            if (tables.isEmpty()) {
                jdbcTemplateObject.execute(query);
            } 
        } catch (Exception e) {
            // Gestione dell'errore
            e.printStackTrace();
        }
        
       
    }

    /**
     * Crea una nuova tabella chiamata "nomeLibreria" nel database 
     * se essa non esiste gia. La tabella ha 6 colonne: 
     * idLibreria, idLibro, TitoloLibro, genereLibro, autoreLibro, copertinaLibro.
     * La tabella e' utilizzata per contenere i libri presenti nella libreria dell'utente.
     * 
     * @param nomeLibreria il nome della libreria da creare
     * @author Alessandro Bugatti
     */
    public void creaLibreriaUser(String nomeLibreria) {
  
        
    	// Query SQL per verificare se la tabella esiste
        String checkTableQuery = "SHOW TABLES LIKE '" + nomeLibreria + "'";
     	
     	
        // Query SQL per creare una nuova tabella
        String query = "CREATE TABLE " + nomeLibreria + " (" +
                "idLibreria INT AUTO_INCREMENT PRIMARY KEY," +
                "idLibro INT," +
                "TitoloLibro VARCHAR(50)," +
                "genereLibro VARCHAR(50)," +
                "autoreLibro VARCHAR(50)," +
                "copertinaLibro VARCHAR(1000)" +
                ")";
        			
        
        try {
            // Esegui la query di controllo
            List<Map<String, Object>> tables = jdbcTemplateObject.queryForList(checkTableQuery);

            // Se la tabella non esiste, crea la tabella
            if (tables.isEmpty()) {
                jdbcTemplateObject.execute(query);
            } 
        } catch (Exception e) {
            // Gestione dell'errore
            e.printStackTrace();
        }
        
       
    }
    
    
/**
 * Crea una nuova tabella chiamata 'users' nel database se non esiste già.
 * La tabella include le colonne: userid, username, email, password e punteggio.
 * Se la tabella esiste già, non viene eseguita alcuna azione.
 * In caso di errore durante l'esecuzione della query, l'eccezione verrà stampata.
 */

    public void creaNuovaTabUsers() {
        // Query SQL per verificare se la tabella esiste
        String checkTableQuery = "SHOW TABLES LIKE 'users'";

        // Query SQL per creare una nuova tabella
        String createTableQuery = "CREATE TABLE users (" +
                                "userid INT AUTO_INCREMENT PRIMARY KEY, " +
                                "username VARCHAR(50) NOT NULL UNIQUE, " +
                                "email VARCHAR(50) NOT NULL UNIQUE, " +
                                "password VARCHAR(50) NOT NULL, " +
                                "nomelibreria VARCHAR(50) NOT NULL, " +
                                "punteggio INT DEFAULT 0" +
                                ")";

        try {
            // Esegui la query di controllo
            List<Map<String, Object>> tables = jdbcTemplateObject.queryForList(checkTableQuery);

            // Se la tabella non esiste, crea la tabella
            if (tables.isEmpty()) {
                jdbcTemplateObject.execute(createTableQuery);
            }
        } catch (Exception e) {
            System.err.println("Errore nella creazione della tabella users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * Ritorna la lista di tutti gli utenti presenti nella tabella 'users' del database.
     * La lista contiene oggetti di tipo User, ciascuno dei quali rappresenta un utente
     * con i propri attributi: userid, username, email, password, nomelibreria e punteggio.
     * Se si verifica un errore durante l'esecuzione della query, viene ritornata una lista vuota.
     * @return lista di utenti
     */
    public ArrayList<User> ritornaUsers() {
        try {
            String query = "SELECT * FROM users";
            return jdbcTemplateObject.query(query, new ResultSetExtractor<ArrayList<User>>() {
                @Override
                public ArrayList<User> extractData(ResultSet rs) throws SQLException {
                    ArrayList<User> listUsers = new ArrayList<>();
                    while (rs.next()) {
                    	User user = new User();
                        user.setUserId(rs.getInt("userid"));
                        user.setUsername(rs.getString("username"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                        user.setNomeLibreria(rs.getString("nomelibreria"));
                        user.setPunteggio(rs.getInt("punteggio"));
                     
                        listUsers.add(user);
                    }
                    return listUsers;
                }
            });
        } catch (Exception e) {
            // Gestione dell'errore
            return new ArrayList<>();
        }
    }

    /* Metodo per creare il nome della libreria derivante dall'username */
    public String creaLibreriaName(String username) {
        String pulita = username.replaceAll("[^a-zA-Z0-9]", "");
        String nameLibreria = pulita.toLowerCase();
        return "libreria_" + nameLibreria;    
    }
    /* Metodo per creare un user */
    public void createUser(User user, String username, String email, String password, int punteggio) {
        
        String query = "INSERT INTO users (username, email, password, nomelibreria, punteggio) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplateObject.update(query, user.getUsername(), user.getEmail(), user.getPassword(), user.getNomeLibreria(), user.getPunteggio());
    }
    
    // Metodo per eseguire query DDL
    public void executeDDLQuery(String query) {
        try {
            jdbcTemplateObject.execute(query);
        } catch (Exception e) {
            // Gestione dell'errore
        }
    }

}

