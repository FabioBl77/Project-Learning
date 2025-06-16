package com.libreria.techbook.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.libreria.techbook.model.Challange;
import com.libreria.techbook.model.LibreriaUser;
import com.libreria.techbook.model.Prodotto;
import com.libreria.techbook.model.Storico;
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
        prodottoJDBCTemp.creaStoricoChallanger();

        User userLoggato = (User) session.getAttribute("userLoggato");
        
        if (userLoggato == null) {
            // Utente non loggato
            List<User> listUsers = prodottoJDBCTemp.ritornaUsers();
            ArrayList<Prodotto> lista = prodottoJDBCTemp.ritornaProdotto();
            List<Storico> listaStorico = prodottoJDBCTemp.ritornaStorico();
            model.addAttribute("listaStorico", listaStorico);
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
              // memorizza l'utente in sessione come utente
            List<Prodotto> listProdotto = prodottoJDBCTemp.ritornaProdotto();
            List<Prodotto> topLibri = listProdotto.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getLetture(), p1.getLetture()))
                .limit(3)
                .collect(Collectors.toList());

                model.addAttribute("topLibri", topLibri);
                
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

    /**
     * Mostra la pagina di registrazione.
     * La pagina di registrazione richiede all'utente di inserire le credenziali per creare un nuovo utente.
     * La pagina e vuota e non effettua alcuna operazione.
     * @return la pagina di registrazione
     */
    @GetMapping("/registerPage")
    public String registerPage() {
        return "registerPage";
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
        List<Prodotto> listProdotto = prodottoJDBCTemp.ritornaProdotto();

        for (User user : listUsers) {
            if((user.getUsername().equals(username) && username.equals("admin")) && (user.getPassword().equals(password) && password.equals("admin"))) {
                session.setAttribute("userLoggato", user);  // memorizza l'utente in sessione come amministratore
                return "vetrinaAdmin";
            }
            else if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                session.setAttribute("userLoggato", user);  // memorizza l'utente in sessione come utente
                List<Prodotto> topLibri = listProdotto.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getLetture(), p1.getLetture()))
                .limit(3)
                .collect(Collectors.toList());

                model.addAttribute("topLibri", topLibri);
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
     * Mostra la pagina pre-profilo dell'utente, visualizzando le informazioni dell'utente e offrendo la possibilita' di accedere
     * alla sua libreria o di creare una challenge.
     * @param model il modello passato alla vista
     * @param session l'oggetto session contenente l'utente corrente
     * @return la stringa "preProfiloPage" che indica la pagina da visualizzare
     */
    @GetMapping("/preProfilo")
    public String getPreProfilo(Model model,
                              HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");
        model.addAttribute("userLoggato", userLoggato);
        return "preProfiloPage";
    }

    /**
     * Mostra la pagina della libreria dell'utente loggato.
     * La funzione recupera la lista dei libri presenti nella libreria dell'utente
     * e la passa alla vista "libreriaPage" insieme all'oggetto utente.
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente corrente
     * @return la stringa "libreriaPage" che indica la pagina da visualizzare
     */
    @GetMapping("/libreria")
    public String getLibreria(Model model, HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");
        ArrayList<LibreriaUser> listaLibrerie = prodottoJDBCTemp.ritornaLibreria(userLoggato.getNomeLibreria());
        model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("listaLibrerie", listaLibrerie);
        return "libreriaPage";
    }
    
    

    /**
     * Mostra la pagina del profilo dell'utente loggato, visualizzando le informazioni dell'utente.
     * @param model il modello passato alla vista
     * @param session la sessione dell'utente corrente
     * @return la stringa "profiloPage" che indica la pagina da visualizzare
     */
    @GetMapping("/profilo")
    public String getProfilo(Model model,
                              HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");
        model.addAttribute("userLoggato", userLoggato);
        return "profiloPage";
    }

    /**
     * Mostra la pagina di conferma dell'eliminazione dell'utente loggato.
     * La funzione reindirizza alla pagina di conferma dell'eliminazione dell'utente.
     * @param model il modello passato alla vista
     * @return la stringa "confermaEliminazioneUserPage" che indica la pagina da visualizzare
     */
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
    public String aggiungiLibro(@RequestParam("idLibro") int idLibro,Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLoggato");
        if (user != null) {
            prodottoJDBCTemp.aggiungiLibroAllaLibreria(user.getNomeLibreria(), idLibro);
    }
     ArrayList<Prodotto> listaLibri = prodottoJDBCTemp.ritornaProdotto();
        model.addAttribute("listaLibri", listaLibri);
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
    public String rimuoviLibro(@RequestParam("idLibro") int idLibro, Model model, HttpSession session) {
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
                return "/libreriaPage"; 
            }
    
       return "/";
    }

    /**
     * Mostra la pagina di lettura di un libro specificato.
     * La funzione riceve come parametro l'id del libro da visualizzare e verifica se l'utente è loggato.
     * Se l'utente è loggato, recupera il libro con l'id specificato e lo aggiunge al modello.
     * Restituisce la stringa "leggiLibroPage" che indica la pagina da visualizzare con il libro.
     * @param idLibro l'id del libro da visualizzare
     * @param model il modello passato alla vista
     * @param session l'oggetto session contenente l'utente corrente
     * @return la stringa "leggiLibroPage" che indica la pagina da visualizzare
     */
    @GetMapping("/leggiLibro")
    public String getMethodName(@RequestParam("idLibro") int idLibro, Model model, HttpSession session) {
        User user = (User) session.getAttribute("userLoggato");
        
      
        Prodotto libro = prodottoJDBCTemp.getLibroById(idLibro);
            
        model.addAttribute("libro", libro);
        

        return "leggiLibroPage";
    }
    


    @GetMapping("/creaChallenge")
    public String getCreaChallenge(@RequestParam ("challange") String challenge, @RequestParam ("tempo") String tempoSelect, @RequestParam ("scelta") String condizione, Model model, HttpSession session) {
       
        int tempo = 0;
        switch (tempoSelect) {
            case "settimana":
                tempo = 7;
                break;
            case "dieci":
                tempo = 10;
                break;
            case "mese":
                tempo = 30;
                break;
            default:
                tempo = 5;
                break;
        }
       
        String alertStato;
        User userLoggato = (User) session.getAttribute("userLoggato");
        Storico storico = new Storico();
        Challange newChallange = new Challange();
        String challengeName = prodottoJDBCTemp.creaChallangeName(challenge);
        prodottoJDBCTemp.creaTabellaChallange(challengeName);
        newChallange.setDataInizio(LocalDate.now());
        newChallange.setNomePartecipante(userLoggato.getUsername());
        newChallange.setPunteggio(0);
        prodottoJDBCTemp.insertUserCallange(challengeName, newChallange, newChallange.getDataInizio(), newChallange.getNomePartecipante(), newChallange.getPunteggio());

        storico.setData(newChallange.getDataInizio());
        storico.setDataFine(newChallange.getDataInizio().plusDays(tempo));
        storico.setNomeChallange(challengeName);
        storico.setCondizione(condizione);
        storico.setNomeVincitore("In Corso");
        storico.setPunti(0);
        storico.setStato(0);
        if (storico.getStato() == 0) {
            alertStato = "In Corso";
        }else {
            alertStato = "Terminato";
        }
        prodottoJDBCTemp.insertStoricoCallange(storico, storico.getData(), storico.getDataFine(), storico.getNomeChallange(), storico.getCondizione(), storico.getNomeVincitore(), storico.getPunti(), storico.getStato());

        model.addAttribute("dataFine", storico.getDataFine());
        model.addAttribute("newChallange", newChallange);
        model.addAttribute("storico", storico);
        model.addAttribute("alertStato", alertStato);



        return "creaChallangeConfermata";
    }

    @GetMapping("/preCreaChallange")
