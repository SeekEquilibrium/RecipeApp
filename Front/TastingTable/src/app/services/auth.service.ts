import { Injectable, signal, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, of } from 'rxjs';

export interface UserDTO {
  name: string;
  surname: string;
  email: string;
  phoneNumber: string;
  gender: string;
  street: string;
  city: string;
  country: string;
}

export interface RegisterDTO {
  name: string;
  surname: string;
  email: string;
  password: string;
  phoneNumber: string;
  gender: string;
  street: string;
  city: string;
  country: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly base = 'http://localhost:8080/auth';

  isLoggedIn = signal(false);
  currentUser = signal<UserDTO | null>(null);

  constructor() {
    this.loadCurrentUser();
  }

  loadCurrentUser(): void {
    this.http.get<UserDTO>(`${this.base}/current`).pipe(
      catchError(() => of(null))
    ).subscribe(user => {
      this.currentUser.set(user);
      this.isLoggedIn.set(!!user);
    });
  }

  login(email: string, password: string) {
    return this.http.post<{ accessToken: string }>(`${this.base}/login`, { email, password });
  }

  register(dto: RegisterDTO) {
    return this.http.post<{ accessToken: string }>(`${this.base}/register`, dto);
  }

  refreshToken() {
    return this.http.post(`${this.base}/refresh-token`, {});
  }

  clearSession(): void {
    this.currentUser.set(null);
    this.isLoggedIn.set(false);
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.http.post(`${this.base}/logout`, {}).pipe(
      catchError(() => of(null))
    ).subscribe(() => {
      this.currentUser.set(null);
      this.isLoggedIn.set(false);
      this.router.navigate(['/']);
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
