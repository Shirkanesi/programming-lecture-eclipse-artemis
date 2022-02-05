package edu.kit.kastel.sdq.eclipse.grading.api.client;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.kit.kastel.sdq.eclipse.grading.api.Constants;

/**
 * Encapsulates methods to get data from and send data to Artemis
 */
public abstract class AbstractArtemisClient implements IArtemisClient {

	// paths
	protected static final String PROGRAMMING_SUBMISSION_PATHPART = "programming-submissions";
	protected static final String EXERCISES_PATHPART = "exercises";
	protected static final String COURSES_PATHPART = "courses";
	protected static final String EXAMS_PATHPART = "exams";
	protected static final String USERS_PATHPART = "users";
	protected static final String PARTICIPATION_PATHPART = "participations";
	protected static final String RESULT_PATHPART = "results";
	protected static final String STUDENT_EXAM_PATH = "student-exams";
	
	

	protected static final String AUTHORIZATION_NAME = "Authorization";

	private String artemisUsername;
	private String artemisPassword;
	private String artemisHostname;

	/**
	 *
	 * @param artemisUsername for login to artemis
	 * @param artemisPassword for login to artemis
	 * @param artemisHostname the hostname, only! (e.g. "test.kit.edu")
	 */
	protected AbstractArtemisClient(String artemisUsername, String artemisPassword, String artemisHostname) {
		this.artemisUsername = artemisUsername;
		this.artemisPassword = artemisPassword;
		this.artemisHostname = artemisHostname;
	}

	public boolean isReady() {
		return !(this.artemisHostname.isBlank() && this.artemisUsername.isBlank() && this.artemisPassword.isBlank());
	}

	protected final String getApiRoot() {
		String endpoint = this.artemisHostname;
		if (!endpoint.startsWith(Constants.HTTPS_PREFIX)) {
			endpoint = Constants.HTTPS_PREFIX + endpoint;
		}

		if (endpoint.endsWith("/")) {
			endpoint = endpoint.substring(0, endpoint.length() - 1);
		}

		return endpoint + "/api";
	}

	public String getArtemisUsername() {
		return this.artemisUsername;
	}

	protected final AuthenticationEntity getAuthenticationEntity() {
		AuthenticationEntity entity = new AuthenticationEntity();
		entity.username = this.artemisUsername;
		entity.password = this.artemisPassword;
		return entity;
	}

	protected static final class AuthenticationEntity implements Serializable {
		private static final long serialVersionUID = -6291795795865534155L;
		@JsonProperty
		private String username;
		@JsonProperty
		private String password;
		@JsonProperty
		private boolean rememberMe = true;
	}

}