public String getMethodName(Model model, HttpSession session) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    ArrayList<Prodotto> lista = prodottoJDBCTemp.ritornaProdotto();

    Set<String> generiUnici = new TreeSet<>();
    Set<String> autoriUnici = new TreeSet<>();

    for (Prodotto p : lista) {
        if (p.getGenere() != null && !p.getGenere().isBlank()) {
            generiUnici.add(p.getGenere());
        }
        if (p.getAutore() != null && !p.getAutore().isBlank()) {
            autoriUnici.add(p.getAutore());
        }
    }


    model.addAttribute("genereLibri", generiUnici);
    model.addAttribute("autoreLibri", autoriUnici);
    model.addAttribute("userLoggato", userLoggato);

    return "preCreaChallangePage";
}

    
    @GetMapping("/preChallenge")
    public String getPreCreaChallenge(Model model, HttpSession session) {
        return "preChallengePage";
    }

    @GetMapping("/vediChallange")
    public String getVediChallange(Model model, HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");

        
       // 1. Aggiorna lo stato delle challenge concluse
        List<Storico> listaStoricoFine = prodottoJDBCTemp.ritornaStorico();
        for (Storico storico : listaStoricoFine) {
            if (storico.getStato() != 1 && !storico.getDataFine().isAfter(LocalDate.now())) {
                List<Challange> listaPartecipazioni = prodottoJDBCTemp.ritornaChallange(storico.getNomeChallange());

                if (listaPartecipazioni != null && !listaPartecipazioni.isEmpty()) {
                    // Ordina per punteggio decrescente
                    listaPartecipazioni.sort(Comparator.comparingInt(Challange::getPunteggio).reversed());

                    // Imposta vincitore e stato
                    Challange vincitore = listaPartecipazioni.get(0);
                    storico.setStato(1);
                    storico.setNomeVincitore(vincitore.getNomePartecipante());
                    storico.setPunti(vincitore.getPunteggio());

                    // Aggiorna nel DB
                    prodottoJDBCTemp.updateStatoStorico(
                        storico.getIdChallange(),
                        storico.getNomeVincitore(),
                        storico.getPunti(),
                        storico.getStato()
                    );
                }
            }
        }

        List<Storico> listaStorico = prodottoJDBCTemp.ritornaStorico();


        model.addAttribute("listaStorico", listaStorico);
        model.addAttribute("userLoggato", userLoggato);
        return "vediChallangePage";
    }
    
   @PostMapping("/dettagliChallange")
