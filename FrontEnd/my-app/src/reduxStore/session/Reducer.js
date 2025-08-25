// import {
//     CREATE_SESSION_REQUEST, CREATE_SESSION_SUCCESS, CREATE_SESSION_FAILURE,
//     SET_SESSION_ID, CLEAR_SESSION,
//     LOAD_SESSION_HISTORY_REQUEST, LOAD_SESSION_HISTORY_SUCCESS, LOAD_SESSION_HISTORY_FAILURE,
//     SET_SESSION_DATA, UPDATE_SESSION_DATA,
//     DELETE_SESSION_FAILURE,
//     DELETE_SESSION_SUCCESS,
//     DELETE_SESSION_REQUEST
// } from "./ActionType";

// const initialState = {
//     // Current session
//     sessionId: localStorage.getItem("sessionId") || null,
//     currentSessionId: localStorage.getItem("sessionId") || null, // ✅ Add for consistency with delete logic
//     sessionData: {},
    
//     // Sessions list (for sidebar)
//     sessions: [], // ✅ Add this for managing multiple sessions
    
//     // History and loading states
//     history: [],
//     loading: false,
//     historyLoading: false,
//     deleteLoading: false,
    
//     // Error states
//     error: null,
//     deleteError: null,
    
//     // Status
//     isActive: false,
// };

// const sessionReducer = (state = initialState, action) => {
//     switch (action.type) {
//         case CREATE_SESSION_REQUEST:
//             return { 
//                 ...state, 
//                 loading: true, 
//                 error: null 
//             };

//         case LOAD_SESSION_HISTORY_REQUEST:
//             return { 
//                 ...state, 
//                 historyLoading: true, 
//                 error: null 
//             };

//         case CREATE_SESSION_SUCCESS:
//             const newSession = action.payload;
//             // Store sessionId in localStorage
//             localStorage.setItem("sessionId", newSession.id);
            
//             return {
//                 ...state,
//                 loading: false,
//                 sessionId: newSession.id,
//                 currentSessionId: newSession.id, // ✅ Keep both for consistency
//                 sessionData: newSession,
//                 isActive: true,
//                 error: null,
//                 // ✅ Add new session to sessions array if not exists
//                 sessions: state.sessions.some(s => s.id === newSession.id) 
//                     ? state.sessions 
//                     : [...state.sessions, newSession]
//             };

//         case DELETE_SESSION_REQUEST:
//             return {
//                 ...state,
//                 deleteLoading: true,
//                 deleteError: null
//             };
            
//         case DELETE_SESSION_SUCCESS:
//             const deletedSessionId = action.payload;
            
//             // ✅ Remove from localStorage if it's the current session
//             if (state.currentSessionId === deletedSessionId) {
//                 localStorage.removeItem("sessionId");
//             }
            
//             return {
//                 ...state,
//                 deleteLoading: false,
//                 deleteError: null,
//                 // ✅ Remove from sessions array
//                 sessions: state.sessions.filter(session => session.id !== deletedSessionId),
//                 // ✅ Clear current session if it was deleted
//                 sessionId: state.sessionId === deletedSessionId ? null : state.sessionId,
//                 currentSessionId: state.currentSessionId === deletedSessionId ? null : state.currentSessionId,
//                 sessionData: state.sessionId === deletedSessionId ? {} : state.sessionData,
//                 isActive: state.sessionId === deletedSessionId ? false : state.isActive
//             };
            
//         case DELETE_SESSION_FAILURE:
//             return {
//                 ...state,
//                 deleteLoading: false,
//                 deleteError: action.payload
//             };

//         case SET_SESSION_ID:
//             // Store in localStorage
//             localStorage.setItem("sessionId", action.payload.id);
            
//             return {
//                 ...state,
//                 sessionId: action.payload.id,
//                 currentSessionId: action.payload.id, // ✅ Keep both in sync
//                 isActive: true
//             };

//         case LOAD_SESSION_HISTORY_SUCCESS:
//             return {
//                 ...state,
//                 historyLoading: false,
//                 history: action.payload,
//                 error: null
//             };

//         case SET_SESSION_DATA:
//             console.log('🔍 SET_SESSION_DATA - payload:', action.payload);
            
//             // Store sessionId in localStorage
//             if (action.payload.id) {
//                 localStorage.setItem("sessionId", action.payload.id);
//             }
            
