import {
    REGISTER_REQUEST, REGISTER_SUCCESS, REGISTER_FAILURE,
    LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_FAILURE,
    LOGOUT,
    GET_USER_REQUEST, GET_USER_SUCCESS, GET_USER_FAILURE
} from "./ActionType";

const initialState = {
    user: null,
    loading: false,
    loginLoading: false,
    error: null,
    token: localStorage.getItem("token") || null,
    isAuthenticated: !!localStorage.getItem("token")
};

const authReducer = (state = initialState, action) => {
    switch (action.type) {
        case REGISTER_REQUEST:
        case GET_USER_REQUEST:
            return { ...state, loading: true, error: null };

        case LOGIN_REQUEST:
            return { ...state, loginLoading: true, error: null };

        case REGISTER_SUCCESS:
            return { 
                ...state, 
                loading: false, 
                error: null, 
                token: action.payload,
                isAuthenticated: true 
            };

        case LOGIN_SUCCESS:
            return { 
                ...state, 
                loginLoading: false, 
                error: null, 
                token: action.payload,
                isAuthenticated: true 
            };

        case GET_USER_SUCCESS:
            return { 
                ...state, 
                loading: false, 
                user: action.payload, 
                error: null,
                isAuthenticated: true
            };

        case REGISTER_FAILURE:
        case GET_USER_FAILURE:
            return { ...state, loading: false, error: action.payload };

        case LOGIN_FAILURE:
            return { ...state, loginLoading: false, error: action.payload, isAuthenticated: false, token: null };

        case LOGOUT:
            return {
                ...initialState,
                token: null,
                isAuthenticated: false
            };

        default:
            return state;
    }
};

export default authReducer;