public String getDettagliChallange(Model model, HttpSession session,
                                   @RequestParam("nomeChallange") String nomeChallange,
                                   @RequestParam("fineChallange") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fineChallange) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    List<Challange> challange = prodottoJDBCTemp.ritornaChallange(nomeChallange);

    // Ordina la lista per punteggio decrescente
    challange.sort(Comparator.comparingInt(Challange::getPunteggio).reversed());

    model.addAttribute("challange", challange);
    model.addAttribute("userLoggato", userLoggato);
    model.addAttribute("nomeChallange", nomeChallange);
    model.addAttribute("fineChallange", fineChallange);

    return "dettagliChallangePage";
}

    
@PostMapping("/addPartecipante")
public String addPartecipante(Model model, @RequestParam("nomeChallange") String nomeChallange, @RequestParam("fineChallange") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fineChallange, HttpSession session, RedirectAttributes redirectAttributes) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    Challange partecipante = new Challange();
    Storico storico = new Storico();
    ArrayList<Storico> listaStorico = prodottoJDBCTemp.ritornaStorico();
    for (Storico s : listaStorico) { 
        if (s.getNomeChallange().equals(nomeChallange)) {
            storico.setStato(s.getStato());
         }
    }

    System.out.println(storico.getStato());
    if (userLoggato == null) {
        // Se l'utente non è loggato, reindirizza alla login o altra pagina
        return "redirect:/login";
    }
    if (storico.getStato() > 0) {
         model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("nomeChallange", nomeChallange);
        return "challangeTerminatoPage";

    }
    boolean isPartecipante = prodottoJDBCTemp.isUserPartecipante(nomeChallange, userLoggato.getUsername());
     if (isPartecipante) {
         model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("nomeChallange", nomeChallange);
        return "partecipazioneGiaEffettuata";
    } else {
        // Aggiungi il partecipante usando il metodo del prodottoJDBCTemp
        partecipante.setDataInizio(LocalDate.now());
        partecipante.setNomePartecipante(userLoggato.getUsername());
        partecipante.setPunteggio(0);
        prodottoJDBCTemp.insertUserCallange(nomeChallange, partecipante, partecipante.getDataInizio(), partecipante.getNomePartecipante(), partecipante.getPunteggio());

       
        model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("nomeChallange", nomeChallange);
        model.addAttribute("fineChallange", fineChallange);

    }

   
    return "confermaPartecipazione";
}




 @GetMapping("/classificaGlobale") 