//             return {
//                 ...state,
//                 sessionId: action.payload.id,
//                 currentSessionId: action.payload.id, // ✅ Keep both in sync
//                 sessionData: action.payload, // ✅ Replace entire sessionData, don't merge
//                 isActive: true,
//                 // ✅ Update sessions array if this session exists
//                 sessions: state.sessions.map(session => 
//                     session.id === action.payload.id 
//                         ? { ...session, ...action.payload }
//                         : session
//                 )
//             };

//         case UPDATE_SESSION_DATA:
//             const updatedSessionData = { ...state.sessionData, ...action.payload };
            
//             return {
//                 ...state,
//                 sessionData: updatedSessionData,
//                 // ✅ Update in sessions array too
//                 sessions: state.sessions.map(session => 
//                     session.id === state.sessionId 
//                         ? { ...session, ...action.payload }
//                         : session
//                 )
//             };

//         case CREATE_SESSION_FAILURE:
//         case LOAD_SESSION_HISTORY_FAILURE:
//             return {
//                 ...state,
//                 loading: false,
//                 historyLoading: false,
//                 error: action.payload
//             };

//         case CLEAR_SESSION:
//             // Remove from localStorage
//             localStorage.removeItem("sessionId");
            
//             return {
//                 ...initialState,
//                 sessionId: null,
//                 currentSessionId: null,
//                 sessions: state.sessions // ✅ Keep other sessions in sidebar
//             };

//         // ✅ Add action to load all sessions for sidebar
//         case 'LOAD_SESSIONS_SUCCESS':
//             return {
//                 ...state,
//                 sessions: action.payload,
//                 loading: false,
//                 error: null
//             };

//         // ✅ Add action to switch between sessions
//         case 'SWITCH_SESSION':
//             const selectedSession = state.sessions.find(s => s.id === action.payload);
//             if (selectedSession) {
//                 localStorage.setItem("sessionId", selectedSession.id);
//                 return {
//                     ...state,
//                     sessionId: selectedSession.id,
//                     currentSessionId: selectedSession.id,
//                     sessionData: selectedSession,
//                     isActive: true
//                 };
//             }
//             return state;

//         default:
//             return state;
//     }
// };

// export default sessionReducer;


import {
    CREATE_SESSION_REQUEST, CREATE_SESSION_SUCCESS, CREATE_SESSION_FAILURE,
    SET_SESSION_ID, CLEAR_SESSION,
    LOAD_SESSION_HISTORY_REQUEST, LOAD_SESSION_HISTORY_SUCCESS, LOAD_SESSION_HISTORY_FAILURE,
    SET_SESSION_DATA, UPDATE_SESSION_DATA,
    DELETE_SESSION_FAILURE,
    DELETE_SESSION_SUCCESS,
    DELETE_SESSION_REQUEST,
    // ✅ Import these missing action types
    LOAD_SESSIONS_REQUEST,
    LOAD_SESSIONS_SUCCESS,
    LOAD_SESSIONS_FAILURE,
    SWITCH_SESSION,
    RENAME_SESSION_REQUEST,
    RENAME_SESSION_SUCCESS,
    RENAME_SESSION_FAILURE
} from "./ActionType";

const initialState = {
    // Current session
    sessionId: localStorage.getItem("sessionId") || null,
    currentSessionId: localStorage.getItem("sessionId") || null,
    sessionData: {},
    
    // Sessions list (for sidebar)
    sessions: [],
    
    // History and loading states
    history: [],
    loading: false,
    historyLoading: false,
    deleteLoading: false,
    
    // Error states
    error: null,
    deleteError: null,
    
    // Status
    isActive: false,
};

