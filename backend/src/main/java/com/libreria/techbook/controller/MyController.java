package com.libreria.techbook.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.libreria.techbook.model.LibreriaUser;
import com.libreria.techbook.model.Prodotto;
import com.libreria.techbook.model.User;
import com.libreria.techbook.service.ProdottoJDBCTemp;

import org.springframework.ui.Model;

@Controller
public class MyController {

    private final ProdottoJDBCTemp prodottoJDBCTemp;
    
    @Autowired
    public MyController(ProdottoJDBCTemp prodottoJDBCTemp) {
        this.prodottoJDBCTemp = prodottoJDBCTemp;
    }
    
    

    /* la rotta Homepage che ti dirotta in una pagina web o l'altra in base al login */   
    /**
     * Mostra la pagina di home page in base allo stato del login dell'utente.
     * Se l'utente non e' loggato, mostra la pagina "vetrinaLogout" con la lista degli utenti 
     * e la lista dei prodotti.
     * Se l'utente e' loggato come admin, mostra la pagina "vetrinaAdmin" con le informazioni dell'utente.
     * Se l'utente e' loggato come utente normale, mostra la pagina "vetrinaLogin" con le informazioni dell'utente.
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente corrente
     * @return la stringa della pagina da visualizzare
     */
    @GetMapping("/")
    public String mostraListaUsers(Model model, HttpSession session) {
        
        prodottoJDBCTemp.creaNuovaTabUsers();
        prodottoJDBCTemp.creaNuovaTabLibri();

        User userLoggato = (User) session.getAttribute("userLoggato");
        
        if (userLoggato == null) {
            // Utente non loggato
            List<User> listUsers = prodottoJDBCTemp.ritornaUsers();
            ArrayList<Prodotto> lista = prodottoJDBCTemp.ritornaProdotto();
            model.addAttribute("listUsers", listUsers);
            model.addAttribute("lista", lista);
            return "vetrinaLogout";
        } else if (userLoggato.getUsername().equals("admin")) {
            // Utente loggato come admin
            model.addAttribute("userLoggato", userLoggato);
            return "vetrinaAdmin";
        }
        else {
            // Utente loggato
           model.addAttribute("userLoggato", userLoggato);
            return "vetrinaLogin";
        }
    }

