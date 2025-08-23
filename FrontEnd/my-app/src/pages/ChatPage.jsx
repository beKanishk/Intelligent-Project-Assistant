import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Layout } from 'antd';
import ChatSidebar from '@/components/sidebar/ChatSidebar';
import ChatHeader from '@/components/layout/ChatHeader';
import ChatWindow from '@/components/chat/ChatWindow';
import QueryForm from '@/components/query/QueryForm';
import DynamicInputForm from '@/components/chat/DynamicInputForm';
import { loadMessages } from '@/reduxStore/message/Action';
import { initializeSession } from '@/reduxStore/session/Action';

const { Content } = Layout;

const ChatPage = () => {
  const dispatch = useDispatch();
  const [siderCollapsed, setSiderCollapsed] = useState(false);
  
  const { sessionId } = useSelector(state => state.session);
  const { waitingForInput, userInputFields } = useSelector(state => state.message);
  const { user } = useSelector(state => state.auth);

  // ✅ Debug logging (remove in production)
  console.log('🔍 ChatPage render:');
  console.log('  - waitingForInput:', waitingForInput);
  console.log('  - userInputFields:', userInputFields);
  console.log('  - userInputFields.length:', userInputFields?.length);
  console.log('  - Should show form?', waitingForInput && userInputFields?.length > 0);

  useEffect(() => {
    // Initialize session when component mounts
    if (!sessionId) {
      dispatch(initializeSession());
    }
  }, [dispatch, sessionId]);

  // Load messages when sessionId changes
  useEffect(() => {
    if (sessionId) {
      dispatch(loadMessages(sessionId));
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
            <ChatWindow 
              sessionId={sessionId} 
              userId={user?.id} 
            />
          </div>
          
          <div style={{ 
            borderTop: '1px solid #f0f0f0',
            backgroundColor: '#fff',
            padding: '16px'
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
