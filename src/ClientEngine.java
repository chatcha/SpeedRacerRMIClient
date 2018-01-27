
import java.io.Serializable;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author brasc
 */
public class ClientEngine extends Observable implements ClientInterface, IClient, Serializable {

    private static final long serialVersionUID = 2533181686936469703L;
    private static final Logger LOGGER = Logger.getLogger(ClientEngine.class
            .getName());
    private String name;
    private GameEngineInterface server;
    private GUI gui;
    private Arena arena;
    private Map<String, Party> parties;
    private long id;

    /**
     * Constructeur
     *
     * @param username
     * @throws RemoteException
     */
    public ClientEngine(String username) throws RemoteException {
        super();
        this.name = username;

        this.parties = new HashMap<String, Party>();
    }

    private Map<String, Integer> getScores() {
        Map<String, Integer> scores = null;
        try {
            scores = server.getScores(id);
            LOGGER.finest("scores retrieved succesfully");
        } catch (ConnectException ce) {

        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "get scores failed", e);
        }
        return scores;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    public String getUsername() {
        return name;
    }

    /**
     * la méthode connect appelée par le bouton "connexion" de la fenêtre de
     * connexion.
     *
     * Main
     */
    @Override
    public boolean connect(String url, int port) {
        // si pas d'url donnée, on la met à localhost pour jouer en local
        if (url == null || url.isEmpty()) {
            url = "localhost";
        }
        try {
            //Registry registry = LocateRegistry.getRegistry(url, port);

            //server = (GameEngineInterface)Naming.lookup(Constants.SERVER_PATH);
            server = (GameEngineInterface) Naming.lookup(url);

            // enveloppe le client en UnicastRemoteObject et l'enregistre sur le serveur
            ClientInterface client = (ClientInterface) UnicastRemoteObject.exportObject(
                    this, 0);
            id = server.connect(client);

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "connection failed at " + url + " on "
                    + port, ex);
            return false;
        }
        // si on reçoit bien un id (donc enregistrement a été accepté)
        if (id != 0) {
            LOGGER.fine("connection established at " + url + " on " + port);
            // alors on charge les données de jeu en appelant le serveur
            load();
            return true;
        }
        LOGGER.fine("connection refused at " + url + " on " + port);
        return false;
    }

    @Override
    public boolean isConnected() {
        return (server != null && id != 0);
    }

    /**
     * Gère le problème de connexion
     */
    private void onConnectionLost() {
        if (isConnected()) {
            if (arena != null) {
                arena.setState(ArenaState.Interrupted);
            }
            server = null;
            id = 0;
            setChanged();
            notifyObservers(ClientState.Disconnected);
        }
    }

    /**
     * Permet de se déconnecter
     */
    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                server.disconnect(id);
                LOGGER.fine("disconnection done");
            } catch (ConnectException ce) {
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "disconnection failed", e);
            }
        }
    }

    @Override
    public List<Party> getParties() {
        Collection<Party> c = parties.values();
        return new ArrayList<Party>(c);
    }

    @Override
    public Arena getArena() {
        return arena;
    }

    /**
     * méthode joinParty appelée par un clic sur une partie de la liste dans la
     * fenêtre de navigation.
     */
    @Override
    public boolean joinParty(String name) {
        // si on est bien connecté
        if (isConnected()) {
            try {
                // demande au serveur pour rejoindre la partie
                boolean joined = server.joinArena(id, name);
                if (joined) {
                    System.out.println("join party '" + name + "' accepted by server");
                    LOGGER.fine("join party '" + name + "' accepted by server");
                    /* si on a réussi a rejoindre, on crée un nouvel objet Arena
					 * pour y stocker les données de jeu
                     */
                    arena = new Arena(name);
                    // on met l'état en attente
                  //  arena.setState(ArenaState.Waiting);
                    Map<String, Integer> scores = getScores();
                    // on récupère les scores (car la partie est en peut être en cours
                    if (scores != null) {
                        arena.setScores(scores);
                    }
                } else {
                    LOGGER.fine("join party '" + name + "' refused by server");
                }
                return joined;
            } catch (ConnectException ce) {
                // si on perd la connexion, on appelle la méthode pour gérer la situation
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "join party '" + name + "' failed", e);
                return false;
            }
        }
        return false;
    }

    /**
     * méthode joinParty appelée par un clic sur une partie de la liste dans la
     * fenêtre de navigation.
     */
    @Override
    public boolean leaveParty(String name) {
        // si on est bien connecté et qu'on a bien un nom de partie
        if (isConnected() && !name.isEmpty() && arena != null) {
            try {
                // demande au serveur pour quitter la partie
                boolean left = server.leaveArena(id);
                if (left) {
                    LOGGER.fine("leave party '" + name + "' accepted by server");
                    /* si on a réussi à quitter, on vide l'objet Arena pour pouvoir
					 * rejoindre plus tard une autre partie
                     */
                    arena = null;
                } else {
                    LOGGER.fine("leave party '" + name + "' refused by server");
                }
                return left;
            } catch (ConnectException ce) {
                // si on perd la connexion, on appelle la méthode pour gérer la situation
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "leave party '" + name + "' failed", e);
                return false;
            }
        }
        return false;
    }

    /**
     * méthode createParty appelée par le bouton "new party" dans la fenêtre de
     * navigation.
     */
    @Override
    public boolean createParty(String name) {
        // si on est bien connecté et qu'on a bien un nom de partie
        if (isConnected() && !name.isEmpty()) {
            try {
                // demande au serveur pour créer la partie
                boolean created = server.createArena(id, name);
                if (created) {
                    /* si la partie a bien été crée, on instancie une Party et
					 * on l'ajoute à la table des parties
                     */
                    Party newParty = instanciateParty(name);
                    parties.put(name, newParty);
                    LOGGER.fine("create party '" + name + "' accepted by server");
                    return true;
                } else {
                    LOGGER.fine("create party '" + name + "' refused by server");
                    return false;
                }
            } catch (ConnectException ce) {
                // si on perd la connexion, on appelle la méthode pour gérer la situation
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "create party '" + name + "' failed", e);
                return false;
            }
        }
        return false;
    }

    private Party instanciateParty(String username) {
        return new Party(username, name);
    }

    @Override
    public boolean addParty(String name) throws RemoteException {
        Party party = instanciateParty(name);
        parties.put(name, party);
        LOGGER.fine("add party '" + name + "'");
        setChanged();
        notifyObservers(new Operation(Action.Add, party));
        return true;
    }

    @Override
    public boolean removeParty(String name) throws RemoteException {
        Party party = parties.remove(name);
        if (party != null) {
            LOGGER.fine("remove party '" + name + "'");
            setChanged();
            notifyObservers(new Operation(Action.Remove, party));
        }
        return true;
    }

    private void start(boolean started) {
        if (started) {
            LOGGER.fine("game is started");
            loadArena();
            arena.setState(ArenaState.Started);
            System.out.println("ClientEngine.start()  : game is started");
        } else {
            LOGGER.fine("game isn't ready to start");
            System.out.println("ClientEngine.start():  game isn't ready to start");
            arena.setState(ArenaState.Waiting);
        }
    }

    public void interrupt() {
        leaveParty(arena.getName());
        setChanged();
        notifyObservers(ArenaState.Interrupted);
    }

    protected void load() {
        if (isConnected()) {
            try {
                Set<String> parties = server.listArenas();
                loadParties(parties);
            } catch (ConnectException ce) {
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "data loading failed", e);
            }
        }
    }

    protected void loadParties(Set<String> parties) {
        for (String name : parties) {
            Party party = new Party(name, "unknown");
            this.parties.put(name, party);
        }
        LOGGER.finer("parties are loaded succesfully");
    }

    private boolean loadArena() {
        // si il existe bien une Arena pour y stocker les données
        if (arena == null) {
            LOGGER.finer("no existing arena for loading data");
            return false;
        }
        String name = arena.getName();
        try {

            // on demande au serveur les listes de Tile et Fence de cette arène
            Vector<Rectangle> vDisplayRoad = server.listvDisplayRoad(id, name);
            Vector<Rectangle> vDisplayObstacles = server.listvDisplayObstacles(id, name);
            Vector<Rectangle> vDisplayCars = server.listvDisplayCars(id, name);

            // on s'assure qu'on a bien reçu des données
            if ((vDisplayRoad == null || vDisplayRoad.isEmpty())
                    || (vDisplayObstacles == null || vDisplayObstacles.isEmpty())
                    || (vDisplayCars == null || vDisplayCars.isEmpty())) {
                LOGGER.finer("arena '" + name + "' can't be loaded without any data");
                return false;
            }

            // on stocke les données dans l'arène
            arena.setvDisplayRoad(vDisplayRoad);
            arena.setVDisplayCars(vDisplayCars);
            arena.setVDisplayObstacles(vDisplayObstacles);
            // arena.setVCars(vCars);

            LOGGER.finer("arena '" + name + "' is loaded succesfully");
            return true;
        } catch (ConnectException ce) {
            // si on perd la connexion, on appelle la méthode pour gérer la situation
            onConnectionLost();
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "load party '" + name + "' failed", e);
            /* en cas de problème, on stoppe le jeu dans l'arène :
			 * ceci permet (avec Observable) de notifier les fenêtres et
			 * d'avertir l'utilisateur avec un message
             */
            arena.setState(ArenaState.Interrupted);
        }
        return false;
    }

    @Override
    public void newGame() {
        if (isConnected() && arena != null) {
            boolean started = false;
            try {
                started = server.startGame(id);
            } catch (ConnectException ce) {
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "starting game failed", e);
            } finally {
                start(started);
            }
        }
    }

    @Override
    public void close() {
        if (arena.isInProgress()) {
            arena.setState(ArenaState.Interrupted);
        }
        leaveParty(arena.getName());
    }

    @Override
    public void update(Vector<Rectangle> vDisplayRoad, Vector<Rectangle> vDisplayObstacles, Vector<Rectangle> vDisplayCars, Car myCar, int pos, int nbParticipants, boolean bGameOver, String sPosition) throws RemoteException {

        if (isConnected() && arena != null) {

            gui.update(vDisplayRoad, vDisplayObstacles, vDisplayCars, myCar, pos, nbParticipants, bGameOver, sPosition);

            System.out.println("clienEngine Update");;

        }

    }

    @Override
    public void setEnabled(boolean flag) throws RemoteException {
        gui.jButton1.setEnabled(flag);
    }

    @Override
    public void newGrid() {

        try {
            server.newGrid(id);
            System.out.println("ClientEngine.newGrid()");
        } catch (ConnectException ce) {
            onConnectionLost();
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "starting game failed", e);
        }

    }

    @Override
    public void beginGame() {

        if (isConnected() && arena != null) {
            boolean started = false;
            try {
                server.beginGame(id);
                System.out.println("ClientEngine.beginGame()");
            } catch (ConnectException ce) {
                onConnectionLost();
            } catch (RemoteException e) {
                LOGGER.log(Level.SEVERE, "starting game failed", e);
            } finally {
                start(started);
            }
        }
    }

    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Permet de déplacer la voiture
     *
     * @param choice
     * @param flag
     */
    @Override
    public void moveCar(String choice, boolean flag) {

        try {
            server.moveCar(id, choice, flag);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getScore() {
        try {
           return  server.getScoreClient(id);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

}
