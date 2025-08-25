import axios from "axios";
import {
    CREATE_SESSION_REQUEST, CREATE_SESSION_SUCCESS, CREATE_SESSION_FAILURE,
    SET_SESSION_ID, CLEAR_SESSION,
    LOAD_SESSION_HISTORY_REQUEST, LOAD_SESSION_HISTORY_SUCCESS, LOAD_SESSION_HISTORY_FAILURE,
    SET_SESSION_DATA, UPDATE_SESSION_DATA,
    DELETE_SESSION_REQUEST,
    DELETE_SESSION_SUCCESS,
    DELETE_SESSION_FAILURE,
    // ✅ Add new action types
    LOAD_SESSIONS_REQUEST,
    LOAD_SESSIONS_SUCCESS,
    LOAD_SESSIONS_FAILURE,
    SWITCH_SESSION,
    RENAME_SESSION_REQUEST,
    RENAME_SESSION_SUCCESS,
    RENAME_SESSION_FAILURE
} from "./ActionType";

const baseURL = import.meta.env.VITE_BACKEND_URL;

// ✅ Create session with improved response handling
export const createSession = () => async (dispatch, getState) => {
    dispatch({ type: CREATE_SESSION_REQUEST });

    const { auth } = getState();
    const token = auth.token;
    console.log("Creating session with token:", token);
    
    try {
        const response = await axios.post(`${baseURL}/api/sessions`, null, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        });

        // ✅ Handle both string sessionId and object response
        const sessionData = typeof response.data === 'string' 
            ? { id: response.data, name: `Chat ${response.data.slice(-4)}`, createdAt: new Date().toISOString() }
            : response.data;

        console.log("Session created:", sessionData);

        localStorage.setItem("sessionId", sessionData.id);
        dispatch({ type: CREATE_SESSION_SUCCESS, payload: sessionData });
        
        // ✅ Also refresh sessions list
        dispatch(loadSessions());
        
        return { success: true, sessionData };
    } catch (error) {
        console.error("Create Session Error:", error.response?.data || error.message);
        dispatch({ type: CREATE_SESSION_FAILURE, payload: error.response?.data?.message || error.message });
        return { success: false, error: error.response?.data?.message || error.message };
    }
};

