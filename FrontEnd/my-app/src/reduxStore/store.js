// store.js
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./auth/Reducer";
import sessionReducer from "./session/Reducer";
import messageReducer from "./message/Reducer";

const store = configureStore({
  reducer: {
    auth: authReducer,
    session: sessionReducer,
    message: messageReducer,
  },
});

export default store;
