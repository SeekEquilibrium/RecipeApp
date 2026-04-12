import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RecipeService, RecipeResponse, RecipeCategory } from '../services/recipe.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-my-kitchen',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-kitchen.component.html',
  styleUrl: './my-kitchen.component.css'
})
export class MyKitchenComponent implements OnInit {
  private recipeService = inject(RecipeService);
  private router = inject(Router);
  auth = inject(AuthService);

  favouriteRecipes = signal<RecipeResponse[]>([]);
  favouriteCategories = signal<RecipeCategory[]>([]);
  loadingRecipes = signal(true);
  loadingCategories = signal(true);
  activeTab = signal<'recipes' | 'categories'>('recipes');
  selectedRecipe = signal<RecipeResponse | null>(null);
  pendingRecipeNames = signal(new Set<string>());
  pendingCategoryNames = signal(new Set<string>());

  ngOnInit(): void {
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/']);
      return;
    }

    this.recipeService.getFavourites().subscribe({
      next: recipes => { this.favouriteRecipes.set(recipes); this.loadingRecipes.set(false); },
      error: () => this.loadingRecipes.set(false)
    });

    this.recipeService.getFavouriteCategories().subscribe({
      next: cats => { this.favouriteCategories.set(cats); this.loadingCategories.set(false); },
      error: () => this.loadingCategories.set(false)
    });
  }

  setTab(tab: 'recipes' | 'categories'): void {
    this.activeTab.set(tab);
  }

  // Recipe helpers
  hasImage(recipe: RecipeResponse): boolean {
    return !!recipe.imageId;
  }

  imageUrl(recipe: RecipeResponse): string {
    return this.recipeService.getImage(recipe.imageId!);
  }

  onImgError(event: Event): void {
    const el = event.target as HTMLImageElement;
    el.style.display = 'none';
    const placeholder = el.nextElementSibling as HTMLElement;
    if (placeholder) placeholder.style.display = 'flex';
  }

  openDetail(recipe: RecipeResponse): void {
    this.selectedRecipe.set(recipe);
  }

  closeDetail(): void {
    this.selectedRecipe.set(null);
  }

  isPendingRecipe(recipe: RecipeResponse): boolean {
    return this.pendingRecipeNames().has(recipe.name);
  }

  removeRecipe(recipe: RecipeResponse, event: Event): void {
    event.stopPropagation();
    if (this.isPendingRecipe(recipe)) return;

    const name = recipe.name;
    this.pendingRecipeNames.update(s => new Set(s).add(name));

    const snapshot = this.favouriteRecipes();
    this.favouriteRecipes.update(list => list.filter(r => r.name !== name));

    this.recipeService.removeFavourite(name).subscribe({
      next: () => {
        this.pendingRecipeNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.favouriteRecipes.set(snapshot);
        this.pendingRecipeNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }

  // Category helpers
  isPendingCategory(cat: RecipeCategory): boolean {
    return this.pendingCategoryNames().has(cat.name);
  }

  goToCategory(cat: RecipeCategory, event: Event): void {
    event.stopPropagation();
    this.router.navigate(['/recipes'], { queryParams: { category: cat.name } });
  }

  removeCategory(cat: RecipeCategory, event: Event): void {
    event.stopPropagation();
    if (this.isPendingCategory(cat)) return;

    const name = cat.name;
    this.pendingCategoryNames.update(s => new Set(s).add(name));

    const snapshot = this.favouriteCategories();
    this.favouriteCategories.update(list => list.filter(c => c.name !== name));

    this.recipeService.removeFavouriteCategory(name).subscribe({
      next: () => {
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.favouriteCategories.set(snapshot);
        this.pendingCategoryNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }
}
