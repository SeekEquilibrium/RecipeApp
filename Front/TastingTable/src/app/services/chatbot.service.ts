import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private http = inject(HttpClient);
  private readonly base = 'http://localhost:8080';

  sendMessage(message: string): Observable<string> {
    return this.http.post(
      `${this.base}/recipeBot/chat`,
      { message },
      { responseType: 'text' }
    );
  }
}
