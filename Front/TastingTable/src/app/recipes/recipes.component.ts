import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { RecipeService, RecipeResponse, RecipeCategory } from '../services/recipe.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-recipes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './recipes.component.html',
  styleUrl: './recipes.component.css'
})
export class RecipesComponent implements OnInit {
  private recipeService = inject(RecipeService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  auth = inject(AuthService);

  allRecipes = signal<RecipeResponse[]>([]);
  categories = signal<RecipeCategory[]>([]);
  activeCategory = signal('All');
  searchTerm = signal('');
  loading = signal(true);
  selectedRecipe = signal<RecipeResponse | null>(null);

  favouriteNames = signal(new Set<string>());
  pendingNames = signal(new Set<string>());

  filteredRecipes = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.allRecipes().filter(r =>
      (!term || r.name.toLowerCase().includes(term))
    );
  });

  ngOnInit(): void {
    this.recipeService.getCategories().subscribe({
      next: cats => this.categories.set(cats),
      error: () => {}
    });

    if (this.auth.isLoggedIn()) {
      this.recipeService.getFavourites().subscribe({
        next: favs => this.favouriteNames.set(new Set(favs.map(r => r.name))),
        error: () => {}
      });
    }

    const category = this.route.snapshot.queryParamMap.get('category');
    if (category) {
      this.activeCategory.set(category);
      this.loadByCategory(category);
    } else {
      this.loadAll();
    }
  }

  private loadAll(): void {
    this.loading.set(true);
    this.recipeService.getAll().subscribe({
      next: recipes => { this.allRecipes.set(recipes); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  private loadByCategory(name: string): void {
    this.loading.set(true);
    this.recipeService.getByCategory(name).subscribe({
      next: recipes => { this.allRecipes.set(recipes); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  setCategory(name: string): void {
    this.activeCategory.set(name);
    this.searchTerm.set('');
    this.router.navigate([], {
      queryParams: name === 'All' ? {} : { category: name },
      replaceUrl: true
    });
    if (name === 'All') {
      this.loadAll();
    } else {
      this.loadByCategory(name);
    }
  }

  openDetail(recipe: RecipeResponse): void {
    this.selectedRecipe.set(recipe);
  }

  closeDetail(): void {
    this.selectedRecipe.set(null);
  }

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

  addRecipe(): void {
    this.router.navigate(['/recipes/add']);
  }

  isFavourite(recipe: RecipeResponse): boolean {
    return this.favouriteNames().has(recipe.name);
  }

  isPending(recipe: RecipeResponse): boolean {
    return this.pendingNames().has(recipe.name);
  }

  toggleFavourite(recipe: RecipeResponse, event: Event): void {
    event.stopPropagation();
    if (!this.auth.isLoggedIn() || this.isPending(recipe)) return;

    const name = recipe.name;
    const wasFav = this.isFavourite(recipe);

    this.pendingNames.update(s => new Set(s).add(name));

    this.favouriteNames.update(s => {
      const next = new Set(s);
      wasFav ? next.delete(name) : next.add(name);
      return next;
    });

    const call = wasFav
      ? this.recipeService.removeFavourite(name)
      : this.recipeService.addFavourite(name);

    call.subscribe({
      next: () => {
        this.pendingNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      },
      error: () => {
        this.favouriteNames.update(s => {
          const next = new Set(s);
          wasFav ? next.add(name) : next.delete(name);
          return next;
        });
        this.pendingNames.update(s => { const n = new Set(s); n.delete(name); return n; });
      }
    });
  }
}
