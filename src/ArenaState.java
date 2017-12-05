
/**
 * L'état de l'arène de combat. 
 *
 * Etats : 
 * InProgress - {@link ArenaState#InProgress} 
 * Interrupted- {@link ArenaState#Interrupted}
 */
public enum ArenaState {
    /**
     * le combat est en attente de démmarage
     */
    Waiting,
    /**
     * le combat démarre
     */
    Started,
    /**
     * le combat est en cours de progression
     */
    InProgress,
    /**
     * le combat a été interrompu
     */
    Interrupted,
    /**
     * le combat s'est terminé
     */
    Over
}
