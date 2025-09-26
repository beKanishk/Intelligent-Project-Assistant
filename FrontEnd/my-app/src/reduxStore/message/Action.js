import axios from "axios";
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
    SEND_MESSAGE_REQUEST, SEND_MESSAGE_SUCCESS, SEND_MESSAGE_FAILURE,
    CONTINUE_AGENT_REQUEST, CONTINUE_AGENT_SUCCESS, CONTINUE_AGENT_FAILURE,
    ADD_MESSAGE, CLEAR_MESSAGES,
    LOAD_MESSAGES_REQUEST, LOAD_MESSAGES_SUCCESS, LOAD_MESSAGES_FAILURE,
    SET_USER_INPUT_FIELDS, CLEAR_USER_INPUT_FIELDS, SET_WAITING_FOR_INPUT,
    WEBSOCKET_CONNECTED, WEBSOCKET_DISCONNECTED, WEBSOCKET_ERROR,
    SET_AI_PROCESSING
} from "./ActionType";
import { useSelector } from "react-redux";

const baseURL = import.meta.env.VITE_BACKEND_URL;
export let stompClient = null;

let subscribedSessionId = null;

// WebSocket Actions
// Update this section in your connectWebSocket action
// export const connectWebSocket = () => (dispatch, getState) => {
//     const { auth } = getState();
    
//     if (stompClient && stompClient.connected) {
//         return;
//     }

//     // Pass JWT token as query parameter
//     // const socketUrl = `${baseURL}/ws?token=${auth.token}`;
//     const socketUrl = `http://localhost:8085/ws?token=${auth.token}`;
//     const socket = new SockJS(socketUrl);
    
//     stompClient = new Client({
//         webSocketFactory: () => socket,
//         // Remove the connectHeaders since they don't work for handshake
//         // connectHeaders: {
//         //     'Authorization': `Bearer ${auth.token}`
//         // },
//         debug: (str) => console.log('STOMP: ' + str),
//         onConnect: () => {
//             console.log('WebSocket Connected');
//             dispatch({ type: WEBSOCKET_CONNECTED });
//         },
//         onDisconnect: () => {
//             console.log('WebSocket Disconnected');
//             dispatch({ type: WEBSOCKET_DISCONNECTED });
//         },
//         onStompError: (error) => {
//             console.error('STOMP Error:', error);
//             dispatch({ type: WEBSOCKET_ERROR, payload: error.body });
//         }
//     });

//     stompClient.activate();
// };


