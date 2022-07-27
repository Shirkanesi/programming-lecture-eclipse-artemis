/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.core;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.eclipse.common.api.ArtemisClientException;
import edu.kit.kastel.eclipse.common.api.artemis.ILockResult;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.controller.IAssessmentController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingArtemisController;
import edu.kit.kastel.eclipse.common.api.controller.IGradingSystemwideController;
import edu.kit.kastel.eclipse.common.api.messages.Messages;
import edu.kit.kastel.eclipse.common.api.model.IAnnotation;
import edu.kit.kastel.eclipse.common.api.model.IRatingGroup;
import edu.kit.kastel.eclipse.common.core.artemis.AnnotationMapper;

public class GradingArtemisController extends ArtemisController implements IGradingArtemisController {

	public GradingArtemisController(String host, String username, String password) {
		super(host, username, password);
	}

	@Override
	public List<String> getExerciseShortNamesFromExam(final String examTitle) {
		return this.getExercisesFromExam(examTitle).stream().map(IExercise::getShortName).toList();
	}

	@Override
	protected List<ICourse> fetchCourses() {
		if (!this.clientManager.isReady()) {
			return List.of();
		}
		try {
			return this.clientManager.getCourseArtemisClient().getCoursesForAssessment();
		} catch (ArtemisClientException e) {
			this.error(e.getMessage(), e);
			return List.of();
		}
	}

	@Override
	public boolean saveAssessment(IAssessmentController assessmentController, IExercise exercise, ISubmission submission, boolean submit) {
		if (!this.lockResults.containsKey(submission.getSubmissionId())) {
			throw new IllegalStateException("Assessment not started, yet!");
		}
		final ILockResult lock = this.lockResults.get(submission.getSubmissionId());
		final int participationId = lock.getParticipationId();

		final List<IAnnotation> annotations = assessmentController.getAnnotations();
		final List<IRatingGroup> ratingGroups = assessmentController.getRatingGroups();

		try {
			AnnotationMapper mapper = //
					new AnnotationMapper(exercise, submission, annotations, ratingGroups, this.clientManager.getAuthenticationClient().getUser(), lock);
			this.clientManager.getAssessmentArtemisClient().saveAssessment(participationId, submit, mapper.createAssessmentResult());
		} catch (IOException e) {
			this.error("Local backend failed to format the annotations: " + e.getMessage(), e);
			return false;
		} catch (ArtemisClientException e) {
			this.error("Assessor could not be retrieved from Artemis or Authentication to Artemis failed:" + e.getMessage(), e);
			return false;
		}

		if (submit) {
			this.lockResults.remove(submission.getSubmissionId());
		}
		return true;
	}

	@Override
	public void startAssessment(ISubmission submissionID) {
		try {
			this.lockResults.put(submissionID.getSubmissionId(), this.clientManager.getAssessmentArtemisClient().startAssessment(submissionID));
		} catch (ArtemisClientException e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
		}
	}

	@Override
	public Optional<ISubmission> startNextAssessment(IExercise exercise, IGradingSystemwideController systemwideController) {
		return this.startNextAssessment(exercise, 0, systemwideController);
	}

	@Override
	public Optional<ISubmission> startNextAssessment(IExercise exercise, int correctionRound, IGradingSystemwideController systemwideController) {
		Optional<ILockResult> lockResultOptional;
		
		ILockResult lockResult;
		
		Set<String> studentNames = systemwideController.getOwnStudentsNames();
		
		if (!studentNames.isEmpty()) {
			// load from own students
			List<ISubmission> submissions;
			try {
				submissions = this.clientManager.getSubmissionArtemisClient().getSubmissions(exercise, correctionRound);				
			} catch (ArtemisClientException e) {
				this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
				return Optional.empty();
			}
			Optional<ISubmission> optionalSubmission = submissions.stream()
				.filter(submission -> studentNames.contains(submission.getParticipantIdentifier().toLowerCase()))
				.filter(submission -> !(submission.hasSavedAssessment() || submission.hasSubmittedAssessment()))
				.findAny();
			
			if (optionalSubmission.isPresent()) {
				ISubmission submission = optionalSubmission.get();
				try {
					lockResult = this.clientManager.getAssessmentArtemisClient().startAssessment(submission);
				} catch (ArtemisClientException e) {
					this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
					return Optional.empty();
				}
			} else {
				return Optional.empty();
			}
		} else {
			// load from all students
			try {
				lockResultOptional = this.clientManager.getAssessmentArtemisClient().startNextAssessment(exercise, correctionRound);
			} catch (ArtemisClientException e) {
				this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
				return Optional.empty();
			}
			if (lockResultOptional.isEmpty()) {
				return Optional.empty();
			}
			lockResult = lockResultOptional.get();			
		}
		
		
		final int submissionID = lockResult.getSubmissionId();
		this.lockResults.put(submissionID, lockResult);
		try {
			return Optional.of(exercise.getSubmission(submissionID));
		} catch (ArtemisClientException e) {
			this.error(Messages.ASSESSMENT_COULD_NOT_BE_STARTED_MESSAGE + e.getMessage(), e);
			return Optional.empty();
		}
	}

}
