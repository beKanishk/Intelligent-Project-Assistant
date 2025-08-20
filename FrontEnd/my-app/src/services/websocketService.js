import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
  }

  connect(token, onMessage, onError) {
    if (this.client) {
      this.disconnect();
    }

    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      onConnect: (frame) => {
        console.log('✅ Connected to WebSocket:', frame);
        this.connected = true;
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
        if (onError) onError(frame);
      },
      onWebSocketError: (error) => {
        console.error('❌ WebSocket error:', error);
        if (onError) onError(error);
      },
      onDisconnect: () => {
        console.log('🔌 Disconnected from WebSocket');
        this.connected = false;
      }
    });

    this.client.activate();
    return this.client;
  }

  subscribeToSession(sessionId, onMessage) {
    if (!this.client || !this.connected) {
      console.error('WebSocket not connected');
      return null;
    }

    const subscription = this.client.subscribe(
      `/user/queue/session/${sessionId}`,
      (message) => {
        try {
          const data = JSON.parse(message.body);
          console.log('📨 Received message:', data);
          if (onMessage) onMessage(data);
        } catch (error) {
          console.error('Error parsing message:', error);
        }
      }
    );

    console.log(`🔔 Subscribed to session: ${sessionId}`);
    return subscription;
  }

  sendMessage(sessionId, content, userId) {
    if (!this.client || !this.connected) {
      console.error('WebSocket not connected');
      return false;
    }

    const message = {
      content: content,
      userId: userId,
      sessionId: sessionId
    };

    this.client.publish({
      destination: `/app/chat/${sessionId}`,
      body: JSON.stringify(message)
    });

    console.log('📤 Sent message:', message);
    return true;
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.connected = false;
      console.log('🔌 WebSocket disconnected');
    }
  }

  isConnected() {
    return this.connected;
  }
}

export const websocketService = new WebSocketService();
export default websocketService;
