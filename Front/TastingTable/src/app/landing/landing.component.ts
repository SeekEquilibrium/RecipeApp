import { Component } from '@angular/core';
import { HeroComponent } from '../sections/hero/hero.component';
import { FeaturedRecipeComponent } from '../sections/featured-recipe/featured-recipe.component';
import { FeaturesComponent } from '../sections/features/features.component';
import { CategoriesComponent } from '../sections/categories/categories.component';
import { FooterComponent } from '../sections/footer/footer.component';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    HeroComponent,
    FeaturedRecipeComponent,
    FeaturesComponent,
    CategoriesComponent,
    FooterComponent
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent {}
