package com.courselens.config;

import com.courselens.model.Course;
import com.courselens.model.HistoricalDataPoint;
import com.courselens.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;

    @Autowired
    public DataSeeder(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (courseRepository.count() == 0) {
            seedCourses();
        }
    }

    private void seedCourses() {
        Course csc108 = new Course();
        csc108.setCode("CSC108H5F");
        csc108.setTitle("Introduction to Computer Programming");
        csc108.setDescription("A first course in computer science for students with little or no programming experience.");
        csc108.setCapacity(500);
        csc108.setCurrentEnrollment(480);
        csc108.setHistoricalData(List.of(
                new HistoricalDataPoint(LocalDate.parse("2023-08-20"), 350),
                new HistoricalDataPoint(LocalDate.parse("2023-08-21"), 420),
                new HistoricalDataPoint(LocalDate.parse("2023-08-22"), 480)
        ));

        Course mat135 = new Course();
        mat135.setCode("MAT135H5F");
        mat135.setTitle("Calculus I");
        mat135.setDescription("An introductory course in calculus.");
        mat135.setCapacity(600);
        mat135.setCurrentEnrollment(595);
        mat135.setHistoricalData(List.of(
                new HistoricalDataPoint(LocalDate.parse("2023-08-20"), 500),
                new HistoricalDataPoint(LocalDate.parse("2023-08-21"), 550),
                new HistoricalDataPoint(LocalDate.parse("2023-08-22"), 595)
        ));

        Course psy100 = new Course();
        psy100.setCode("PSY100H5F");
        psy100.setTitle("Introduction to Psychology");
        psy100.setDescription("A survey of the major areas of psychology.");
        psy100.setCapacity(750);
        psy100.setCurrentEnrollment(740);
        psy100.setHistoricalData(List.of(
                new HistoricalDataPoint(LocalDate.parse("2023-08-20"), 600),
                new HistoricalDataPoint(LocalDate.parse("2023-08-21"), 680),
                new HistoricalDataPoint(LocalDate.parse("2023-08-22"), 740)
        ));

        courseRepository.saveAll(List.of(csc108, mat135, psy100));
    }
}
