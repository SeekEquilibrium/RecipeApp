import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RecipeService, RecipeCategory } from '../../services/recipe.service';
import { AuthService } from '../../services/auth.service';
import { LoginModalComponent } from '../../shared/login-modal/login-modal.component';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, LoginModalComponent],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent implements OnInit {
  private recipeService = inject(RecipeService);
  private authService = inject(AuthService);
  private router = inject(Router);

  categories = signal<RecipeCategory[]>([]);
  loading = signal(true);
  showModal = signal(false);
  selectedCategory = signal('');

  ngOnInit(): void {
    this.recipeService.getCategories().subscribe({
      next: (cats) => {
        this.categories.set(cats);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onCategoryClick(categoryName: string): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/recipes'], { queryParams: { category: categoryName } });
    } else {
      this.selectedCategory.set(categoryName);
      this.showModal.set(true);
    }
  }

  onBrowseAll(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/recipes']);
    } else {
      this.selectedCategory.set('all categories');
      this.showModal.set(true);
    }
  }

  closeModal(): void {
    this.showModal.set(false);
  }
}