// ✅ Load all sessions for sidebar
export const loadSessions = () => async (dispatch, getState) => {
    dispatch({ type: LOAD_SESSIONS_REQUEST });
    
    try {
        const { auth } = getState();
        const token = auth.token;
        const userId = auth.user.id;

        const response = await axios.get(`${baseURL}/api/sessions/user/${userId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        });
        
        const sessions = response.data || [];
        console.log("Sessions loaded:", sessions);
        
        dispatch({ 
            type: LOAD_SESSIONS_SUCCESS, 
            payload: sessions 
        });
        
        return { success: true, sessions };
    } catch (error) {
        console.error('Load sessions error:', error);
        const errorMessage = error.response?.data?.message || 'Failed to load sessions';
        dispatch({ 
            type: LOAD_SESSIONS_FAILURE, 
            payload: errorMessage 
        });
        return { success: false, error: errorMessage };
    }
};

// ✅ Switch to a different session
export const switchSession = (sessionId) => async (dispatch, getState) => {
    const { session } = getState();
    const targetSession = session.sessions.find(s => s.id === sessionId);
    
    if (targetSession) {
        localStorage.setItem("sessionId", sessionId);
        dispatch({ 
            type: SWITCH_SESSION, 
            payload: sessionId 
        });
        
        // ✅ Load messages for the switched session
        // You might want to dispatch loadMessages here
        
        return { success: true };
    }
    
    return { success: false, error: 'Session not found' };
};

// ✅ Enhanced initialize session
export const initializeSession = () => async (dispatch, getState) => {
    const { auth } = getState();
    
    if (!auth.isAuthenticated) {
        return;
    }

    // Load all sessions first
    await dispatch(loadSessions());

    const existingSessionId = localStorage.getItem("sessionId");
    
    if (existingSessionId) {
        // Verify session exists in loaded sessions
        const { session } = getState();
        const sessionExists = session.sessions.some(s => s.id === existingSessionId);
        
        if (sessionExists) {
            console.log("Using existing session:", existingSessionId);
            dispatch({ type: SET_SESSION_ID, payload: { id: existingSessionId } });
            return;
        } else {
            // Session doesn't exist, clear localStorage
            localStorage.removeItem("sessionId");
        }
    }

    // Create new session if no existing session
    console.log("Creating new session...");
    dispatch(createSession());
};

// ✅ Enhanced delete session
export const deleteSession = (sessionId) => async (dispatch, getState) => {
    console.log('🔍 deleteSession ACTION CALLED with sessionId:', sessionId);
    console.log('🔍 dispatch function:', typeof dispatch);
    console.log('🔍 getState function:', typeof getState);
    dispatch({ type: DELETE_SESSION_REQUEST });
    
    try {
        const { auth } = getState();
        const token = auth.token;
        
        await axios.delete(`${baseURL}/api/sessions/${sessionId}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        console.log('🔍 DELETE API call successful');

        dispatch({ 
            type: DELETE_SESSION_SUCCESS, 
            payload: sessionId 
        });
        
        // // ✅ If deleted session was current, create a new one
        // const { session } = getState();
        // if (session.currentSessionId === sessionId) {
        //     setTimeout(() => {
        //         dispatch(createSession());
        //     }, 500);
        // }
        
        return { success: true };
    } catch (error) {
        console.error('Delete session error:', error);
        const errorMessage = error.response?.data?.message || 'Failed to delete session';
        dispatch({ 
            type: DELETE_SESSION_FAILURE, 
            payload: errorMessage 
        });
        return { success: false, error: errorMessage };
    }
};

// ✅ Rename session (bonus feature)
export const renameSession = (sessionId, newName) => async (dispatch, getState) => {
    dispatch({ type: RENAME_SESSION_REQUEST });
    
    try {
        const { auth } = getState();
        const token = auth.token;
        
        const response = await axios.put(`${baseURL}/api/sessions/${sessionId}`, 
            { name: newName },
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        
        dispatch({ 
            type: RENAME_SESSION_SUCCESS, 
            payload: { sessionId, name: newName }
        });
        
        return { success: true };
    } catch (error) {
        console.error('Rename session error:', error);
        const errorMessage = error.response?.data?.message || 'Failed to rename session';
        dispatch({ 
            type: RENAME_SESSION_FAILURE, 
            payload: errorMessage 
        });
        return { success: false, error: errorMessage };
    }
};

// ✅ Enhanced load session history
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

// ✅ Enhanced clear session
export const clearSession = () => (dispatch) => {
    localStorage.removeItem("sessionId");
    dispatch({ type: CLEAR_SESSION });
};

// ✅ Enhanced set session data
export const setSessionData = (sessionData) => (dispatch) => {
    console.log('🔍 setSessionData action - session:', sessionData);
    
    // Update localStorage
    if (sessionData.id) {
        localStorage.setItem("sessionId", sessionData.id);
    }
    
    dispatch({
        type: SET_SESSION_DATA,
        payload: sessionData
    });
};

// ✅ Enhanced update session data
export const updateSessionData = (data) => (dispatch) => {
    console.log('Updating session data:', data);
    dispatch({ type: UPDATE_SESSION_DATA, payload: data });
};

// ✅ Get session by ID
export const getSessionById = (sessionId) => async (dispatch, getState) => {
    try {
        const { auth } = getState();
        const token = auth.token;
        
        const response = await axios.get(`${baseURL}/api/sessions/${sessionId}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        });
        
        const sessionData = response.data;
        dispatch(setSessionData(sessionData));
        
        return { success: true, sessionData };
    } catch (error) {
        console.error('Get session error:', error);
        return { success: false, error: error.response?.data?.message || error.message };
    }
};