export const connectWebSocket = () => (dispatch, getState) => {
    const { auth } = getState();
    const { session } = getState(); // safe here
    const sessionId = session?.sessionId;

    if (!sessionId) return;

    if (stompClient) {
        if (stompClient.connected) return;
        if (stompClient.active) return; // already activating
    }

    const socketUrl = `http://localhost:8085/ws?token=${auth.token}`;
    const socket = new SockJS(socketUrl);

    stompClient = new Client({
        webSocketFactory: () => socket,
        debug: (str) => console.log('STOMP: ' + str),
        onConnect: () => {
            console.log('WebSocket Connected');
            dispatch({ type: WEBSOCKET_CONNECTED });
            if (sessionId) {
                dispatch(subscribeToSession(sessionId));
            }
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

    // Return a promise that resolves once connected
    return new Promise((resolve) => {
        const checkConnected = () => {
            if (stompClient.connected) {
                resolve();
            } else {
                setTimeout(checkConnected, 50);
            }
        };
        checkConnected();
    });
};


let currentSubscription = null;

// export const subscribeToSession = (sessionId) => (dispatch) => {
//     // ... existing connection logic ...
    
//     currentSubscription = stompClient.subscribe(`/user/queue/session/${sessionId}`, (message) => {
//         const messageData = JSON.parse(message.body);
//         console.log('Received WebSocket message:', messageData);
//         console.log('🔥 user_input_fields:', messageData.user_input_fields);
//         console.log('🔥 paused:', messageData.paused);
//         console.log('🔥 user_input_required:', messageData.user_input_required);
        
//         dispatch({ type: ADD_MESSAGE, payload: messageData });

//         // ✅ Handle HITL with proper field names and skip metadata field
//         if (messageData.paused && messageData.user_input_required && messageData.user_input_fields) {
//             console.log('🔥 Processing user input fields...');
            
//             // ✅ Skip the first field (index 0) and normalize the rest
//             const actualFields = messageData.user_input_fields.slice(1); // Skip metadata field
            
//             const normalizedFields = actualFields.map(field => {
//                 console.log('🔍 Processing field:', field);
//                 return {
//                     fieldName: field.field_name,
//                     fieldDescription: field.field_description || field.field_name, // fallback
//                     fieldType: field.field_type,
//                     required: field.required || false,
//                     defaultValue: field.default_value || '',
//                     options: field.options || [],
//                     minLength: field.min_length,
//                     maxLength: field.max_length,
//                     minValue: field.min_value,
//                     maxValue: field.max_value,
//                     pattern: field.pattern,
//                     placeholder: field.placeholder || '',
//                 };
//             });
            
//             console.log('🔥 Normalized fields:', normalizedFields);
//             dispatch({ type: SET_USER_INPUT_FIELDS, payload: normalizedFields });
//             dispatch({ type: SET_WAITING_FOR_INPUT, payload: true });
//         } else if (messageData.needsConfirmation) {
//             console.log('Agent requesting confirmation');
//         }
//     });
// };



export const subscribeToSession = (sessionId) => async (dispatch) => {
    if (!stompClient) {
        console.error('STOMP client not initialized');
        return;
    }

    if (subscribedSessionId === sessionId) return;
    // Wait until connected
    if (!stompClient.connected) {
        console.log('Waiting for WebSocket connection to subscribe...');
        await new Promise((resolve) => {
            const checkConnected = () => {
                if (stompClient.connected) resolve();
                else setTimeout(checkConnected, 50);
            };
            checkConnected();
        });
    }

    if (currentSubscription) {
        currentSubscription.unsubscribe();
        currentSubscription = null;
        subscribedSessionId = null;
    }

    currentSubscription = stompClient.subscribe(`/user/queue/session/${sessionId}`, (message) => {
        const messageData = JSON.parse(message.body);
        console.log('Received WebSocket message:', messageData);
        console.log('🔥 user_input_fields:', messageData.user_input_fields);
        console.log('🔥 paused:', messageData.paused);
        console.log('🔥 user_input_required:', messageData.user_input_required);

        dispatch({ type: ADD_MESSAGE, payload: messageData });

        // ✅ Handle HITL with proper field names and skip metadata field
        if (messageData.paused && messageData.user_input_required && messageData.user_input_fields) {
            console.log('🔥 Processing user input fields...');
            const actualFields = messageData.user_input_fields.slice(1); // Skip metadata
            const normalizedFields = actualFields.map(field => ({
                fieldName: field.field_name,
                fieldDescription: field.field_description || field.field_name,
                fieldType: field.field_type,
                required: field.required || false,
                defaultValue: field.default_value || '',
                options: field.options || [],
                minLength: field.min_length,
                maxLength: field.max_length,
                minValue: field.min_value,
                maxValue: field.max_value,
                pattern: field.pattern,
                placeholder: field.placeholder || '',
            }));
            console.log('🔥 Normalized fields:', normalizedFields);
            dispatch({ type: SET_USER_INPUT_FIELDS, payload: normalizedFields });
            dispatch({ type: SET_WAITING_FOR_INPUT, payload: true });
        } else if (messageData.needsConfirmation) {
            console.log('Agent requesting confirmation');
        }
    });
    subscribedSessionId = sessionId;
};





export const disconnectWebSocket = () => (dispatch) => {
    if (stompClient) {
        stompClient.deactivate();
        stompClient = null;
        currentSubscription = null;
        subscribedSessionId = null;
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
        // // Add user message first (for immediate UI feedback)
        // const userMessage = {
        //     id: Date.now() + '_user',
        //     type: 'user',
        //     content: messageData.content,
        //     timestamp: new Date().toISOString(),
        //     sessionId: session.sessionId,
        //     role: 'user'
        // };
        // dispatch({ type: ADD_MESSAGE, payload: userMessage });

        // Construct MessageRequest exactly as your backend expects
        const backendMessage = {
            content: messageData.content,
            sessionId: session.sessionId,
            tools: messageData.tools || [], // List<String>
            userId: auth.user?.id, // Long
            role: 'user', // String
            paused: false, // boolean
            needsConfirmation: false // boolean
        };

        console.log('Sending message:', backendMessage);

        // Send via WebSocket to your backend endpoint
        stompClient.publish({
            destination: `/app/chat/${session.sessionId}`,
            body: JSON.stringify(backendMessage)
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

    // ✅ Validate runId
    if (!runId || runId === null || runId === undefined) {
        console.error('Continue Agent Error: Invalid runId:', runId);
        dispatch({ type: CONTINUE_AGENT_FAILURE, payload: 'Invalid run ID. Please try again.' });
        return;
    }

    // Construct ContinueRequest payload according to your backend
    const payload = {
        runId: runId.toString(), // ✅ Ensure it's a string
        sessionId: session.sessionId,
        userId: auth.user?.id,
        userInputs: userInputs // This matches your ContinueRequest structure
    };

    console.log('🔍 Continue agent payload:', payload); // Debug log

    try {
        // Send via WebSocket
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
        // REST API call to load historical messages
        const response = await axios.get(`${baseURL}/api/message/history/${sessionId}`, {
            headers: { Authorization: `Bearer ${auth.token}` }
        });

        const messages = response.data;
        console.log('Loaded messages from backend:', messages);

        // Transform backend Message entities to frontend message format
        const transformedMessages = messages.map(backendMessage => ({
            id: backendMessage.id, // Long from backend
            type: backendMessage.role, // 'user', 'assistant', 'tool'
            content: backendMessage.content,
            tools: backendMessage.tools || [], // List<String> from backend
            timestamp: backendMessage.timestamp, // LocalDateTime from backend
            sessionId: sessionId,
            role: backendMessage.role,
            // Add any additional frontend-specific fields if needed
            userId: backendMessage.user?.id
        }));

        dispatch({ type: LOAD_MESSAGES_SUCCESS, payload: transformedMessages });
        return { success: true, messages: transformedMessages };
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