public String getClassificaGlobale(Model model, HttpSession session) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    model.addAttribute("userLoggato", userLoggato);

    List<User> listUsers = prodottoJDBCTemp.ritornaUsers();

    // Ordina per punteggio decrescente
    listUsers.sort((u1, u2) -> Integer.compare(u2.getPunteggio(), u1.getPunteggio()));

    model.addAttribute("classifica", listUsers);

    return "classificaGlobalePage";
}

    @GetMapping("/mieChallange")
    public String getMieChallange(Model model, HttpSession session) {
        User userLoggato = (User) session.getAttribute("userLoggato");

        List<Challange> listaMieChallange = new ArrayList<>();
        List<Storico> listaMioStorico = new ArrayList<>();

        // 1. Aggiorna lo stato delle challenge concluse
        List<Storico> listaStoricoFine = prodottoJDBCTemp.ritornaStorico();
        for (Storico storico : listaStoricoFine) {
            if (storico.getStato() != 1 && !storico.getDataFine().isAfter(LocalDate.now())) {
                List<Challange> listaPartecipazioni = prodottoJDBCTemp.ritornaChallange(storico.getNomeChallange());

                if (listaPartecipazioni != null && !listaPartecipazioni.isEmpty()) {
                    // Ordina per punteggio decrescente
                    listaPartecipazioni.sort(Comparator.comparingInt(Challange::getPunteggio).reversed());

                    // Imposta vincitore e stato
                    Challange vincitore = listaPartecipazioni.get(0);
                    storico.setStato(1);
                    storico.setNomeVincitore(vincitore.getNomePartecipante());
                    storico.setPunti(vincitore.getPunteggio());

                    // Aggiorna nel DB
                    prodottoJDBCTemp.updateStatoStorico(
                        storico.getIdChallange(),
                        storico.getNomeVincitore(),
                        storico.getPunti(),
                        storico.getStato()
                    );
                }
            }
        }

        // 2. Estrai le challenge dell'utente loggato
        List<Storico> listaStorico = prodottoJDBCTemp.ritornaStorico();
        for (Storico storico : listaStorico) {
            List<Challange> partecipazioni = prodottoJDBCTemp.ritornaChallange(storico.getNomeChallange());

            for (Challange c : partecipazioni) {
                if (c.getNomePartecipante().equals(userLoggato.getUsername())) {
                    listaMieChallange.add(c);
                    listaMioStorico.add(storico);
                }
            }
        }
        
        // 3. Aggiungi attributi al model
        model.addAttribute("userLoggato", userLoggato);
        model.addAttribute("listaMioStorico", listaMioStorico);

        return "mieChallengePage";
    }


@PostMapping("/dettagliMiaChallange")
public String getDettagliMiaChallange(Model model, HttpSession session,
                                   @RequestParam("nomeChallange") String nomeChallange,
                                   @RequestParam("fineChallange") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fineChallange) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    List<Challange> challange = prodottoJDBCTemp.ritornaChallange(nomeChallange);

    // Ordina la lista per punteggio decrescente
    challange.sort(Comparator.comparingInt(Challange::getPunteggio).reversed());

    model.addAttribute("challange", challange);
    model.addAttribute("userLoggato", userLoggato);
    model.addAttribute("nomeChallange", nomeChallange);
    model.addAttribute("fineChallange", fineChallange);
    return "dettagliMiaChallangePage";
}

@PostMapping("/confermaLibroLetto")
public String getLeggiLibro(Model model, HttpSession session, @RequestParam("idLibro") int idLibro) {
    User userLoggato = (User) session.getAttribute("userLoggato");
    List<Storico> storico = prodottoJDBCTemp.ritornaStorico();
    ArrayList<Prodotto> listaLibri = prodottoJDBCTemp.ritornaProdotto();
    Prodotto libroLetto = null;

    for (Prodotto libro : listaLibri) {
        if (libro.getId() == idLibro) {
            // Aggiorna le letture del libro
            libro.setLetture(libro.getLetture() + 1);
            prodottoJDBCTemp.updateLettureLibro(libro.getId(), libro.getLetture());
            libroLetto = libro;
            break;
        }
    }

    // Se il libro non è stato trovato, reindirizza a errore
    if (libroLetto == null) {
        return "redirect:/errore"; // oppure una pagina che gestisce l'errore
    }

    // Aggiorna punteggio utente
    userLoggato.setPunteggio(userLoggato.getPunteggio() + 1);
    prodottoJDBCTemp.updatePunteggioUser(userLoggato.getUsername(), userLoggato.getPunteggio());

    // Aggiorna punteggio challenge se l'utente partecipa e la condizione è soddisfatta
    for (Storico s : storico) {
        if (s.getStato() != 0) continue;

        List<Challange> partecipazioni = prodottoJDBCTemp.ritornaChallange(s.getNomeChallange());
        for (Challange c : partecipazioni) {
            if (!c.getNomePartecipante().equals(userLoggato.getUsername())) continue;

            boolean soddisfaCondizione = false;

            if ("nessuna".equalsIgnoreCase(s.getCondizione())) {
                soddisfaCondizione = true;
            } else {
                String condizione = s.getCondizione();
                String genere = libroLetto.getGenere();
                String autore = libroLetto.getAutore();

                if ((genere != null && genere.equalsIgnoreCase(condizione)) ||
                    (autore != null && autore.equalsIgnoreCase(condizione))) {
                    soddisfaCondizione = true;
                }
            }

            if (soddisfaCondizione) {
                c.setPunteggio(c.getPunteggio() + 1);
                prodottoJDBCTemp.updatePunteggioChallange(s.getNomeChallange(), c.getNomePartecipante(), c.getPunteggio());
            }
        }
    }

    model.addAttribute("userLoggato", userLoggato);
    model.addAttribute("libroLetto", libroLetto);
    return "confermaLibroLettoPage";
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

