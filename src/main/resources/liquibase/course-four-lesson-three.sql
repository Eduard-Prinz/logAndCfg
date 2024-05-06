--changeset formatted sql

--changeset prince:course-four-lesson-three.1
CREATE  INDEX IDX_STUDENTS_NAME ON students(name);

--changeset prince:course-four-lesson-three.2
CREATE  INDEX IDX_FACULTIES_NAME_COLOR ON faculties(name, color);