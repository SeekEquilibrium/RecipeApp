import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <a routerLink="/" class="auth-back">&larr; Back to Home</a>
        <div class="auth-logo">&#x1F373;</div>
        <h1>Welcome back</h1>
        <p class="auth-sub">Login page coming soon</p>
        <a routerLink="/register" class="btn-primary auth-btn">Create an Account</a>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      min-height: 100vh;
      background: var(--color-bg);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 2rem;
    }
    .auth-card {
      background: var(--color-surface);
      border-radius: var(--radius-lg);
      padding: 3rem 2.5rem;
      max-width: 400px;
      width: 100%;
      text-align: center;
      box-shadow: var(--shadow-lg);
      border: 1px solid var(--color-border);
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 1rem;
    }
    .auth-back {
      align-self: flex-start;
      font-size: 0.88rem;
      color: var(--color-text-muted);
      transition: color 0.2s;
    }
    .auth-back:hover { color: var(--color-primary); }
    .auth-logo { font-size: 3rem; }
    h1 { font-size: 1.6rem; font-weight: 700; }
    .auth-sub { color: var(--color-text-muted); font-size: 0.95rem; }
    .auth-btn { margin-top: 0.5rem; }
  `]
})
export class LoginComponent {}
