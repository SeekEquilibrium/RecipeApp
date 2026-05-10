import {
  Component, signal, computed, ViewChild, ElementRef,
  AfterViewChecked, inject, OnInit
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ChatbotService } from '../../services/chatbot.service';

interface ChatMessage {
  id: number;
  role: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chatbot-bubble',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chatbot-bubble.component.html',
  styleUrl: './chatbot-bubble.component.css'
})
export class ChatbotBubbleComponent implements OnInit, AfterViewChecked {
  @ViewChild('messageArea') private messageArea!: ElementRef<HTMLDivElement>;

  private auth = inject(AuthService);
  private chatbot = inject(ChatbotService);

  isLoggedIn = this.auth.isLoggedIn;
  isOpen = signal(false);
  hasOpened = signal(false);
  messages = signal<ChatMessage[]>([]);
  draft = signal('');
  isLoading = signal(false);
  canSend = computed(() => !!this.draft().trim() && !this.isLoading());

  private shouldScroll = false;
  private nextId = 0;

  ngOnInit(): void {}

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  toggle(): void {
    if (!this.isOpen() && !this.hasOpened()) {
      this.hasOpened.set(true);
      this.addBotMessage("Hello! I'm your Recipe Assistant. Ask me anything about recipes, ingredients, cooking techniques, or meal planning!");
    }
    this.isOpen.update(v => !v);
  }

  close(): void {
    this.isOpen.set(false);
  }

  send(): void {
    const text = this.draft().trim();
    if (!text || this.isLoading()) return;
    this.addUserMessage(text);
    this.draft.set('');
    this.isLoading.set(true);
    this.chatbot.sendMessage(text).subscribe({
      next: (res) => { this.isLoading.set(false); this.addBotMessage(res); },
      error: () => { this.isLoading.set(false); this.addBotMessage('Sorry, I had trouble connecting. Please try again.'); }
    });
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  private addUserMessage(text: string): void {
    this.messages.update(msgs => [...msgs, { id: this.nextId++, role: 'user', text }]);
    this.shouldScroll = true;
  }

  private addBotMessage(text: string): void {
    this.messages.update(msgs => [...msgs, { id: this.nextId++, role: 'bot', text }]);
    this.shouldScroll = true;
  }

  private scrollToBottom(): void {
    try {
      const el = this.messageArea?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    } catch {}
  }
}
