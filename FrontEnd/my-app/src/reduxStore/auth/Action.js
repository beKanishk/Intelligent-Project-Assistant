import axios from "axios";
import {
    REGISTER_REQUEST, REGISTER_SUCCESS, REGISTER_FAILURE,
    LOGIN_REQUEST, LOGIN_SUCCESS, LOGIN_FAILURE,
    LOGOUT,
    GET_USER_REQUEST, GET_USER_SUCCESS, GET_USER_FAILURE
} from "./ActionType";

const baseURL = import.meta.env.VITE_BACKEND_URL;

export const register = (userData) => async (dispatch) => {
    dispatch({ type: REGISTER_REQUEST });

    try {
        // const response = await axios.post(`${baseURL}/api/users/register`, userData, {
        //     headers: { "Content-Type": "application/json" }
        // });

        console.log("User data: ", userData);
        
        const response = await axios.post(`${baseURL}/auth/register`, userData, {
            headers: { "Content-Type": "application/json" }
        });

        const user = response.data;
        dispatch({ type: REGISTER_SUCCESS, payload: user.token });
        localStorage.setItem("token", user.token);
        
        dispatch(getUser(user.token));
        return { success: true };
    } catch (error) {
        console.error("Registration Error:", error.response?.data || error.message);
        dispatch({ type: REGISTER_FAILURE, payload: error.response?.data?.message || error.message });
        return { success: false };
    }
};

export const login = (userData) => async (dispatch) => {
    dispatch({ type: LOGIN_REQUEST });

    try {
        console.log('Making login API call...');
        // const response = await axios.post(`${baseURL}/api/users/login`, userData.data);
        const response = await axios.post(`${baseURL}/auth/token`, userData.data);
        let user = response.data;
        console.log('Login API response:', user);
        
        //This is for monolithic backend
        // if (user.token) {
        //     dispatch({ type: LOGIN_SUCCESS, payload: user.token });
        //     localStorage.setItem("token", user.token);

        //     // Try to get user profile, but don't block navigation if it fails
        //     try {
        //         await dispatch(getUser(user.token));
        //     } catch (userError) {
        //         console.warn("Failed to get user profile:", userError);
        //     }
        //     console.log('Navigating to /chat...');
        //     userData.navigate("/chat");
        //     return { success: true };
        // }
        if(user != null){
            user = user.trim();
            dispatch({ type: LOGIN_SUCCESS, payload: user });
            localStorage.setItem("token", user);

            // Try to get user profile, but don't block navigation if it fails
            try {
                await dispatch(getUser(user));
            } catch (userError) {
                console.warn("Failed to get user profile:", userError);
            }
            console.log('Navigating to /chat...');
            userData.navigate("/chat");
            return { success: true };
        }
        else {
            return { success: false, error: "No token received" };
        }
    } catch (error) {
        dispatch({ type: LOGIN_FAILURE, payload: error.message });
        console.log("Login error:", error);
        return { success: false };
    }
};

export const getUser = (token) => async (dispatch) => {
    dispatch({ type: GET_USER_REQUEST });

    try {
        // const response = await axios.get(`${baseURL}/api/users/me`, {
        //     headers: {
        //         Authorization: `Bearer ${token}`
        //     }
        // });

        const response = await axios.get(`${baseURL}/user/me`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const user = response.data;
        dispatch({ type: GET_USER_SUCCESS, payload: user });
        return { success: true };
    } catch (error) {
        console.error("Get User Error:", error.response?.data || error.message);
        dispatch({ type: GET_USER_FAILURE, payload: error.response?.data?.message || error.message });
        return { success: false };
    }
};

export const logout = () => (dispatch) => {
    localStorage.clear();
    dispatch({ type: LOGOUT });
};
