SELECT student.name AS student_name, student.age, faculty.name AS faculty_name
FROM student
INNER JOIN faculty ON student.faculty_id = faculty.id;

SELECT student.*
FROM student
INNER JOIN avatar ON avatar.student_id = student.id;
