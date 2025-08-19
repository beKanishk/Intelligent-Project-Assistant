import {
    SEND_MESSAGE_REQUEST, SEND_MESSAGE_SUCCESS, SEND_MESSAGE_FAILURE,
    CONTINUE_AGENT_REQUEST, CONTINUE_AGENT_SUCCESS, CONTINUE_AGENT_FAILURE,
    ADD_MESSAGE, CLEAR_MESSAGES,
    LOAD_MESSAGES_REQUEST, LOAD_MESSAGES_SUCCESS, LOAD_MESSAGES_FAILURE,
    SET_USER_INPUT_FIELDS, CLEAR_USER_INPUT_FIELDS, SET_WAITING_FOR_INPUT,
    WEBSOCKET_CONNECTED, WEBSOCKET_DISCONNECTED, WEBSOCKET_ERROR
} from "./ActionType";

const initialState = {
    messages: [],
    loading: false,
    continueLoading: false,
    messagesLoading: false,
    error: null,
    userInputFields: [],
    waitingForInput: false,
    websocketConnected: false
};

const messageReducer = (state = initialState, action) => {
    switch (action.type) {
        case SEND_MESSAGE_REQUEST:
            return { ...state, loading: true, error: null };

        case CONTINUE_AGENT_REQUEST:
            return { ...state, continueLoading: true, error: null };

        case LOAD_MESSAGES_REQUEST:
            return { ...state, messagesLoading: true, error: null };

        case SEND_MESSAGE_SUCCESS:
        case CONTINUE_AGENT_SUCCESS:
            return {
                ...state,
                loading: false,
                continueLoading: false,
                error: null
            };

        case ADD_MESSAGE:
            return {
                ...state,
                messages: [...state.messages, action.payload]
            };

        case LOAD_MESSAGES_SUCCESS:
            return {
                ...state,
                messagesLoading: false,
                messages: action.payload,
                error: null
            };

        case SET_USER_INPUT_FIELDS:
            return {
                ...state,
                userInputFields: action.payload
            };

        case CLEAR_USER_INPUT_FIELDS:
            return {
                ...state,
                userInputFields: []
            };

        case SET_WAITING_FOR_INPUT:
            return {
                ...state,
                waitingForInput: action.payload
            };

        case CLEAR_MESSAGES:
            return {
                ...state,
                messages: []
            };

        case WEBSOCKET_CONNECTED:
            return {
                ...state,
                websocketConnected: true
            };

        case WEBSOCKET_DISCONNECTED:
            return {
                ...state,
                websocketConnected: false
            };

        case SEND_MESSAGE_FAILURE:
        case CONTINUE_AGENT_FAILURE:
        case LOAD_MESSAGES_FAILURE:
        case WEBSOCKET_ERROR:
            return {
                ...state,
                loading: false,
                continueLoading: false,
                messagesLoading: false,
                error: action.payload
            };

        default:
            return state;
    }
};

export default messageReducer;
