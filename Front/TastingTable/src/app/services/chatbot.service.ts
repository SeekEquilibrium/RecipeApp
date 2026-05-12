import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RecipeBotResponse {
  recipeName?: string;
  description?: string;
  cuisine?: string;
  diet?: string;
  course?: string;
  prepTime?: string;
  cookTime?: string;
  ingredients?: { name: string; quantity: string }[];
  steps?: string[];
  answer?: string;
  error?: string;
}

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private http = inject(HttpClient);
  private readonly base = 'http://localhost:8080';

  sendMessage(message: string, sessionId: string): Observable<RecipeBotResponse> {
    return this.http.post<RecipeBotResponse>(
      `${this.base}/recipeBot/chat`,
      { message, sessionId }
    );
  }
}