    /* rotta che posta alla pagina di login senza effettuare nessuna operazione */
    /**
     * Mostra la pagina di login.
     * La pagina di login richiede all'utente di inserire le credenziali per effettuare il login.
     * La pagina e vuota e non effettua alcuna operazione.
     * @return la pagina di login
     */
    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }

    /* Questa rotta serve per effettuare il login riceve i dati dalla pagina loginPage e li confronta per vedere se sono corretti
     * e se si logga un normale utente oppure un amministratore
     */
    /**
     * Esegue il login dell'utente.
     * La funzione controlla se l'utente esiste e se le credenziali sono corrette.
     * Se l'utente e l'amministratore, redirige alla pagina "vetrinaAdmin".
     * Se l'utente e un utente normale, redirige alla pagina "vetrinaLogin".
     * Se le credenziali sono errate, redirige alla pagina "loginPage" con un messaggio di errore.
     * @param username lo username dell'utente
     * @param password la password dell'utente
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente
     * @return la stringa della pagina da visualizzare
     */
    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {
        List<User> listUsers = prodottoJDBCTemp.ritornaUsers();

        for (User user : listUsers) {
            if((user.getUsername().equals(username) && username.equals("admin")) && (user.getPassword().equals(password) && password.equals("admin"))) {
                session.setAttribute("userLoggato", user);  // memorizza l'utente in sessione come amministratore
                return "vetrinaAdmin";
            }
            else if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                session.setAttribute("userLoggato", user);  // memorizza l'utente in sessione come utente
                model.addAttribute("userLoggato", user);
                return "vetrinaLogin";
            }
        }

        model.addAttribute("error", "Credenziali non valide");
        return "loginPage";
    }

    
    /**
     * Registrazione di un nuovo utente.
     * La funzione controlla se l'utente e già esistente, se si reindirizza alla pagina di login con un messaggio di errore.
     * Se l'utente non esiste, crea un nuovo utente e lo registra nel database.
     * @param model il modello passato alla vista
     * @param username lo username dell'utente da registrare
     * @param email l'email dell'utente da registrare
     * @param password la password dell'utente da registrare
     * @return la stringa "vetrinaLogin" che indica la pagina da visualizzare
     */
    @PostMapping("/register")
    public String registrazione(Model model, HttpSession session, @RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password) {
        List<User> listUsers = prodottoJDBCTemp.ritornaUsers();
        User user = new User();
        String nomeLibreria = prodottoJDBCTemp.creaLibreriaName(username); // crea il nome della libreria derivante dall'username
        for ( User utente : listUsers) {
            if (utente.getUsername().equals(username) || utente.getEmail().equals(email) || utente.getPassword().equals(password) || utente.getNomeLibreria().equals(nomeLibreria)) {
                model.addAttribute("error", "User già esistente");
                return "loginPage";
            }
           
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setNomeLibreria(nomeLibreria);
        user.setPunteggio(0);

        int punteggio = 0;
        prodottoJDBCTemp.createUser(user, username, email, password, punteggio);
        
        prodottoJDBCTemp.creaLibreriaUser(user.getNomeLibreria()); // crea la tabella libreria per l'utente nell'sql
        session.setAttribute("userLoggato", user);  // memorizza l'utente in sessione come utente
        model.addAttribute("userLoggato", user);        
        return "vetrinaLogin";
    }

    /**
     * Mostra la pagina del profilo dell'utente loggato, visualizzando le informazioni dell'utente e la lista delle librerie associate all'utente.
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente corrente
     * @return la stringa "profiloPage" che indica la pagina da visualizzare
     */
    @GetMapping("/profilo")
    public String getProfilo(Model model,
                              HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");
        ArrayList<LibreriaUser> listaLibrerie = prodottoJDBCTemp.ritornaLibreria(userLoggato.getNomeLibreria());

        model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("listaLibrerie", listaLibrerie);

        return "profiloPage";
    }

    @PostMapping("eliminaProfilo")
    public String getEliminaProfilo(Model model) {
        
        
    
        return "confermaEliminazioneUserPage";
    }

    

    /**
     * La funzione si occupa di eliminare l'utente corrente e la sua libreria
     * e di invalidare la sessione.
     * 
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente corrente
     * @return la stringa "redirect:/" che indica di reindirizzare alla home page
     */
    @PostMapping("eliminazioneConfermata")
    public String getEliminazioneConfermata(Model model, HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");
        
        try {
            prodottoJDBCTemp.eliminaUser(userLoggato.getNomeLibreria());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        session.invalidate();
        return "redirect:/";
    }
    
    

    /**
     * Mostra la pagina con la lista dei libri presenti nel database.
     * La lista e' composta da oggetti Prodotto, ciascuno dei quali rappresenta un libro
     * con i propri attributi: id, titolo, genere, autore e copertina.
     * 
     * @param model il modello passato alla vista
     * @return la stringa "libriPage" che indica la pagina da visualizzare
     */
    @GetMapping("/libri")
    public String getListaLibri(Model model) {
        ArrayList<Prodotto> listaLibri = prodottoJDBCTemp.ritornaProdotto();
        model.addAttribute("listaLibri", listaLibri);
        return "libriPage";
    }
    
   
    /**
     * La funzione si occupa di aggiungere un libro alla libreria dell'utente corrente.
     * La funzione riceve come parametro l'id del libro da aggiungere e l'oggetto session
     * contenente l'utente corrente.
     * La funzione controlla se l'utente e' loggato, se si chiama la funzione
     * aggiungiLibroAllaLibreria per aggiungere il libro alla sua libreria.
     * Infine restituisce la stringa "/libriPage" che indica la pagina da visualizzare.
     * @param idLibro l'id del libro da aggiungere
     * @param session l'oggetto session contenente l'utente corrente
     * @return la stringa "/libriPage" che indica la pagina da visualizzare
     */
    @PostMapping("/aggiungiLibro")
    public String aggiungiLibro(@RequestParam("idLibro") int idLibro, HttpSession session) {
        User user = (User) session.getAttribute("userLoggato");
        if (user != null) {
            prodottoJDBCTemp.aggiungiLibroAllaLibreria(user.getNomeLibreria(), idLibro);
    }
    return "/libriPage"; // oppure altra vista
    }
    
    
/**
 * Rimuove un libro dalla libreria dell'utente corrente.
 * La funzione riceve come parametro l'id del libro da rimuovere e verifica se l'utente è loggato.
 * Se l'utente è loggato, chiama la funzione rimuoviLibroDaLibreria per rimuovere il libro dalla sua libreria.
 * Restituisce la stringa "/profiloPage" che indica la pagina da visualizzare con la lista aggiornata delle librerie.
 * In caso contrario, restituisce la stringa "/" che indica la pagina da visualizzare in caso di errore.
 * @param idLibro l'id del libro da rimuovere
 * @param model il modello passato alla vista
 * @param session l'oggetto session contenente l'utente corrente
 * @return la stringa "/profiloPage" o "/" che indica la pagina da visualizzare
 */
    @PostMapping("/rimuoviLibro")
    public String rimuoviLibro(@RequestParam("idLibro") int idLibro,Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLoggato");
            if (user != null) {
                try {
                    prodottoJDBCTemp.rimuoviLibroDaLibreria(user.getNomeLibreria(), idLibro);
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ArrayList<LibreriaUser> listaLibrerie = prodottoJDBCTemp.ritornaLibreria(user.getNomeLibreria());

                model.addAttribute("userLoggato", user);
                model.addAttribute("listaLibrerie", listaLibrerie);
                return "/profiloPage"; 
            }
    
       return "/";
    }



    /* questa rotta serve per effettuare il logout dalla sessione */
    /**
     * Questa funzione effettua il logout dell'utente dalla sessione e
     * reindirizza alla home page.
     * 
     * @param session la sessione dell'utente corrente
     * @return la stringa "redirect:/" che indica di reindirizzare alla home page
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // cancella tutto dalla sessione
        return "redirect:/";
    }
}

