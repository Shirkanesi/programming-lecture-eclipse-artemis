/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.controller;

import java.time.LocalDateTime;
import java.util.List;

import edu.kit.kastel.eclipse.common.api.artemis.mapping.Feedback;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ICourse;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExam;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.IExercise;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.ISubmission;
import edu.kit.kastel.eclipse.common.api.artemis.mapping.User;

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
	 *
	 * Returns all exercises for the given course.
	 *
	 * @param course
	 * @param withExamExercises if true returns also exercises of the exams of the
	 *                          course.
	 * @return
	 */
	List<IExercise> getExercises(ICourse course, boolean withExamExercises);

	/**
	 * Returns all exercises of exasm with title examTitle.
	 *
	 * @param examTitle exam title of the exam
	 * @return a list of all exercises of the exam
	 */
	List<IExercise> getExercisesFromExam(String examTitle);

	/**
	 *
	 * @return the {@link IExercise#getShortName()}s of the given {@link IExam}
	 */
	List<String> getExerciseShortNamesFromExam(String examTitle);

	/**
	 * Fetches current local time of artemis backend via REST.
	 *
	 * @return current time of Artemis-Server
	 */
	LocalDateTime getCurrentDate();

	User getUserLogin();
}