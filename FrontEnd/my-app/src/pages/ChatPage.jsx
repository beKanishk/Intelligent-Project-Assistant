import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Layout } from 'antd';
import ChatSidebar from '@/components/sidebar/ChatSidebar';
import ChatHeader from '@/components/layout/ChatHeader';
import ChatWindow from '@/components/chat/ChatWindow';
import QueryForm from '@/components/query/QueryForm';
import DynamicInputForm from '@/components/chat/DynamicInputForm';
import { connectWebSocket, loadMessages } from '@/reduxStore/message/Action';
import { initializeSession } from '@/reduxStore/session/Action';

const { Content } = Layout;

const ChatPage = () => {
  const dispatch = useDispatch();
  const [siderCollapsed, setSiderCollapsed] = useState(false);
  
  const { sessionId } = useSelector(state => state.session);
  const { waitingForInput, userInputFields } = useSelector(state => state.message);
  const { user } = useSelector(state => state.auth);

  // ✅ Initialize session on component mount
  useEffect(() => {
    if (!sessionId) {
      dispatch(initializeSession());
    }
  }, [dispatch, sessionId]);

 useEffect(() => {
  dispatch(connectWebSocket());
}, [dispatch]);

  // ✅ Load messages when sessionId changes
  useEffect(() => {
    if (sessionId) {
      console.log('🔍 ChatPage - Loading messages for session:', sessionId);
      dispatch(loadMessages(sessionId));
    }
  }, [dispatch, sessionId]);

  const handleSiderCollapse = (collapsed) => {
    console.log('🔍 ChatPage - handleSiderCollapse called with:', collapsed);
    setSiderCollapsed(collapsed);
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <ChatSidebar 
        collapsed={siderCollapsed} 
        onCollapse={handleSiderCollapse}
      />
      
      {/* ✅ Main Layout - adjust margin for fixed sidebar */}
      <Layout style={{ 
        marginLeft: siderCollapsed ? 80 : 280,
        transition: 'margin-left 0.2s ease'
      }}>
        <ChatHeader 
          onMenuClick={() => handleSiderCollapse(!siderCollapsed)}
          collapsed={siderCollapsed}
        />
        
        <Content style={{ 
          display: 'flex', 
          flexDirection: 'column',
          height: 'calc(100vh - 64px)',
          backgroundColor: '#fff'
        }}>
          <div style={{ 
            flex: 1, 
            overflow: 'hidden',
            display: 'flex',
            flexDirection: 'column'
          }}>
            <ChatWindow 
              sessionId={sessionId} // ✅ Pass current sessionId
              userId={user?.id} 
            />
          </div>
          
          <div style={{ 
            borderTop: '1px solid #f0f0f0',
            backgroundColor: '#fff',
            padding: '16px',
            flexShrink: 0
          }}>
            {waitingForInput && userInputFields?.length > 0 ? (
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
