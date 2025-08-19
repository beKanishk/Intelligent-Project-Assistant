import {
    CREATE_SESSION_REQUEST, CREATE_SESSION_SUCCESS, CREATE_SESSION_FAILURE,
    SET_SESSION_ID, CLEAR_SESSION,
    LOAD_SESSION_HISTORY_REQUEST, LOAD_SESSION_HISTORY_SUCCESS, LOAD_SESSION_HISTORY_FAILURE,
    SET_SESSION_DATA, UPDATE_SESSION_DATA
} from "./ActionType";

const initialState = {
    sessionId: localStorage.getItem("sessionId") || null,
    sessionData: {},
    history: [],
    loading: false,
    historyLoading: false,
    error: null,
    isActive: false
};

const sessionReducer = (state = initialState, action) => {
    switch (action.type) {
        case CREATE_SESSION_REQUEST:
            return { ...state, loading: true, error: null };

        case LOAD_SESSION_HISTORY_REQUEST:
            return { ...state, historyLoading: true, error: null };

        case CREATE_SESSION_SUCCESS:
            return {
                ...state,
                loading: false,
                sessionId: action.payload.sessionId,
                sessionData: action.payload,
                isActive: true,
                error: null
            };

        case SET_SESSION_ID:
            return {
                ...state,
                sessionId: action.payload,
                isActive: true
            };

        case LOAD_SESSION_HISTORY_SUCCESS:
            return {
                ...state,
                historyLoading: false,
                history: action.payload,
                error: null
            };

        case SET_SESSION_DATA:
            return {
                ...state,
                sessionData: { ...state.sessionData, ...action.payload }
            };

        case UPDATE_SESSION_DATA:
            return {
                ...state,
                sessionData: { ...state.sessionData, ...action.payload }
            };

        case CREATE_SESSION_FAILURE:
        case LOAD_SESSION_HISTORY_FAILURE:
            return {
                ...state,
                loading: false,
                historyLoading: false,
                error: action.payload
            };

        case CLEAR_SESSION:
            return {
                ...initialState,
                sessionId: null
            };

        default:
            return state;
    }
};

export default sessionReducer;
