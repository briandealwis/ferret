package ca.ubc.cs.ferret;

import ca.ubc.cs.ferret.model.Consultation;

public interface IConsultancyClient {
	/**
	 * Return true if this client is awaiting the result of the provided consultation.
	 * @param c
	 * @return true if consultation is being used
	 */
	public boolean isAwaiting(Consultation c);
	
	/**
	 * Notify that any existing consultations should be flushed and potentially
	 * regenerated.
	 */
	public void consultancyReset();
}
