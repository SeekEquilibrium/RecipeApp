import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Client } from '@stomp/stompjs';

export interface MessageDTO {
  id: number;
  toUserId: number;
  fromUserId: number;
  content: string;
  time: string;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);
  private readonly base = 'http://localhost:8080';

  messages$ = new Subject<MessageDTO>();
  private client: Client | null = null;

  async connect(): Promise<void> {
    if (!isPlatformBrowser(this.platformId)) return;

    const token = sessionStorage.getItem('accessToken') ?? '';
    if (!(window as any)['global']) (window as any)['global'] = window;
    const { default: SockJS } = await import('sockjs-client');

    this.client = new Client({
      webSocketFactory: () => new SockJS(`${this.base}/socket`),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        this.client!.subscribe(
          `/user/queue/position-update`,
          (msg) => {
            try {
              const dto: MessageDTO = JSON.parse(msg.body);
              this.messages$.next(dto);
            } catch {
              // malformed frame — ignore
            }
          }
        );
      },
    });

    this.client.activate();
  }

  disconnect(): void {
    if (this.client?.active) {
      this.client.deactivate();
    }
    this.client = null;
  }

  sendMessage(toUserId: number, content: string): void {
    if (!this.client?.connected) return;
    this.client.publish({
      destination: '/app/message',
      body: JSON.stringify({ toUserId, content }),
    });
  }

  getHistory(friendId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.base}/message/all/${friendId}`);
  }
}
