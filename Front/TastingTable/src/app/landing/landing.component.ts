import { Component, signal, HostListener, OnInit, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../shared/nav/nav.component';
import { SidebarComponent } from '../shared/sidebar/sidebar.component';
import { HeroComponent } from '../sections/hero/hero.component';
import { FeaturedRecipeComponent } from '../sections/featured-recipe/featured-recipe.component';
import { FeaturesComponent } from '../sections/features/features.component';
import { CategoriesComponent } from '../sections/categories/categories.component';
import { FooterComponent } from '../sections/footer/footer.component';

const MOBILE_BREAKPOINT = 768;

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [
    CommonModule,
    NavComponent,
    SidebarComponent,
    HeroComponent,
    FeaturedRecipeComponent,
    FeaturesComponent,
    CategoriesComponent,
    FooterComponent
  ],
  templateUrl: './landing.component.html',
  styleUrl: './landing.component.css'
})
export class LandingComponent implements OnInit {
  private platformId = inject(PLATFORM_ID);

  sidebarOpen = signal(true);
  isMobile = signal(false);

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.checkScreen();
    }
  }

  @HostListener('window:resize')
  checkScreen(): void {
    const mobile = window.innerWidth <= MOBILE_BREAKPOINT;
    this.isMobile.set(mobile);
    this.sidebarOpen.set(!mobile);
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }
}
