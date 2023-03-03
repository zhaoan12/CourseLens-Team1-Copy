package com.courselens.service;

import com.courselens.model.Course;
import com.courselens.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final PredictionService predictionService;

    @Autowired
    public CourseService(CourseRepository courseRepository, PredictionService predictionService) {
        this.courseRepository = courseRepository;
        this.predictionService = predictionService;
    }

    public List<Course> getAllCourses(String searchTerm) {
        if (StringUtils.hasText(searchTerm)) {
            return courseRepository.findByTitleContainingIgnoreCaseOrCodeContainingIgnoreCase(searchTerm, searchTerm);
        }
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public List<Course> getPredictions() {
        return courseRepository.findAll().stream()
                .filter(predictionService::isCourseLikelyToClose)
                .collect(Collectors.toList());
    }
}
