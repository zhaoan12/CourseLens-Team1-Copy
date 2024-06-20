import { Component, OnInit } from '@angular/core';
import { Course } from '../../models/course.model';
import { CourseService } from '../../services/course.service';

@Component({
  selector: 'app-predictions',
  templateUrl: './predictions.component.html',
  styleUrls: ['./predictions.component.css']
})
export class PredictionsComponent implements OnInit {
  predictedCourses: Course[] = [];

  constructor(private courseService: CourseService) { }

  ngOnInit(): void {
    this.courseService.getPredictedCourses().subscribe(courses => {
      this.predictedCourses = courses;
    });
  }
}
