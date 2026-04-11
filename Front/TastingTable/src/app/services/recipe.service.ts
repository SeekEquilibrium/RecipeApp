import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecipeResponse {
  id: number;
  name: string;
  description?: string;
  categoryName?: string;
  imageId?: number;
  createdAt?: string;
}

export interface RecipeCategory {
  id: number;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  getAll(): Observable<RecipeResponse[]> {
    return this.http.get<RecipeResponse[]>(`${this.baseUrl}/recipe/getAll`);
  }

  getCategories(): Observable<RecipeCategory[]> {
    return this.http.get<RecipeCategory[]>(`${this.baseUrl}/category/getAll`);
  }

  getImage(imageId: number): string {
    return `${this.baseUrl}/image/getImage?imageId=${imageId}`;
  }
}
