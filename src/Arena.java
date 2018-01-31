
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Vector;

/**
 * Classe représentant l'arène dans laquelle l'utilisateur combat. 
 * Cette arène a un nom et un état décrit par ArenaState.
 * Elle conserve la liste des objets Tile et Fence que la fenêtre de jeu
 * (GuiArena) lit à chaque update. 
 * Elle stocke également le nom du joueur gagnant ainsi que les scores.
 *
 * À chaque update du client par le serveur, le client modifie l'état, la liste
 * de Tile, le nom du joueur gagnant et les scores.
 *
 */
public class Arena extends Observable {

    private final String name;

    private ArenaState state;

    private Vector<Rectangle> vDisplayRoad;
    private Vector<Rectangle> vDisplayObstacles;
    private Vector<Rectangle> vDisplayCars;
    private Vector<Car> vCars;
    private Car mCar;
    int nbParticipants;
    boolean bGameOver = false;
    private String winner;
    private int iFinalPosition;
    private boolean bGameFinishing;
    public String sFinalPosition;
   //  player.update(vDisplayRoad, vDisplayObstacles, vDisplayCars, vCars.elementAt(0), iFinalPosition, iNbParticipants, bGameFinishing, sFinalPosition);
    private Map<String, Integer> scores;

    public Arena(String name) {
        this.name = name;
        this.vDisplayRoad = new Vector();
        this.vDisplayObstacles = new Vector();
        this.vDisplayCars = new Vector();
        this.vCars = new Vector<Car>();
        iFinalPosition = 0;
        bGameFinishing = false;
        sFinalPosition = "";
        nbParticipants = 4;
        this.winner = null;
        this.scores = new HashMap<String, Integer>();
       // this.state = ArenaState.Waiting;
    }

    /**
     * *************************************************************************
     * ACCESSORS
     * ************************************************************************
     */
    /**
     * Indique le nom de la partie de jeu associée à cette arène. 
     *
     * @return nom de la partie correspondant à l'arène
     */
    public String getName() {
        return name;
    }

    /**
     * Indique l'état courrant de l'arène de combat.
     *
     * @see ArenaState
     * @return l'état de l'Arena
     */
    public ArenaState getState() {
        return state;
    }

    public void setState(ArenaState state) {
        this.state = state;
        setChanged();
        notifyObservers(state);
    }

    public boolean isInProgress() {
        return state == ArenaState.InProgress;
    }

    public boolean isInterrupted() {
        return state == ArenaState.Interrupted;
    }

    public boolean isWaiting() {
        return state == ArenaState.Waiting;
    }

    public boolean isOver() {
        return state == ArenaState.Over;
    }

    public boolean isStarted() {
        return state == ArenaState.Started;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
        setChanged();
        notifyObservers(scores);
    }

    public void setvDisplayRoad(Vector<Rectangle> vDisplayRoad) {
        this.vDisplayRoad = vDisplayRoad;
        setChanged();
        notifyObservers(vDisplayRoad);
    }

    public Vector<Rectangle> getVDisplayRoad() {
        return vDisplayRoad;
    }

    public void setVDisplayObstacles(Vector<Rectangle> vDisplayObstacles) {
        this.vDisplayObstacles = vDisplayObstacles;
        setChanged();
        notifyObservers(vDisplayObstacles);
    }

    public Vector<Rectangle> getVDisplayObstacles() {
        return vDisplayObstacles;
    }

    public void setVDisplayCars(Vector<Rectangle> vDisplayCars) {
        this.vDisplayCars = vDisplayCars;
        setChanged();
        notifyObservers(vDisplayCars);
    }

    public Vector<Rectangle> getVDisplayCars() {
        return vDisplayCars;
    }

    public void setVCars(Vector<Car> vCars) {
        this.mCar = vCars.elementAt(0);
        setChanged();
        notifyObservers(this.mCar);
    }

    public Car getVCars() {
        return vCars.elementAt(0);
    }

    public Car getmCar() {
        return mCar;
    }

    public void setmCar(Car mCar) {
        this.mCar = mCar;
        setChanged();
        notifyObservers(mCar);

    }



    public int getNbParticipants() {
        return nbParticipants;
    }

    public void setNbParticipants(int nbParticipants) {
        this.nbParticipants = nbParticipants;
        setChanged();
        notifyObservers(nbParticipants);
    }

    public boolean isbGameOver() {
        return bGameOver;
    }

    public void setbGameOver(boolean bGameOver) {
        this.bGameOver = bGameOver;
        setChanged();
        notifyObservers(bGameOver);
    }


    public int getiFinalPosition() {
        return iFinalPosition;
    }

    public void setiFinalPosition(int iFinalPosition) {
        this.iFinalPosition = iFinalPosition;
        setChanged();
	notifyObservers(iFinalPosition);
    }

    public boolean isbGameFinishing() {
        return bGameFinishing;
    }

    public void setbGameFinishing(boolean bGameFinishing) {
        this.bGameFinishing = bGameFinishing;
         setChanged();
	notifyObservers(bGameFinishing);
    }

    public String getsFinalPosition() {
        return sFinalPosition;
    }

    public void setsFinalPosition(String sFinalPosition) {
        this.sFinalPosition = sFinalPosition;
        setChanged();
	notifyObservers(sFinalPosition);
    }

    
}
