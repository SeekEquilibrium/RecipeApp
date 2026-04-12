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
  auth = inject(AuthService);
  private router = inject(Router);

  categories = signal<RecipeCategory[]>([]);
  loading = signal(true);
  showModal = signal(false);
  selectedCategory = signal('');

  favouriteCategoryNames = signal(new Set<string>());
  pendingCategoryNames = signal(new Set<string>());

  ngOnInit(): void {
    this.recipeService.getCategories().subscribe({
      next: (cats) => {
        this.categories.set(cats);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    if (this.auth.isLoggedIn()) {
      this.recipeService.getFavouriteCategories().subscribe({
        next: cats => this.favouriteCategoryNames.set(new Set(cats.map(c => c.name))),
        error: () => {}
      });
    }
  }

  isFavouriteCategory(name: string): boolean {
    return this.favouriteCategoryNames().has(name);
  }

  isPendingCategory(name: string): boolean {
    return this.pendingCategoryNames().has(name);
  }

  toggleFavouriteCategory(cat: RecipeCategory, event: Event): void {
    event.stopPropagation();
    if (!this.auth.isLoggedIn()) {
      this.selectedCategory.set(cat.name);
      this.showModal.set(true);
      return;
    }
    if (this.isPendingCategory(cat.name)) return;

    const name = cat.name;
    const wasFav = this.isFavouriteCategory(name);

    this.pendingCategoryNames.update(s => new Set(s).add(name));
    this.favouriteCategoryNames.update(s => {
      const next = new Set(s);
      wasFav ? next.delete(name) : next.add(name);
      return next;
    });

    const call = wasFav
      ? this.recipeService.removeFavouriteCategory(name)
      : this.recipeService.addFavouriteCategory(name);

    call.subscribe({
      next: () => {
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.favouriteCategoryNames.update(s => {
          const next = new Set(s);
          wasFav ? next.add(name) : next.delete(name);
          return next;
        });
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }

  onCategoryClick(categoryName: string): void {
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/recipes'], { queryParams: { category: categoryName } });
    } else {
      this.selectedCategory.set(categoryName);
      this.showModal.set(true);
    }
  }

  onBrowseAll(): void {
    if (this.auth.isLoggedIn()) {
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