const sessionReducer = (state = initialState, action) => {
    switch (action.type) {
        case CREATE_SESSION_REQUEST:
        case LOAD_SESSIONS_REQUEST: // ✅ Add this case
            return { 
                ...state, 
                loading: true, 
                error: null 
            };

        case LOAD_SESSION_HISTORY_REQUEST:
            return { 
                ...state, 
                historyLoading: true, 
                error: null 
            };

        case CREATE_SESSION_SUCCESS:
            const newSession = action.payload;
            localStorage.setItem("sessionId", newSession.id);
            
            return {
                ...state,
                loading: false,
                sessionId: newSession.id,
                currentSessionId: newSession.id,
                sessionData: newSession,
                isActive: true,
                error: null,
                sessions: state.sessions.some(s => s.id === newSession.id) 
                    ? state.sessions 
                    : [...state.sessions, newSession]
            };

        case DELETE_SESSION_REQUEST:
            return {
                ...state,
                deleteLoading: true,
                deleteError: null
            };
            
        case DELETE_SESSION_SUCCESS:
            const deletedSessionId = action.payload;
            
            console.log('🔍 REDUCER - Deleting session:', deletedSessionId);
            console.log('🔍 REDUCER - Sessions before delete:', state.sessions);
            
            // Remove from localStorage if it's the current session
            if (state.currentSessionId === deletedSessionId) {
                localStorage.removeItem("sessionId");
            }
            
            const filteredSessions = state.sessions.filter(session => session.id !== deletedSessionId);
            console.log('🔍 REDUCER - Sessions after delete:', filteredSessions);
            
            return {
                ...state,
                deleteLoading: false,
                deleteError: null,
                // ✅ Remove from sessions array
                sessions: filteredSessions,
                // ✅ Clear current session if it was deleted
                sessionId: state.sessionId === deletedSessionId ? null : state.sessionId,
                currentSessionId: state.currentSessionId === deletedSessionId ? null : state.currentSessionId,
                sessionData: state.sessionId === deletedSessionId ? {} : state.sessionData,
                isActive: state.sessionId === deletedSessionId ? false : state.isActive
            };
            
        case DELETE_SESSION_FAILURE:
            return {
                ...state,
                deleteLoading: false,
                deleteError: action.payload
            };

        case SET_SESSION_ID:
            localStorage.setItem("sessionId", action.payload.id);
            
            return {
                ...state,
                sessionId: action.payload.id,
                currentSessionId: action.payload.id,
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
            console.log('🔍 SET_SESSION_DATA - payload:', action.payload);
            
            if (action.payload.id) {
                localStorage.setItem("sessionId", action.payload.id);
            }
            
            return {
                ...state,
                sessionId: action.payload.id,
                currentSessionId: action.payload.id,
                sessionData: action.payload,
                isActive: true,
                sessions: state.sessions.map(session => 
                    session.id === action.payload.id 
                        ? { ...session, ...action.payload }
                        : session
                )
            };

        case UPDATE_SESSION_DATA:
            const updatedSessionData = { ...state.sessionData, ...action.payload };
            
            return {
                ...state,
                sessionData: updatedSessionData,
                sessions: state.sessions.map(session => 
                    session.id === state.sessionId 
                        ? { ...session, ...action.payload }
                        : session
                )
            };

        case CREATE_SESSION_FAILURE:
        case LOAD_SESSION_HISTORY_FAILURE:
        case LOAD_SESSIONS_FAILURE: // ✅ Add this case
            return {
                ...state,
                loading: false,
                historyLoading: false,
                error: action.payload
            };

        case CLEAR_SESSION:
            localStorage.removeItem("sessionId");
            
            return {
                ...initialState,
                sessionId: null,
                currentSessionId: null,
                sessions: state.sessions
            };

        // ✅ Use imported constants instead of hardcoded strings
        case LOAD_SESSIONS_SUCCESS:
            console.log('🔍 REDUCER - LOAD_SESSIONS_SUCCESS:', action.payload);
            return {
                ...state,
                sessions: action.payload,
                loading: false,
                error: null
            };

        case SWITCH_SESSION:
            const selectedSession = state.sessions.find(s => s.id === action.payload);
            console.log('🔍 REDUCER - SWITCH_SESSION:', action.payload, selectedSession);
            
            if (selectedSession) {
                localStorage.setItem("sessionId", selectedSession.id);
                return {
                    ...state,
                    sessionId: selectedSession.id,
                    currentSessionId: selectedSession.id,
                    sessionData: selectedSession,
                    isActive: true
                };
            }
            return state;

        // ✅ Add rename session cases
        case RENAME_SESSION_REQUEST:
            return {
                ...state,
                loading: true,
                error: null
            };

        case RENAME_SESSION_SUCCESS:
            const { sessionId: renameSessionId, name: newName } = action.payload;
            return {
                ...state,
                loading: false,
                sessions: state.sessions.map(session => 
                    session.id === renameSessionId 
                        ? { ...session, name: newName }
                        : session
                ),
                sessionData: state.sessionId === renameSessionId 
                    ? { ...state.sessionData, name: newName }
                    : state.sessionData
            };

        case RENAME_SESSION_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };

        default:
            return state;
    }
};

export default sessionReducer;
