import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormArray, Validators, AbstractControl } from '@angular/forms';
import { RecipeService, RecipeCategory } from '../../services/recipe.service';

@Component({
  selector: 'app-add-recipe',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './add-recipe.component.html',
  styleUrl: './add-recipe.component.css'
})
export class AddRecipeComponent implements OnInit {
  private fb = inject(FormBuilder);
  private recipeService = inject(RecipeService);
  private router = inject(Router);

  categories: RecipeCategory[] = [];
  imageFile: File | null = null;
  imagePreview: string | null = null;
  submitted = false;
  loading = false;
  serverError = '';

  form = this.fb.group({
    name:            ['', [Validators.required, Validators.minLength(3)]],
    preparation:     ['', [Validators.required, Validators.minLength(10)]],
    servings:        ['', Validators.required],
    recipeCategory:  ['', Validators.required],
    ingredients: this.fb.array([this.createIngredientRow()])
  });

  get f() { return this.form.controls; }
  get ingredients(): FormArray { return this.form.get('ingredients') as FormArray; }

  createIngredientRow() {
    return this.fb.group({
      ingredientName: ['', Validators.required],
      amount:         ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.recipeService.getCategories().subscribe({
      next: cats => this.categories = cats,
      error: () => {}
    });
  }

  addIngredient(): void {
    this.ingredients.push(this.createIngredientRow());
  }

  removeIngredient(index: number): void {
    if (this.ingredients.length > 1) {
      this.ingredients.removeAt(index);
    }
  }

  onImageChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.imageFile = file;
    if (file) {
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = e.target?.result as string;
      reader.readAsDataURL(file);
    } else {
      this.imagePreview = null;
    }
  }

  clearImage(): void {
    this.imageFile = null;
    this.imagePreview = null;
  }

  fieldInvalid(ctrl: AbstractControl | null): boolean {
    return !!ctrl && this.submitted && ctrl.invalid;
  }

  onSubmit(): void {
    this.submitted = true;
    this.serverError = '';
    if (this.form.invalid) return;

    const val = this.form.value;
    const fd = new FormData();
    fd.append('name', val.name!);
    fd.append('preparation', val.preparation!);
    fd.append('servings', val.servings!);
    fd.append('recipeCategory', val.recipeCategory!);

    (val.ingredients as any[]).forEach((ing, i) => {
      fd.append(`ingredientRequestDTOList[${i}].ingredientName`, ing.ingredientName);
      fd.append(`ingredientRequestDTOList[${i}].amount`, ing.amount);
    });

    if (this.imageFile) {
      fd.append('image', this.imageFile);
    }

    this.loading = true;
    this.recipeService.createRecipe(fd).subscribe({
      next: () => this.router.navigate(['/recipes']),
      error: (err) => {
        this.loading = false;
        this.serverError = err.status === 403
          ? 'You do not have permission to add recipes.'
          : 'Failed to create recipe. Please try again.';
      }
    });
  }
}
