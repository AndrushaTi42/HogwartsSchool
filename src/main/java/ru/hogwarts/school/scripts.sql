select * from student;
select * from student where age between 10 and 20;
select name from student;

//с игнором регистра через приведение к нижнему
select * from student where LOWER("name" COLLATE "ru_RU") like '%а%';
//без игнора регистра
select * from student where name like '%А%';

select * from student where age < id;
select * from student order by age;