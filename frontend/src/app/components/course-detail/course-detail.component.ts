import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Course } from '../../models/course.model';
import { CourseService } from '../../services/course.service';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class CourseDetailComponent implements OnInit, AfterViewInit {
  course: Course | undefined;
  @ViewChild('enrollmentChart') private chartRef!: ElementRef;
  chart: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private courseService: CourseService
  ) { }

  ngOnInit(): void {
    const courseId = this.route.snapshot.paramMap.get('id');
    if (courseId) {
      this.courseService.getCourse(courseId).subscribe(course => {
        if (course) {
          this.course = course;
        } else {
          this.router.navigate(['/']);
        }
      });
    }
  }

  ngAfterViewInit(): void {
    if (this.course) {
      this.createChart();
    }
  }

  createChart(): void {
    const context = this.chartRef.nativeElement.getContext('2d');
    this.chart = new Chart(context, {
      type: 'line',
      data: {
        labels: this.course?.historicalData.map(d => d.date),
        datasets: [{
          label: 'Enrollment Over Time',
          data: this.course?.historicalData.map(d => d.enrolled),
          borderColor: '#3e95cd',
          fill: false
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    });
  }
}
