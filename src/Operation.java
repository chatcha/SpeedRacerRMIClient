import java.io.Serializable;


public class Operation implements Serializable {

	/**
	 * last update : 09 / 11 / 2015
	 */
	private static final long serialVersionUID = 20151109L;
	
	
	private final Action action;
	private final Object data;
	
	public Operation(Action action, Object data) {
		this.action = action;
		this.data = data;
	}

	public Action getAction() {
		return action;
	}

	public Object getData() {
		return data;
	}
	
	
}
