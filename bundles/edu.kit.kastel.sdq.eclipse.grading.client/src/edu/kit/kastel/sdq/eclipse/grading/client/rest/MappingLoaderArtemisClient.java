package edu.kit.kastel.sdq.eclipse.grading.client.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;

import com.fasterxml.jackson.databind.JsonNode;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExerciseGroup;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.client.ICourseArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.api.client.ISubmissionsArtemisClient;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisDashboardCourse;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.ArtemisExercise;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.IMappingLoader;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExam;
import edu.kit.kastel.sdq.eclipse.grading.client.mappings.exam.ArtemisExerciseGroup;

public class MappingLoaderArtemisClient extends AbstractArtemisClient implements ICourseArtemisClient, IMappingLoader{
	private static final ILog log = Platform.getLog(MappingLoaderArtemisClient.class);

	private WebTarget endpoint;
	private String token;
	private ISubmissionsArtemisClient submissionClient;

	public MappingLoaderArtemisClient(ISubmissionsArtemisClient submissionClient, String hostName, String token) {
		super(hostName);

		this.endpoint = getEndpoint(this.getApiRootURL());
		this.token = token;
		this.submissionClient = submissionClient;
	}
	
	@Override
	public List<ICourse> getCoursesForAssessment() throws ArtemisClientException {
		final Response rsp = this.endpoint.path(COURSES_PATHPART).request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisCourse[] coursesArray = this.read(rspString, ArtemisCourse[].class);
		for (ArtemisCourse course : coursesArray) {
			course.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<ICourse> getCoursesForDashboard() throws ArtemisClientException {
		final Response rsp = this.endpoint.path(COURSES_PATHPART).path("for-dashboard").request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(rsp);
		String rspString = rsp.readEntity(String.class);

		ArtemisDashboardCourse[] coursesArray = this.read(rspString, ArtemisDashboardCourse[].class);
		for (var courses : coursesArray) {
			courses.init(this);
		}
		return Arrays.asList(coursesArray);
	}

	@Override
	public List<IExerciseGroup> getExerciseGroupsForExam(IExam artemisExam, ICourse course)
			throws ArtemisClientException {
		final Response rsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(course.getCourseId())).path(EXAMS_PATHPART)
				.path(String.valueOf(artemisExam.getExamId())).path("exam-for-assessment-dashboard") // web client does it that way..
				.request().header(AUTHORIZATION_NAME, this.token).buildGet().invoke(); // synchronous variant
		this.throwIfStatusUnsuccessful(rsp);

		// need to retrieve the exerciseGroups array root node to deserialize it!
		JsonNode detailledExamJsonNode = this.readTree(rsp.readEntity(String.class));
		JsonNode exerciseGroupsJsonArray = detailledExamJsonNode.get("exerciseGroups");
		if (exerciseGroupsJsonArray == null) {
			// exam has no exercise groups!
			return List.of();
		}
		if (!exerciseGroupsJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		ArtemisExerciseGroup[] exerciseGroupsArray = this.read(exerciseGroupsJsonArray.toString(), ArtemisExerciseGroup[].class);
		for (ArtemisExerciseGroup exerciseGroup : exerciseGroupsArray) {
			exerciseGroup.init(this, course, artemisExam);
		}
		return Arrays.asList(exerciseGroupsArray);
	}

	@Override
	public List<IExam> getExamsForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response examsRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path(EXAMS_PATHPART).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(examsRsp);

		ArtemisExam[] examsArray = this.read(examsRsp.readEntity(String.class), ArtemisExam[].class);
		for (ArtemisExam exam : examsArray) {
			exam.init(this, artemisCourse);
		}
		return Arrays.asList(examsArray);
	}

	@Override
	public List<IExercise> getStudentExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response eexerciseRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path(EXERCISES_PATHPART).request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();
		this.throwIfStatusUnsuccessful(eexerciseRsp);

		ArtemisExercise[] excerciseArray = this.read(eexerciseRsp.readEntity(String.class), ArtemisExercise[].class);
		for (ArtemisExercise exercise : excerciseArray) {
			exercise.init(this, artemisCourse);
		}
		return Arrays.asList(excerciseArray);
	}

	@Override
	public List<IExercise> getNormalExercisesForCourse(ICourse artemisCourse) throws ArtemisClientException {
		final Response exercisesRsp = this.endpoint.path(COURSES_PATHPART).path(String.valueOf(artemisCourse.getCourseId())).path("with-exercises").request()
				.header(AUTHORIZATION_NAME, this.token).buildGet().invoke();

		this.throwIfStatusUnsuccessful(exercisesRsp);

		// get the part of the json that we want to deserialize
		final JsonNode exercisesAndParticipationsJsonNode = this.readTree(exercisesRsp.readEntity(String.class));
		JsonNode exercisesJsonArray = exercisesAndParticipationsJsonNode.get(EXERCISES_PATHPART);
		if (exercisesJsonArray == null) {
			// course has no exercises!
			return List.of();
		}
		if (!exercisesJsonArray.isArray()) {
			throw new ArtemisClientException(JSON_PARSE_ERROR_MESSAGE_CORRUPT_JSON_STRUCTURE);
		}

		ArtemisExercise[] exercisesArray = this.read(exercisesJsonArray.toString(), ArtemisExercise[].class);
		for (ArtemisExercise exercise : exercisesArray) {
			exercise.init(this, artemisCourse, Optional.empty());
		}

		// Here we filter all programming exercises
		return Arrays.stream(exercisesArray).filter(exercise -> "programming".equals(exercise.getType())).collect(Collectors.toList());
	}

	@Override
	public ISubmission getSubmissionById(IExercise artemisExercise, int submissionId) throws ArtemisClientException {
		return submissionClient.getSubmissionById(artemisExercise, submissionId);
	}

}
