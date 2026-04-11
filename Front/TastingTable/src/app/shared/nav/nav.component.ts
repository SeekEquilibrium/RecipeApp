import { Component, Output, EventEmitter, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css'
})
export class NavComponent {
  @Output() sidebarToggled = new EventEmitter<void>();

  auth = inject(AuthService);

  toggleSidebar(): void {
    this.sidebarToggled.emit();
  }

  logout(): void {
    this.auth.logout();
  }
}
