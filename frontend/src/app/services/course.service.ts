import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Course } from '../models/course.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private apiUrl = '/api/courses';

  constructor(private http: HttpClient) { }

  getCourses(searchTerm?: string): Observable<Course[]> {
    let params = new HttpParams();
    if (searchTerm) {
      params = params.append('search', searchTerm);
    }
    return this.http.get<Course[]>(this.apiUrl, { params }).pipe(
      catchError(this.handleError<Course[]>('getCourses', []))
    );
  }

  getCourse(id: string): Observable<Course | undefined> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Course>(url).pipe(
      catchError(this.handleError<Course>(`getCourse id=${id}`))
    );
  }

  getPredictedCourses(): Observable<Course[]> {
    const url = `${this.apiUrl}/predictions`;
    return this.http.get<Course[]>(url).pipe(
      catchError(this.handleError<Course[]>('getPredictedCourses', []))
    );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
