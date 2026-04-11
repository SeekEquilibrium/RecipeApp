import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-login-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './login-modal.component.html',
  styleUrl: './login-modal.component.css'
})
export class LoginModalComponent {
  @Input() action = 'access this feature';
  @Output() closed = new EventEmitter<void>();

  private auth = inject(AuthService);

  login(): void {
    this.auth.navigateToLogin();
    this.closed.emit();
  }

  register(): void {
    this.auth.navigateToRegister();
    this.closed.emit();
  }

  close(): void {
    this.closed.emit();
  }
}
