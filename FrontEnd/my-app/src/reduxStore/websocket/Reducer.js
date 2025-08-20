import * as types from './ActionType';

const initialState = {
  connected: false,
  messages: [],
  error: null,
  connecting: false
};

const websocketReducer = (state = initialState, action) => {
  switch (action.type) {
    case types.WEBSOCKET_CONNECT:
      return {
        ...state,
        connecting: true,
        error: null
      };
    
    case types.WEBSOCKET_CONNECTED:
      return {
        ...state,
        connected: true,
        connecting: false,
        error: null
      };
    
    case types.WEBSOCKET_MESSAGE_RECEIVED:
      return {
        ...state,
        messages: [...state.messages, action.payload]
      };
    
    case types.WEBSOCKET_MESSAGE_SENT:
      return {
        ...state,
        messages: [...state.messages, { ...action.payload, type: 'user' }]
      };
    
    case types.WEBSOCKET_DISCONNECT:
      return {
        ...state,
        connected: false,
        connecting: false
      };
    
    case types.WEBSOCKET_ERROR:
      return {
        ...state,
        error: action.payload,
        connecting: false
      };
    
    default:
      return state;
  }
};

export default websocketReducer;
