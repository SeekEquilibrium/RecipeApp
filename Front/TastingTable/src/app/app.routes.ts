import { Routes } from '@angular/router';
import { LandingComponent } from './landing/landing.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { RecipesComponent } from './recipes/recipes.component';
import { AddRecipeComponent } from './recipes/add-recipe/add-recipe.component';
import { MyKitchenComponent } from './my-kitchen/my-kitchen.component';
import { AdminComponent } from './admin/admin.component';

export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'recipes', component: RecipesComponent },
  { path: 'recipes/add', component: AddRecipeComponent },
  { path: 'my-kitchen', component: MyKitchenComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: '' }
];
