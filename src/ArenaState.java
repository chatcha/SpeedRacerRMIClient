/**
 * L'état de l'arène de combat. <br/>
 * <br/>
 * Etats : <br/>
 * <b>InProgress</b> - {@link ArenaState#InProgress} <br/>
 * <b>Interrupted</b> - {@link ArenaState#Interrupted} <br/>
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