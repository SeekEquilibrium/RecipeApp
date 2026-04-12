import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface RecipeResponse {
  name: string;
  servings?: string;
  preparation?: string;
  recipeCategoryName?: string;
  recipeCategoryDescription?: string;
  ingredientList?: { ingredientName: string; amount: string }[];
  imageName?: string;
  imageId?: number;
  createdDateTime?: string;
}

export interface RecipeCategory {
  name: string;
  description?: string;
}

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  getAll(): Observable<RecipeResponse[]> {
    return this.http.get<RecipeResponse[]>(`${this.baseUrl}/recipe/getAll`);
  }

  getByCategory(categoryName: string): Observable<RecipeResponse[]> {
    return this.http.get<RecipeResponse[]>(
      `${this.baseUrl}/recipe/filter/category`, { params: { categoryName } }
    );
  }

  getCategories(): Observable<RecipeCategory[]> {
    return this.http.get<{ recipeCategoryDTOList: RecipeCategory[] }>(
      `${this.baseUrl}/category/getAll`
    ).pipe(map(r => r.recipeCategoryDTOList ?? []));
  }

  createRecipe(formData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/recipe`, formData);
  }

  getImage(imageId: number): string {
    return `${this.baseUrl}/image/getImage?imageId=${imageId}`;
  }

  getFavourites(): Observable<RecipeResponse[]> {
    return this.http.get<RecipeResponse[]>(`${this.baseUrl}/recipe/favourite/getAll`);
  }

  addFavourite(recipeName: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/recipe/favourite/add`, null, { params: { recipeName } });
  }

  removeFavourite(recipeName: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/recipe/favourite/delete`, { params: { recipeName } });
  }
}
