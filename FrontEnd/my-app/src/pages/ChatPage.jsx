import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Layout } from 'antd';
import ChatSidebar from '@/components/sidebar/ChatSidebar';
import ChatHeader from '@/components/layout/ChatHeader';
import ChatWindow from '@/components/chat/ChatWindow';
import QueryForm from '@/components/query/QueryForm';
import DynamicInputForm from '@/components/chat/DynamicInputForm';
import { connectWebSocket, subscribeToSession, loadMessages } from '@/reduxStore/message/Action';
import { initializeSession } from '@/reduxStore/session/Action';

const { Content } = Layout;

const ChatPage = () => {
  const dispatch = useDispatch();
  const [siderCollapsed, setSiderCollapsed] = useState(false);
  
  const { sessionId } = useSelector(state => state.session);
  const { waitingForInput, userInputFields } = useSelector(state => state.message);

  useEffect(() => {
    // Initialize session when component mounts
    if (!sessionId) {
      dispatch(initializeSession());
    }

    // Initialize WebSocket connection
    dispatch(connectWebSocket());
  }, [dispatch, sessionId]);

  useEffect(() => {
    if (sessionId) {
      // Load chat history for this session
      dispatch(loadMessages(sessionId));
      
      // Subscribe to WebSocket for real-time updates
      dispatch(subscribeToSession(sessionId));
    }
  }, [dispatch, sessionId]);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <ChatSidebar collapsed={siderCollapsed} onCollapse={setSiderCollapsed} />
      
      <Layout>
        <ChatHeader onMenuClick={() => setSiderCollapsed(!siderCollapsed)} />
        
        <Content style={{ 
          display: 'flex', 
          flexDirection: 'column',
          height: 'calc(100vh - 64px)'
        }}>
          <div style={{ flex: 1, overflow: 'hidden' }}>
            <ChatWindow />
          </div>
          
          <div style={{ 
            borderTop: '1px solid #f0f0f0',
            backgroundColor: '#fff',
            padding: '16px'
          }}>
            {waitingForInput && userInputFields.length > 0 ? (
              <DynamicInputForm />
            ) : (
              <QueryForm />
            )}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
};

export default ChatPage;
