import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecipeService, RecipeResponse } from '../../services/recipe.service';
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

  openModal(): void {
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }
}
