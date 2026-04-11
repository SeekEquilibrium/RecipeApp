import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RecipeService, RecipeResponse } from '../../services/recipe.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../shared/login-modal/login-modal.component';

@Component({
  selector: 'app-featured-recipe',
  standalone: true,
  imports: [CommonModule, LoginModalComponent],
  templateUrl: './featured-recipe.component.html',
  styleUrl: './featured-recipe.component.css'
})
export class FeaturedRecipeComponent implements OnInit {
  private recipeService = inject(RecipeService);
  private authService = inject(AuthService);
  private router = inject(Router);

  recipe = signal<RecipeResponse | null>(null);
  loading = signal(true);
  showModal = signal(false);

  ngOnInit(): void {
    this.recipeService.getAll().subscribe({
      next: (recipes) => {
        this.recipe.set(recipes[0] ?? null);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  viewAll(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/recipes']);
    } else {
      this.showModal.set(true);
    }
  }

  closeModal(): void {
    this.showModal.set(false);
  }

  onImgError(event: Event): void {
    const el = event.target as HTMLImageElement;
    el.style.display = 'none';
    const placeholder = el.nextElementSibling as HTMLElement;
    if (placeholder) placeholder.style.display = 'flex';
  }
}
