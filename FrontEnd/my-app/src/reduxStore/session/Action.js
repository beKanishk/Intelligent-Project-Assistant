import axios from "axios";
import {
    CREATE_SESSION_REQUEST, CREATE_SESSION_SUCCESS, CREATE_SESSION_FAILURE,
    SET_SESSION_ID, CLEAR_SESSION,
    LOAD_SESSION_HISTORY_REQUEST, LOAD_SESSION_HISTORY_SUCCESS, LOAD_SESSION_HISTORY_FAILURE,
    SET_SESSION_DATA, UPDATE_SESSION_DATA
} from "./ActionType";

const baseURL = import.meta.env.VITE_BACKEND_URL;

export const createSession = () => async (dispatch, getState) => {
    dispatch({ type: CREATE_SESSION_REQUEST });

    const { auth } = getState();
    const token = auth.token;
    console.log("Creating session with token:", token);
    
    try {
        const response = await axios.post(`${baseURL}/api/sessions`, null, 
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            }
        );

        const sessionId = response.data;
        console.log("Session created:", sessionId);

        localStorage.setItem("sessionId", sessionId);
        dispatch({ type: CREATE_SESSION_SUCCESS, payload: sessionId });
        return { success: true, sessionId };
    } catch (error) {
        console.error("Create Session Error:", error.response?.data || error.message);
        dispatch({ type: CREATE_SESSION_FAILURE, payload: error.response?.data?.message || error.message });
        return { success: false, error: error.response?.data?.message || error.message };
    }
};

export const initializeSession = () => async (dispatch, getState) => {
    const { auth } = getState();
    
    if (!auth.isAuthenticated) {
        return;
    }

    const existingSessionId = localStorage.getItem("sessionId");
    
    if (existingSessionId) {
        // Simply set the existing session ID without verification
        console.log("Using existing session:", existingSessionId);
        dispatch({ type: SET_SESSION_ID, payload: existingSessionId });
        return;
    }

    // Create new session if no existing session
    console.log("Creating new session...");
    dispatch(createSession());
};

export const loadSessionHistory = (userId) => async (dispatch, getState) => {
    dispatch({ type: LOAD_SESSION_HISTORY_REQUEST });

    const { auth } = getState();

    try {
        const response = await axios.get(`${baseURL}/api/sessions/user/${userId}`, {
            headers: { Authorization: `Bearer ${auth.token}` }
        });

        const history = response.data;
        console.log("User session history loaded:", history);
        
        dispatch({ type: LOAD_SESSION_HISTORY_SUCCESS, payload: history });
        return { success: true, history };
    } catch (error) {
        console.error("Load Session History Error:", error.response?.data || error.message);
        dispatch({ type: LOAD_SESSION_HISTORY_FAILURE, payload: error.response?.data?.message || error.message });
        return { success: false, error: error.response?.data?.message || error.message };
    }
};


export const clearSession = () => (dispatch) => {
    localStorage.removeItem("sessionId");
    dispatch({ type: CLEAR_SESSION });
};

export const setSessionData = (data) => (dispatch) => {
    dispatch({ type: SET_SESSION_DATA, payload: data });
};

export const updateSessionData = (data) => (dispatch) => {
    dispatch({ type: UPDATE_SESSION_DATA, payload: data });
};
