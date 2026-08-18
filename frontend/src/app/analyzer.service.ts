import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

export interface AnalysisResult {
  notFollowingBack: string[];
  youDoNotFollowBack: string[];
  mutualFollowers: string[];
  followers: string[];
  following: string[];
  followersCount: number;
  followingCount: number;
  notFollowingBackCount: number;
  youDoNotFollowBackCount: number;
  mutualFollowersCount: number;
}

export interface AnalyzerError {
  message: string;
  code?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyzerService {
  constructor(private readonly http: HttpClient) {}

  analyze(followersFile: File, followingFile: File): Observable<AnalysisResult> {
    const formData = new FormData();
    formData.append('followers', followersFile);
    formData.append('following', followingFile);

    return this.http
      .post<AnalysisResult>(`${environment.apiUrl}/api/analyzer/analyze`, formData)
      .pipe(catchError((error: HttpErrorResponse) => throwError(() => this.toAnalyzerError(error))));
  }

  private toAnalyzerError(error: HttpErrorResponse): AnalyzerError {
    const body = error.error;
    if (body && typeof body === 'object' && typeof body.message === 'string') {
      return {
        message: body.message,
        code: typeof body.code === 'string' ? body.code : undefined
      };
    }

    if (error.status === 0) {
      return {
        message: 'No se ha podido conectar con el servidor. Comprueba que el backend está en marcha.'
      };
    }

    return {
      message: 'No se ha podido completar el análisis. Inténtalo de nuevo.'
    };
  }
}
