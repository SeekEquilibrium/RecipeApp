import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RecipeService, RecipeResponse, RecipeCategory } from '../services/recipe.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {
  private recipeService = inject(RecipeService);
  private router = inject(Router);
  auth = inject(AuthService);

  activeTab = signal<'analytics' | 'categories' | 'ingredients'>('analytics');

  // Analytics
  categoryStats = signal<{ categoryName: string; count: number }[]>([]);
  loadingStats = signal(true);
  selectedDate = signal('');
  dateCount = signal<number | null>(null);
  dateRecipes = signal<RecipeResponse[]>([]);
  loadingDate = signal(false);

  // Categories
  categories = signal<RecipeCategory[]>([]);
  loadingCategories = signal(true);
  newCategoryName = signal('');
  newCategoryDesc = signal('');
  categoryError = signal('');
  pendingCategoryNames = signal(new Set<string>());

  // Ingredients
  ingredients = signal<{ name: string }[]>([]);
  loadingIngredients = signal(true);
  newIngredientName = signal('');
  ingredientError = signal('');
  pendingIngredientNames = signal(new Set<string>());

  ngOnInit(): void {
    if (!this.auth.isAdmin()) {
      this.router.navigate(['/']);
      return;
    }
    this.loadStats();
    this.loadCategories();
    this.loadIngredients();
  }

  setTab(tab: 'analytics' | 'categories' | 'ingredients'): void {
    this.activeTab.set(tab);
  }

  // ── Analytics ──────────────────────────────────────────────────────
  private loadStats(): void {
    this.recipeService.countAllCategories().subscribe({
      next: stats => { this.categoryStats.set(stats); this.loadingStats.set(false); },
      error: () => this.loadingStats.set(false)
    });
  }

  searchByDate(): void {
    const date = this.selectedDate();
    if (!date) return;
    this.loadingDate.set(true);
    this.dateCount.set(null);
    this.dateRecipes.set([]);
    this.recipeService.countRecipesByDate(date).subscribe({
      next: count => this.dateCount.set(count),
      error: () => this.dateCount.set(0)
    });
    this.recipeService.getRecipesByDate(date).subscribe({
      next: recipes => { this.dateRecipes.set(recipes); this.loadingDate.set(false); },
      error: () => this.loadingDate.set(false)
    });
  }

  // ── Categories ─────────────────────────────────────────────────────
  private loadCategories(): void {
    this.recipeService.getCategories().subscribe({
      next: cats => { this.categories.set(cats); this.loadingCategories.set(false); },
      error: () => this.loadingCategories.set(false)
    });
  }

  addCategory(): void {
    const name = this.newCategoryName().trim();
    const desc = this.newCategoryDesc().trim();
    if (!name) return;
    this.categoryError.set('');
    this.recipeService.createCategory(name, desc).subscribe({
      next: () => {
        this.categories.update(list => [...list, { name, description: desc }]);
        this.newCategoryName.set('');
        this.newCategoryDesc.set('');
      },
      error: () => this.categoryError.set('Category already exists or could not be created.')
    });
  }

  deleteCategory(name: string): void {
    if (this.pendingCategoryNames().has(name)) return;
    this.pendingCategoryNames.update(s => new Set(s).add(name));
    const snapshot = this.categories();
    this.categories.update(list => list.filter(c => c.name !== name));
    this.recipeService.deleteCategoryByName(name).subscribe({
      next: () => {
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.categories.set(snapshot);
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }

  // ── Ingredients ────────────────────────────────────────────────────
  private loadIngredients(): void {
    this.recipeService.getIngredients().subscribe({
      next: items => { this.ingredients.set(items); this.loadingIngredients.set(false); },
      error: () => this.loadingIngredients.set(false)
    });
  }

  addIngredient(): void {
    const name = this.newIngredientName().trim();
    if (!name) return;
    this.ingredientError.set('');
    this.recipeService.createIngredient(name).subscribe({
      next: () => {
        this.ingredients.update(list => [...list, { name }]);
        this.newIngredientName.set('');
      },
      error: () => this.ingredientError.set('Ingredient already exists or could not be created.')
    });
  }

  deleteIngredient(name: string): void {
    if (this.pendingIngredientNames().has(name)) return;
    this.pendingIngredientNames.update(s => new Set(s).add(name));
    const snapshot = this.ingredients();
    this.ingredients.update(list => list.filter(i => i.name !== name));
    this.recipeService.deleteIngredient(name).subscribe({
      next: () => {
        this.pendingIngredientNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.ingredients.set(snapshot);
        this.pendingIngredientNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }
}
