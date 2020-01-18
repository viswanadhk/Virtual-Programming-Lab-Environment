CREATE TABLE `students` (
	`student_id` varchar(45) NOT NULL UNIQUE,
	`first_name` varchar(45) NOT NULL,
	`last_name` varchar(45) NOT NULL
);

CREATE TABLE `exams` (
	`exam_id` varchar(1000) NOT NULL,
	`exam_name` varchar(1000) NOT NULL,
	`duration` bigint(20) NOT NULL,
	`no_of_questions` int(11) NOT NULL,
	PRIMARY KEY (`exam_id`)
);

CREATE TABLE `questions` (
	`question_id` bigint(20) NOT NULL AUTO_INCREMENT,
	`description` varchar(10000) NOT NULL,
	`test_cases` varchar(10000) NOT NULL,
	`expected_output` varchar(10000) NOT NULL,
	`score` varchar(100) NOT NULL,
	`exam_id` varchar(100) NOT NULL,
	PRIMARY KEY (`question_id`)
);
'unique_key'
CREATE TABLE `exam_vs_students` (
	`exam_id` varchar(100) NOT NULL,
	`student_id` varchar(100) NOT NULL,
	`unique_key` varchar(100) NOT NULL,
	`exam_finished` bool(2) NOT NULL DEFAULT '0',
	PRIMARY KEY (`unique_key`)
);

CREATE TABLE `student_answers` (
	`exam_vs_students_fk` varchar(100) NOT NULL,
	`question_id` varchar(100) NOT NULL,
	`answer` longtext(10000) NOT NULL
);

CREATE TABLE `rights` (
	`student_id` varchar(45) NOT NULL,
	`role` varchar(100) NOT NULL
);

ALTER TABLE `questions` ADD CONSTRAINT `questions_fk0` FOREIGN KEY (`exam_id`) REFERENCES `exams`(`exam_id`);

ALTER TABLE `exam_vs_students` ADD CONSTRAINT `exam_vs_students_fk0` FOREIGN KEY (`exam_id`) REFERENCES `exams`(`exam_id`);

ALTER TABLE `exam_vs_students` ADD CONSTRAINT `exam_vs_students_fk1` FOREIGN KEY (`student_id`) REFERENCES `students`(`student_id`);

ALTER TABLE `student_answers` ADD CONSTRAINT `student_answers_fk0` FOREIGN KEY (`exam_vs_students_fk`) REFERENCES `exam_vs_students`(`unique_key`);

ALTER TABLE `student_answers` ADD CONSTRAINT `student_answers_fk1` FOREIGN KEY (`question_id`) REFERENCES `questions`(`question_id`);

ALTER TABLE `rights` ADD CONSTRAINT `rights_fk0` FOREIGN KEY (`student_id`) REFERENCES `students`(`student_id`);

