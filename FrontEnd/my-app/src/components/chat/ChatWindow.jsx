import React, { useRef, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { List, Empty, Spin } from 'antd';
import MessageBubble from './MessageBubble';
import AILoadingIndicator from './AILoadingIndicator';

const ChatWindow = ({ sessionId, userId }) => {
  const { 
    messages, 
    loading, 
    messagesLoading, 
    aiProcessing 
  } = useSelector(state => state.message);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, aiProcessing]);

  if (messagesLoading) {
    return (
      <div style={{ 
        height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#fafafa'
      }}>
        <Spin size="large" tip="Loading chat history..." />
      </div>
    );
  }

  if (messages.length === 0 && !loading && !aiProcessing) {
    return (
      <div style={{ 
        height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#fafafa'
      }}>
        <Empty
          description="Start a conversation with your AI assistant"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
        />
      </div>
    );
  }

  return (
    <div style={{
      height: '100%',
      overflowY: 'auto',
      padding: '16px 24px', // ✅ Better padding
      backgroundColor: '#fafafa', // ✅ Light background like chat apps
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* ✅ Messages Container */}
      <div style={{ flex: 1 }}>
        <List
          dataSource={messages}
          renderItem={(message) => (
            <List.Item 
              style={{ 
                border: 'none', 
                padding: '4px 0',
                backgroundColor: 'transparent'
              }}
            >
              <MessageBubble message={message} />
            </List.Item>
          )}
        />
        
        {/* ✅ Show loading indicator when AI is processing */}
        {aiProcessing && (
          <div style={{ padding: '16px 0' }}>
            <AILoadingIndicator />
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>
    </div>
  );
};

export default ChatWindow;
