package edu.kit.kastel.sdq.eclipse.grading.client.mappings.lock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is only used in the json deserialization process. Much like {@link ParticipationDTO}
 */
public class ExerciseDTO {

	private double maxPoints;

	@JsonCreator
	public ExerciseDTO(@JsonProperty("maxPoints") double maxPoints) {
		this.maxPoints = maxPoints;
	}

	public double getMaxPoints() {
		return this.maxPoints;
	}
}