package edu.kit.kastel.sdq.eclipse.grading.api.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.kastel.sdq.eclipse.grading.api.artemis.IProjectFileNamingStrategy;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.Feedback;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExercise;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ISubmission;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ParticipationDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ResultsDTO;
import edu.kit.kastel.sdq.eclipse.grading.api.client.websocket.WebsocketCallback;

/**
 * Works as an interface from backend to REST-clients
 *
 */
public interface IArtemisController extends IController {
	/**
	 *
	 * @return all IFeedbacks that were gotten in the process of locking the given
	 *         submission.
	 */
	List<Feedback> getAllFeedbacksGottenFromLocking(ISubmission submission);

	/**
	 *
	 * @param exerciseID
	 * @return all submissions of the given @link {@link IExercise}, that have been
	 *         started, saved or submitted by the caller.
	 */
	List<ISubmission> getBegunSubmissions(IExercise exercise);

	/**
	 *
	 * @return all available courses (contains exercices and available submissions
	 */
	List<ICourse> getCourses();

	/**
	 *
	 * @return the {@link ICourse#getShortName()} of all available courses
	 */
	List<String> getCourseShortNames();

	/**
	 *
	 * @return the {@link IExam#getTitle()} of all available exams in the given
	 *         {@link ICourse}
	 */
	List<String> getExamTitles(String courseShortName);

	/**
	 * Convenience method. Search the given ids in the given courses.
	 *
	 * @param courses    the data in which to search for the exercise
	 * @param courseID
	 * @param exerciseID
	 * @return the exercise, if found. null else.
	 */
	IExercise getExerciseFromCourses(List<ICourse> courses, int courseID, int exerciseID);

	List<IExercise> getExercises(ICourse course, boolean withExamExercises);

	List<IExercise> getExercisesFromExam(String examTitle);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link ICourse}
	 */
	List<String> getExerciseShortNames(String courseShortName);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link IExam}
	 */
	List<String> getExerciseShortNamesFromExam(String examTitle);

	/**
	 * Pre-condition: You need to have called startAssessment or startNextAssessment
	 * prior to calling this method!
	 *
	 * @return all auto feedbacks gotten by starting the assessment (junit test
	 *         results).
	 */
	List<Feedback> getPrecalculatedAutoFeedbacks(ISubmission submission);

	Date getCurrentDate();
}