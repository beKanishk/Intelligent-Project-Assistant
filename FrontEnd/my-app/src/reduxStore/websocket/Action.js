import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import * as types from './ActionType';
// Import your existing message actions
// import { addMessage } from '../message/Action';

let stompClient = null;

export const connectWebSocket = (token, sessionId) => (dispatch) => {
  if (stompClient) {
    stompClient.deactivate();
  }

  dispatch({ type: types.WEBSOCKET_CONNECT });

  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },
    debug: (str) => console.log('STOMP:', str),
    onConnect: (frame) => {
      dispatch({ type: types.WEBSOCKET_CONNECTED, payload: frame });
      
      // Subscribe to user's session
      stompClient.subscribe(`/user/queue/session/${sessionId}`, (message) => {
        const data = JSON.parse(message.body);
        
        // Add message to your existing message state
        dispatch(addMessage(data));
        dispatch({ type: types.WEBSOCKET_MESSAGE_RECEIVED, payload: data });
      });
    },
    onStompError: (frame) => {
      dispatch({ type: types.WEBSOCKET_ERROR, payload: frame });
    },
    onWebSocketError: (error) => {
      dispatch({ type: types.WEBSOCKET_ERROR, payload: error });
    }
  });

  stompClient.activate();
};

export const sendMessage = (sessionId, content, userId) => (dispatch) => {
  if (stompClient && stompClient.connected) {
    const message = { content, userId, sessionId };
    
    stompClient.publish({
      destination: `/app/chat/${sessionId}`,
      body: JSON.stringify(message)
    });
    
    // Optimistically add user message to state
    const userMessage = {
      ...message,
      type: 'user',
      id: Date.now(),
      timestamp: Date.now()
    };
    
    dispatch(addMessage(userMessage));
    dispatch({ type: types.WEBSOCKET_MESSAGE_SENT, payload: userMessage });
  }
};
