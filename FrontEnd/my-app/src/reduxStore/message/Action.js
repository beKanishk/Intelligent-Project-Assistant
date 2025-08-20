import axios from "axios";
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
    SEND_MESSAGE_REQUEST, SEND_MESSAGE_SUCCESS, SEND_MESSAGE_FAILURE,
    CONTINUE_AGENT_REQUEST, CONTINUE_AGENT_SUCCESS, CONTINUE_AGENT_FAILURE,
    ADD_MESSAGE, CLEAR_MESSAGES,
    LOAD_MESSAGES_REQUEST, LOAD_MESSAGES_SUCCESS, LOAD_MESSAGES_FAILURE,
    SET_USER_INPUT_FIELDS, CLEAR_USER_INPUT_FIELDS, SET_WAITING_FOR_INPUT,
    WEBSOCKET_CONNECTED, WEBSOCKET_DISCONNECTED, WEBSOCKET_ERROR
} from "./ActionType";

const baseURL = import.meta.env.VITE_BACKEND_URL;
let stompClient = null;

// WebSocket Actions
// Update this section in your connectWebSocket action
export const connectWebSocket = () => (dispatch, getState) => {
    const { auth } = getState();
    
    if (stompClient && stompClient.connected) {
        return;
    }

    // Pass JWT token as query parameter
    const socketUrl = `${baseURL}/ws?token=${auth.token}`;
    const socket = new SockJS(socketUrl);
    
    stompClient = new Client({
        webSocketFactory: () => socket,
        // Remove the connectHeaders since they don't work for handshake
        // connectHeaders: {
        //     'Authorization': `Bearer ${auth.token}`
        // },
        debug: (str) => console.log('STOMP: ' + str),
        onConnect: () => {
            console.log('WebSocket Connected');
            dispatch({ type: WEBSOCKET_CONNECTED });
        },
        onDisconnect: () => {
            console.log('WebSocket Disconnected');
            dispatch({ type: WEBSOCKET_DISCONNECTED });
        },
        onStompError: (error) => {
            console.error('STOMP Error:', error);
            dispatch({ type: WEBSOCKET_ERROR, payload: error.body });
        }
    });

    stompClient.activate();
};


let currentSubscription = null;

export const subscribeToSession = (sessionId) => (dispatch) => {
    if (!stompClient || !stompClient.connected) {
        console.log('STOMP client not connected, attempting to connect...');
        dispatch(connectWebSocket());
        setTimeout(() => {
            dispatch(subscribeToSession(sessionId));
        }, 1000);
        return;
    }

    // Unsubscribe from previous session if exists
    if (currentSubscription) {
        console.log('Unsubscribing from previous session');
        currentSubscription.unsubscribe();
    }

    console.log(`Subscribing to session: ${sessionId}`);
    currentSubscription = stompClient.subscribe(`/user/queue/session/${sessionId}`, (message) => {
        const messageData = JSON.parse(message.body);
        console.log('Received WebSocket message:', messageData);
        
        dispatch({ type: ADD_MESSAGE, payload: messageData });
        
        if (messageData.user_input_required && messageData.user_input_fields) {
            dispatch({ type: SET_USER_INPUT_FIELDS, payload: messageData.user_input_fields });
            dispatch({ type: SET_WAITING_FOR_INPUT, payload: true });
        }
    });
};



export const disconnectWebSocket = () => (dispatch) => {
    if (stompClient && stompClient.connected) {
        stompClient.deactivate();
        stompClient = null;
        dispatch({ type: WEBSOCKET_DISCONNECTED });
    }
};

// Message Actions (WebSocket Only)
export const sendMessage = (messageData) => async (dispatch, getState) => {
    dispatch({ type: SEND_MESSAGE_REQUEST });

    const { auth, session } = getState();

    if (!stompClient || !stompClient.connected) {
        console.error('WebSocket not connected');
        dispatch({ type: SEND_MESSAGE_FAILURE, payload: 'WebSocket not connected. Please refresh and try again.' });
        return;
    }

    try {
        // Add user message first
        const userMessage = {
            id: Date.now() + '_user',
            type: 'user',
            content: messageData.content,
            timestamp: new Date().toISOString(),
            sessionId: session.sessionId
        };
        dispatch({ type: ADD_MESSAGE, payload: userMessage });

        // Send via WebSocket only
        stompClient.publish({
            destination: `/app/chat/${session.sessionId}`,
            body: JSON.stringify({
                content: messageData.content,
                userId: auth.user?.id,
                tools: messageData.tools || []
            })
        });

        dispatch({ type: SEND_MESSAGE_SUCCESS });

    } catch (error) {
        console.error("Send Message Error:", error);
        dispatch({ type: SEND_MESSAGE_FAILURE, payload: error.message });
    }
};

export const continueAgent = (userInputs, runId) => async (dispatch, getState) => {
    dispatch({ type: CONTINUE_AGENT_REQUEST });

    const { auth, session } = getState();

    if (!stompClient || !stompClient.connected) {
        console.error('WebSocket not connected');
        dispatch({ type: CONTINUE_AGENT_FAILURE, payload: 'WebSocket not connected. Please refresh and try again.' });
        return;
    }

    const payload = {
        runId: runId,
        sessionId: session.sessionId,
        userId: auth.user?.id,
        userInputs: userInputs
    };

    try {
        // Send via WebSocket only
        stompClient.publish({
            destination: `/app/continue/${session.sessionId}`,
            body: JSON.stringify(payload)
        });

        dispatch({ type: CONTINUE_AGENT_SUCCESS });
        dispatch({ type: CLEAR_USER_INPUT_FIELDS });
        dispatch({ type: SET_WAITING_FOR_INPUT, payload: false });

    } catch (error) {
        console.error("Continue Agent Error:", error);
        dispatch({ type: CONTINUE_AGENT_FAILURE, payload: error.message });
    }
};

export const addMessage = (message) => ({
    type: ADD_MESSAGE,
    payload: message
});

export const loadMessages = (sessionId) => async (dispatch, getState) => {
    dispatch({ type: LOAD_MESSAGES_REQUEST });

    const { auth } = getState();

    try {
        // REST API only for loading historical messages
        const response = await axios.get(`${baseURL}/api/message/history/${sessionId}`, {
            headers: { Authorization: `Bearer ${auth.token}` }
        });

        const messages = response.data;
        console.log('Loaded messages:', messages);

        dispatch({ type: LOAD_MESSAGES_SUCCESS, payload: messages });
        return { success: true, messages };
    } catch (error) {
        console.error("Load Messages Error:", error.response?.data || error.message);
        dispatch({ 
            type: LOAD_MESSAGES_FAILURE, 
            payload: error.response?.data?.message || error.message 
        });
        return { success: false, error: error.response?.data?.message };
    }
};

export const clearMessages = () => (dispatch) => {
    dispatch({ type: CLEAR_MESSAGES });
};

export const setUserInputFields = (fields) => (dispatch) => {
    dispatch({ type: SET_USER_INPUT_FIELDS, payload: fields });
};

export const clearUserInputFields = () => (dispatch) => {
    dispatch({ type: CLEAR_USER_INPUT_FIELDS });
};

export const setWaitingForInput = (waiting) => (dispatch) => {
    dispatch({ type: SET_WAITING_FOR_INPUT, payload: waiting });
};
