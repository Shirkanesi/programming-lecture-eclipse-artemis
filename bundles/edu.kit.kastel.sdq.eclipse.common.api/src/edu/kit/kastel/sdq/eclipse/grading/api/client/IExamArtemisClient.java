package edu.kit.kastel.sdq.eclipse.grading.api.client;

import edu.kit.kastel.sdq.eclipse.grading.api.ArtemisClientException;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.ICourse;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IExam;
import edu.kit.kastel.sdq.eclipse.grading.api.artemis.mapping.IStudentExam;

/**
 * REST-Client to execute calls concerning exams.
 */
public interface IExamArtemisClient {
	/**
	 * If the given exam has already been started, it returns all the exercises of
	 * the exam.
	 * 
	 * @param course
	 * @param exam
	 * @return
	 * @throws ArtemisClientException
	 */
	IStudentExam findExamForSummary(ICourse course, IExam exam) throws ArtemisClientException;

	/**
	 * Starts the given exam. Returns the exam object and all its exercises.
	 * 
	 * @param exam
	 * @return
	 * @throws ArtemisClientException
	 */
	IStudentExam startExam(ICourse course, IExam exam) throws ArtemisClientException;
}
