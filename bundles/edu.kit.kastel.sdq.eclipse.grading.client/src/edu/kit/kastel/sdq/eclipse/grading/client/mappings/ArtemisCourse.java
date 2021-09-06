package edu.kit.kastel.sdq.eclipse.grading.client.mappings;

import java.util.Collection;

import javax.security.sasl.AuthenticationException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.rest.ArtemisRESTClient;

public class ArtemisCourse implements ICourse {

	@JsonProperty(value = "id")
	private int courseId;
	@JsonProperty
	private String title;
	@JsonProperty
	private String shortName;
	private transient Collection<IExercise> exercises;
	private transient Collection<IExam> exams;

	/**
	 * For Auto-Deserialization
	 * Need to call this::init thereafter!
	 */
	public ArtemisCourse() {
	}

	public ArtemisCourse(int courseId, String title, String shortName, Collection<IExercise> exercises, Collection<IExam> exams) {
		this.courseId = courseId;
		this.title = title;
		this.shortName = shortName;
		this.exercises = exercises;
		this.exams = exams;
	}

	@Override
	public int getCourseId() {
		return this.courseId;
	}

	@Override
	public Collection<IExam> getExams() {
		return this.exams;
	}

	@Override
	public Collection<IExercise> getExercises() {
		return this.exercises;
	}

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	public void init(ArtemisRESTClient artemisRESTClient) throws AuthenticationException, JsonProcessingException, ArtemisClientException {
		this.exercises = artemisRESTClient.getExercisesForCourse(this);
		this.exams = artemisRESTClient.getExamsForCourse(this);
	}

	@Override
	public String toString() {
		return "ArtemisCourse [courseId=" + this.courseId + ", title=" + this.title + ", shortName=" + this.shortName + ", exercises="
				+ this.exercises + ", exams=" + this.exams + "]";
	}
}