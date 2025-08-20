// store.js
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./auth/Reducer";
import sessionReducer from "./session/Reducer";
import messageReducer from "./message/Reducer";
import websocketReducer from "./websocket/Reducer";

const store = configureStore({
  reducer: {
    auth: authReducer,
    session: sessionReducer,
    message: messageReducer,
    websocket: websocketReducer,
  },
});

export default store;
