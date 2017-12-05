/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author brasc
 */
public class Player {

    private int score = 0;
    private boolean bGameFinishing;
    private boolean bGameInProgress;
    private long idPlayer;
    private boolean left, rigth, up, down;

    public Player(long id) {

        this.idPlayer = id;

    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isbGameFinishing() {
        return bGameFinishing;
    }

    public void setbGameFinishing(boolean bGameFinishing) {
        this.bGameFinishing = bGameFinishing;
    }

    public boolean isbGameInProgress() {
        return bGameInProgress;
    }

    public void setbGameInProgress(boolean bGameInProgress) {
        this.bGameInProgress = bGameInProgress;
    }

    public long getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRigth() {
        return rigth;
    }

    public void setRigth(boolean rigth) {
        this.rigth = rigth;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

}
