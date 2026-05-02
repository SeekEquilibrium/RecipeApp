import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FriendService, FriendResponse } from '../services/friend.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-friends',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './friends.component.html',
  styleUrl: './friends.component.css'
})
export class FriendsComponent implements OnInit {
  private friendService = inject(FriendService);
  private router = inject(Router);
  private auth = inject(AuthService);

  activeTab = signal<'friends' | 'requests' | 'add'>('friends');

  // Friends tab
  friends = signal<FriendResponse[]>([]);
  loadingFriends = signal(true);
  removingEmail = signal<string | null>(null);
  confirmRemoveEmail = signal<string | null>(null);

  // Requests tab
  requests = signal<FriendResponse[]>([]);
  loadingRequests = signal(true);
  acceptingEmail = signal<string | null>(null);
  decliningEmail = signal<string | null>(null);

  // Add Friend tab
  addEmail = signal('');
  addStatus = signal<'idle' | 'success' | 'error'>('idle');
  addError = signal('');
  adding = signal(false);
  private sentEmails = new Set<string>();

  ngOnInit(): void {
    this.loadFriends();
    this.loadRequests();
  }

  setTab(tab: 'friends' | 'requests' | 'add'): void {
    this.activeTab.set(tab);
    if (tab === 'friends') this.loadFriends();
    if (tab === 'requests') this.loadRequests();
  }

  private loadFriends(): void {
    this.friendService.getFriends().subscribe({
      next: list => { this.friends.set((list ?? []).filter(Boolean)); this.loadingFriends.set(false); },
      error: (err) => {
        this.loadingFriends.set(false);
        if (err.status === 401) this.router.navigate(['/']);
      }
    });
  }

  private loadRequests(): void {
    this.friendService.getRequests().subscribe({
      next: list => { this.requests.set((list ?? []).filter(Boolean)); this.loadingRequests.set(false); },
      error: (err) => {
        this.loadingRequests.set(false);
        if (err.status === 401) this.router.navigate(['/']);
      }
    });
  }

  // ── Friends ──────────────────────────────────────────────────────────

  confirmRemove(email: string): void {
    this.confirmRemoveEmail.set(email);
  }

  cancelRemove(): void {
    this.confirmRemoveEmail.set(null);
  }

  openChat(friend: FriendResponse): void {
    this.router.navigate(['/chat', friend.id]);
  }

  removeFriend(email: string): void {
    this.removingEmail.set(email);
    const snapshot = this.friends();
    this.friends.update(list => list.filter(f => f.email !== email));
    this.confirmRemoveEmail.set(null);
    this.friendService.removeFriend(email).subscribe({
      next: () => { this.removingEmail.set(null); this.loadFriends(); },
      error: () => {
        this.friends.set(snapshot);
        this.removingEmail.set(null);
      }
    });
  }

  // ── Requests ─────────────────────────────────────────────────────────

  acceptRequest(email: string): void {
    this.acceptingEmail.set(email);
    const snapshot = this.requests();
    this.requests.update(list => list.filter(r => r.email !== email));
    this.friendService.acceptFriend(email).subscribe({
      next: () => {
        this.acceptingEmail.set(null);
        this.loadFriends();
        this.loadRequests();
      },
      error: () => {
        this.requests.set(snapshot);
        this.acceptingEmail.set(null);
      }
    });
  }

  declineRequest(email: string): void {
    this.decliningEmail.set(email);
    const snapshot = this.requests();
    this.requests.update(list => list.filter(r => r.email !== email));
    this.friendService.cancelRequest(email).subscribe({
      next: () => this.decliningEmail.set(null),
      error: () => {
        this.requests.set(snapshot);
        this.decliningEmail.set(null);
      }
    });
  }

  // ── Add Friend ───────────────────────────────────────────────────────

  sendRequest(): void {
    const email = this.addEmail().trim();
    if (!email || this.adding()) return;

    if (email === this.auth.currentUser()?.email) {
      this.addStatus.set('error');
      this.addError.set('You cannot send a friend request to yourself.');
      return;
    }

    if (this.sentEmails.has(email)) {
      this.addStatus.set('error');
      this.addError.set('You already sent a request to this person.');
      return;
    }

    this.adding.set(true);
    this.addStatus.set('idle');
    this.friendService.addFriend(email).subscribe({
      next: () => {
        this.sentEmails.add(email);
        this.addStatus.set('success');
        this.addEmail.set('');
        this.adding.set(false);
      },
      error: (err) => {
        this.addStatus.set('error');
        this.addError.set(err?.error?.message || 'Could not send request. Check the email and try again.');
        this.adding.set(false);
      }
    });
  }
}
