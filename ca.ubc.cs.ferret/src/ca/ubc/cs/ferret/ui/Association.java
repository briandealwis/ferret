package ca.ubc.cs.ferret.ui;

public class Association<T1, T2> {
	protected T1 from;
	protected T2 to;
	
	public Association(T1 _from, T2 _to) {
		from = _from;
		to = _to;
	}

	public T1 getFrom() {
		return from;
	}

	public T2 getTo() {
		return to;
	}
}
