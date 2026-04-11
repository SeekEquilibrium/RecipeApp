import { Component, inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    name:        ['', Validators.required],
    surname:     ['', Validators.required],
    email:       ['', [Validators.required, Validators.email]],
    password:    ['', [Validators.required, Validators.minLength(8)]],
    phoneNumber: ['', Validators.required],
    gender:      ['', Validators.required],
    street:      ['', Validators.required],
    city:        ['', Validators.required],
    country:     ['', Validators.required]
  });

  submitted = false;
  loading = false;
  serverError = '';

  get f() { return this.form.controls; }

  onSubmit(): void {
    this.submitted = true;
    this.serverError = '';
    if (this.form.invalid) return;

    this.loading = true;
    this.auth.register(this.form.value as any).subscribe({
      next: () => {
        this.auth.loadCurrentUser();
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.serverError = 'An account with this email already exists.';
        } else {
          this.serverError = 'Something went wrong. Please try again.';
        }
      }
    });
  }
}
