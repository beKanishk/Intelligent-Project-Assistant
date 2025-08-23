import {
    SEND_MESSAGE_REQUEST, SEND_MESSAGE_SUCCESS, SEND_MESSAGE_FAILURE,
    CONTINUE_AGENT_REQUEST, CONTINUE_AGENT_SUCCESS, CONTINUE_AGENT_FAILURE,
    ADD_MESSAGE, CLEAR_MESSAGES,
    LOAD_MESSAGES_REQUEST, LOAD_MESSAGES_SUCCESS, LOAD_MESSAGES_FAILURE,
    SET_USER_INPUT_FIELDS, CLEAR_USER_INPUT_FIELDS, SET_WAITING_FOR_INPUT,
    WEBSOCKET_CONNECTED, WEBSOCKET_DISCONNECTED, WEBSOCKET_ERROR,
    SET_AI_PROCESSING
} from "./ActionType";

const initialState = {
    messages: [],
    loading: false,
    continueLoading: false,
    messagesLoading: false,
    error: null,
    userInputFields: [],
    waitingForInput: false,
    websocketConnected: false,
    aiProcessing: false,
};

// Helper function to normalize message structure
const normalizeMessage = (message) => {
    return {
        id: message.id || Date.now() + '_' + Math.random(),
        type: message.type || message.role || 'user',
        role: message.role || message.type || 'user',
        content: message.content || '',
        tools: message.tools || message.toolUsed || [],
        timestamp: message.timestamp || new Date().toISOString(),
        sessionId: message.sessionId,
        userId: message.userId || message.user?.id,
        // ✅ Handle both snake_case and camelCase
        runId: message.runId || message.run_id || null,
        paused: message.paused || false,
        userInputRequired: message.userInputRequired || message.user_input_required || false,
        userInputFields: message.userInputFields || message.user_input_fields || [],
        needsConfirmation: message.needsConfirmation || false,
        error: message.error || false
    };
};


// Helper function to transform backend Message entity to frontend format
const transformBackendMessage = (backendMessage) => {
    return {
        id: backendMessage.id,
        type: backendMessage.role, // 'user', 'assistant', 'tool'
        role: backendMessage.role,
        content: backendMessage.content,
        tools: backendMessage.tools || [],
        timestamp: backendMessage.timestamp,
        sessionId: backendMessage.session?.id,
        userId: backendMessage.user?.id,
        // Initialize WebSocket-specific fields
        paused: false,
        userInputRequired: false,
        userInputFields: [],
        needsConfirmation: false,
        error: false
    };
};

const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case SEND_MESSAGE_REQUEST:
            return { 
                ...state, 
                loading: true, 
                aiProcessing: true,
                error: null 
            };

        case CONTINUE_AGENT_REQUEST:
            return { 
                ...state, 
                continueLoading: true, 
                error: null 
            };
        
        case SET_AI_PROCESSING: // ✅ Add this case
            return {
                ...state,
                aiProcessing: action.payload
            };

        case LOAD_MESSAGES_REQUEST:
            return { 
                ...state, 
                messagesLoading: true, 
                error: null 
            };

        case SEND_MESSAGE_SUCCESS:
            return {
                ...state,
                loading: false,
                error: null
            };

        case CONTINUE_AGENT_SUCCESS:
            return {
                ...state,
                continueLoading: false,
                error: null
            };

        case ADD_MESSAGE:
            // Normalize the message structure and prevent duplicates
            const newMessage = normalizeMessage(action.payload);

            let loading = state.loading;
            
            if (newMessage.role === 'user') {
                // User message received back from server
                loading = false; // Stop send button loading
                // Keep aiProcessing true until AI responds
            }

            // Stop AI processing when assistant responds
            const aiProcessing = newMessage.role === 'assistant' || newMessage.type === 'assistant' 
                ? false 
                : state.aiProcessing;

            const isDuplicate = state.messages.some(msg => 
                msg.id === newMessage.id || 
                (msg.content === newMessage.content && 
                 msg.timestamp === newMessage.timestamp && 
                 msg.role === newMessage.role)
            );

            if (isDuplicate) {
                return state;
            }

            return {
                ...state,
                aiProcessing,
                loading,
                messages: [...state.messages, newMessage]
            };

        case LOAD_MESSAGES_SUCCESS:
            // Transform backend Message entities to frontend format
            const transformedMessages = action.payload.map(transformBackendMessage);
            
            return {
                ...state,
                messagesLoading: false,
                messages: transformedMessages,
                error: null
            };

        case SET_USER_INPUT_FIELDS:
            return {
                ...state,
                userInputFields: action.payload,
                waitingForInput: true
            };

        case CLEAR_USER_INPUT_FIELDS:
            return {
                ...state,
                userInputFields: [],
                waitingForInput: false
            };

        case SET_WAITING_FOR_INPUT:
            return {
                ...state,
                waitingForInput: action.payload
            };

        case CLEAR_MESSAGES:
            return {
                ...state,
                messages: [],
                userInputFields: [],
                waitingForInput: false,
                aiProcessing: false,
                error: null
            };

        case WEBSOCKET_CONNECTED:
            return {
                ...state,
                websocketConnected: true,
                error: null
            };

        case WEBSOCKET_DISCONNECTED:
            return {
                ...state,
                websocketConnected: false
            };

        case SEND_MESSAGE_FAILURE:
            return {
                ...state,
                loading: false,
                aiProcessing: false,
                error: action.payload
            };

        case CONTINUE_AGENT_FAILURE:
            return {
                ...state,
                continueLoading: false,
                error: action.payload
            };

        case LOAD_MESSAGES_FAILURE:
            return {
                ...state,
                messagesLoading: false,
                error: action.payload
            };

        case WEBSOCKET_ERROR:
            return {
                ...state,
                websocketConnected: false,
                error: action.payload
            };

        default:
            return state;
    }
};

export default messageReducer;
