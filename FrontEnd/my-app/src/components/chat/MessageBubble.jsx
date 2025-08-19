import React from 'react';
import { Avatar, Typography, Space, Tag, Alert } from 'antd';
import { UserOutlined, RobotOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import ReactMarkdown from 'react-markdown';

const { Text } = Typography;

const MessageBubble = ({ message }) => {
  const isUser = message.type === 'user';
  const isError = message.error || message.type === 'error';

  return (
    <div style={{
      width: '100%',
      display: 'flex',
      justifyContent: isUser ? 'flex-end' : 'flex-start',
      marginBottom: '16px'
    }}>
      <div style={{
        maxWidth: '70%',
        display: 'flex',
        flexDirection: isUser ? 'row-reverse' : 'row',
        gap: '12px'
      }}>
        {/* Avatar */}
        <Avatar
          size={32}
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          style={{
            backgroundColor: isUser ? '#52c41a' : '#1890ff',
            flexShrink: 0
          }}
        />

        {/* Message Content */}
        <div style={{
          backgroundColor: isError ? '#fff2f0' : (isUser ? '#e6f7ff' : '#f6ffed'),
          border: `1px solid ${isError ? '#ffccc7' : (isUser ? '#91d5ff' : '#b7eb8f')}`,
          borderRadius: '12px',
          padding: '12px 16px',
          position: 'relative'
        }}>
          {/* Message Status */}
          {message.paused && (
            <Alert
              message="AI is waiting for your input"
              type="info"
              size="small"
              style={{ marginBottom: '8px' }}
            />
          )}

          {isError && (
            <Alert
              message="Error occurred"
              type="error"
              size="small"
              style={{ marginBottom: '8px' }}
              icon={<ExclamationCircleOutlined />}
            />
          )}

          {/* Message Text */}
          <div style={{ wordBreak: 'break-word' }}>
            {isUser ? (
              <Text>{message.content}</Text>
            ) : (
              <ReactMarkdown>{message.content || 'No response'}</ReactMarkdown>
            )}
          </div>

          {/* Tools Used */}
          {message.toolUsed && message.toolUsed.length > 0 && (
            <div style={{ marginTop: '8px' }}>
              <Space wrap>
                {message.toolUsed.map((tool, index) => (
                  <Tag key={index} size="small" color="blue">
                    {tool}
                  </Tag>
                ))}
              </Space>
            </div>
          )}

          {/* Timestamp */}
          <div style={{ 
            marginTop: '8px',
            textAlign: isUser ? 'right' : 'left'
          }}>
            <Text type="secondary" style={{ fontSize: '11px' }}>
              {new Date(message.timestamp).toLocaleTimeString()}
            </Text>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MessageBubble;
