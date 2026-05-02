import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FriendResponse {
  id: number;
  name: string;
  surname: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class FriendService {
  private http = inject(HttpClient);
  private base = 'http://localhost:8080/relationship';

  getFriends(): Observable<FriendResponse[]> {
    return this.http.get<FriendResponse[]>(`${this.base}/getFriends`);
  }

  getRequests(): Observable<FriendResponse[]> {
    return this.http.get<FriendResponse[]>(`${this.base}/getRequests`);
  }

  addFriend(email: string): Observable<string> {
    return this.http.post(`${this.base}/addFriend`, null, { params: { email }, responseType: 'text' });
  }

  acceptFriend(email: string): Observable<string> {
    return this.http.post(`${this.base}/acceptFriend`, null, { params: { email }, responseType: 'text' });
  }

  cancelRequest(email: string): Observable<string> {
    return this.http.post(`${this.base}/cancelFriendRequest`, null, { params: { email }, responseType: 'text' });
  }

  removeFriend(email: string): Observable<string> {
    return this.http.post(`${this.base}/removeFriend`, null, { params: { email }, responseType: 'text' });
  }
}
