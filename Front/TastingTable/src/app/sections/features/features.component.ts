import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginModalComponent } from '../../shared/login-modal/login-modal.component';

interface Feature {
  icon: string;
  title: string;
  description: string;
  action: string;
}

@Component({
  selector: 'app-features',
  standalone: true,
  imports: [CommonModule, LoginModalComponent],
  templateUrl: './features.component.html',
  styleUrl: './features.component.css'
})
export class FeaturesComponent {
  showModal = signal(false);
  modalAction = signal('access this feature');

  features: Feature[] = [
    {
      icon: '📝',
      title: 'Recipe Creation',
      description: 'Build and share your own recipes with ingredients, steps, photos, and categories.',
      action: 'create recipes'
    },
    {
      icon: '👥',
      title: 'Friends & Social',
      description: 'Connect with fellow food enthusiasts, send friend requests, and share your culinary journey.',
      action: 'connect with friends'
    },
    {
      icon: '🤖',
      title: 'AI Chef Assistant',
      description: 'Get personalized cooking tips, substitution ideas, and recipe recommendations from our AI chatbot.',
      action: 'chat with the AI chef'
    },
    {
      icon: '❤️',
      title: 'Favorites',
      description: 'Save your favorite recipes and categories to quickly find them again whenever you need inspiration.',
      action: 'save favorites'
    }
  ];

  openModal(action: string): void {
    this.modalAction.set(action);
    this.showModal.set(true);
  }

  closeModal(): void {
    this.showModal.set(false);
  }
}
