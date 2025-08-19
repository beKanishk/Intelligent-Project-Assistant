import React, { useRef, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { List, Empty, Spin } from 'antd';
import MessageBubble from './MessageBubble';

const ChatWindow = () => {
  const { messages, loading, messagesLoading } = useSelector(state => state.message);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  if (messagesLoading) {
    return (
      <div style={{ 
        height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
        <Spin size="large" tip="Loading chat history..." />
      </div>
    );
  }

  if (messages.length === 0 && !loading) {
    return (
      <div style={{ 
        height: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
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
      padding: '16px'
    }}>
      <List
        dataSource={messages}
        renderItem={(message) => (
          <List.Item style={{ border: 'none', padding: '8px 0' }}>
            <MessageBubble message={message} />
          </List.Item>
        )}
      />
      
      {loading && (
        <div style={{ textAlign: 'center', padding: '20px' }}>
          <Spin size="small" />
        </div>
      )}
      
      <div ref={messagesEndRef} />
    </div>
  );
};

export default ChatWindow;
