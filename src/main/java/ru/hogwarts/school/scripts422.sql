CREATE TABLE car (
id SERIAL PRIMARY KEY,
 brand VARCHAR(50),
  model VARCHAR(50),
   price NUMERIC(12, 2)
   );
CREATE TABLE human (
 id SERIAL PRIMARY KEY,
  name VARCHAR(100),
   age INT,
    driver_license BOOLEAN,
     car_id INT,
     FOREIGN KEY (car_id) REFERENCES car(id)
     );
