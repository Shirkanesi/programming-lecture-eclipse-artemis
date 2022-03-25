package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;

/**
 * REST-Client to execute calls concerning feedbacks.
 */
public interface IFeedbackArtemisClient {

	/**
	 * Returns all feedbacks for a given result and participation. A participation
	 * is bound to a specific exercise. If the result does not exist or forbidden or
	 * if the user does not have permissions to access the participation it throws
	 * ArtmisClientException.
	 * 
	 * @param participation
	 * @param result
	 * @return All feedbacks.
	 * @throws ArtemisClientException
	 */
	Feedback[] getFeedbackForResult(ParticipationDTO participation, ResultsDTO result) throws ArtemisClientException;
}
