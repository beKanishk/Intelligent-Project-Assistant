import React from 'react';
import { Avatar, Typography, Tag, Space, Tooltip } from 'antd';
import { UserOutlined, RobotOutlined, ToolOutlined, ExclamationCircleOutlined } from '@ant-design/icons';

const { Text, Paragraph } = Typography;

const MessageBubble = ({ message }) => {
  const isUser = message.role === 'user' || message.type === 'user';
  const isAssistant = message.role === 'assistant' || message.type === 'assistant' || message.type === 'agent';
  const isTool = message.role === 'tool' || message.type === 'tool';
  
  // ✅ Check if this message has user input requirements
  const hasUserInputFields = message.paused && message.userInputRequired && message.userInputFields?.length > 0;

  // Format timestamp
  const formatTimestamp = (timestamp) => {
    if (!timestamp) return '';
    
    const date = new Date(timestamp);
    if (isNaN(date.getTime())) {
      const localDateTimeRegex = /(\d{4}-\d{2}-\d{2})T(\d{2}:\d{2}:\d{2})/;
      const match = timestamp.match(localDateTimeRegex);
      if (match) {
        return new Date(`${match[1]}T${match[2]}`).toLocaleString();
      }
      return 'Unknown time';
    }
    
    return date.toLocaleString();
  };

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: isUser ? 'flex-end' : 'flex-start',
      marginBottom: '16px',
      width: '100%'
    }}>
      {/* Role and timestamp header */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        marginBottom: '4px',
        flexDirection: isUser ? 'row-reverse' : 'row'
      }}>
        <Avatar 
          size="small"
          icon={isUser ? <UserOutlined /> : <RobotOutlined />}
          style={{ 
            backgroundColor: isUser ? '#1890ff' : '#722ed1',
            marginLeft: isUser ? '8px' : '0',
            marginRight: isUser ? '0' : '8px'
          }}
        />
        <Text 
          style={{ 
            fontSize: '12px',
            color: '#666'
          }}
        >
          {isUser ? 'You' : 'Assistant'}
        </Text>
        {message.timestamp && (
          <Text 
            style={{ 
              fontSize: '10px',
              color: '#999',
              marginLeft: '8px'
            }}
          >
            {new Date(message.timestamp).toLocaleTimeString([], {
              hour: '2-digit',
              minute: '2-digit'
            })}
          </Text>
        )}
      </div>

      {/* Message bubble */}
      <div style={{
        maxWidth: '70%',
        padding: '12px 16px',
        borderRadius: isUser ? '18px 18px 4px 18px' : '18px 18px 18px 4px',
        backgroundColor: isUser ? '#1890ff' : '#f5f5f5',
        color: isUser ? 'white' : 'black',
        wordWrap: 'break-word',
        boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
      }}>
        {/* ✅ Always show message content */}
        <Paragraph 
          style={{ 
            margin: 0,
            color: isUser ? 'white' : 'black',
            whiteSpace: 'pre-wrap'
          }}
        >
          {message.content}
        </Paragraph>

        {/* Tools used */}
        {message.tools && message.tools.length > 0 && (
          <div style={{ marginTop: '8px' }}>
            <Space wrap>
              {message.tools.map((tool, index) => (
                <Tag 
                  key={index}
                  color={isUser ? 'blue' : 'green'}
                  size="small"
                >
                  <ToolOutlined style={{ marginRight: '4px' }} />
                  {tool}
                </Tag>
              ))}
            </Space>
          </div>
        )}

        {/* ✅ Show user input indicator if this message requires input */}
        {hasUserInputFields && (
          <div style={{ marginTop: '12px' }}>
            <Tag color="orange" icon={<ExclamationCircleOutlined />}>
              User Input Required
            </Tag>
          </div>
        )}
      </div>
    </div>
  );
};

export default MessageBubble;
