import { Component, OnInit, OnDestroy, inject, signal, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ChatService, MessageDTO } from '../services/chat.service';
import { FriendService } from '../services/friend.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('messageList') private messageList!: ElementRef<HTMLDivElement>;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private chatService = inject(ChatService);
  private friendService = inject(FriendService);
  private auth = inject(AuthService);

  friendId = 0;
  friendName = signal('');
  friendInitials = signal('');
  messages = signal<MessageDTO[]>([]);
  draft = signal('');
  loading = signal(true);

  private sub!: Subscription;
  private shouldScroll = false;

  ngOnInit(): void {
    this.friendId = Number(this.route.snapshot.paramMap.get('userId'));

    this.friendService.getFriends().subscribe(friends => {
      const f = friends.find(fr => fr.id === this.friendId);
      if (f) {
        this.friendName.set(`${f.name} ${f.surname}`);
        this.friendInitials.set(`${f.name[0]}${f.surname[0]}`);
      }
    });

    this.chatService.getHistory(this.friendId).subscribe({
      next: (msgs) => {
        this.messages.set(msgs);
        this.loading.set(false);
        this.shouldScroll = true;
      },
      error: () => this.loading.set(false)
    });

    this.chatService.connect();

    this.sub = this.chatService.messages$.subscribe(msg => {
      if (msg.fromUserId === this.friendId || msg.toUserId === this.friendId) {
        this.messages.update(list => [...list, msg]);
        this.shouldScroll = true;
      }
    });
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
    this.chatService.disconnect();
  }

  send(): void {
    const content = this.draft().trim();
    if (!content) return;
    this.chatService.sendMessage(this.friendId, content);
    this.draft.set('');
  }

  back(): void {
    this.router.navigate(['/friends']);
  }

  isSent(msg: MessageDTO): boolean {
    return msg.fromUserId !== this.friendId;
  }

  formatTime(time: string): string {
    const d = new Date(time);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }

  private scrollToBottom(): void {
    try {
      const el = this.messageList?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    } catch {}
  }
}
