import React from 'react';
import { Avatar, Typography, Tag, Space } from 'antd';
import { 
  UserOutlined, 
  RobotOutlined, 
  ToolOutlined, 
  ExclamationCircleOutlined 
} from '@ant-design/icons';

const { Text } = Typography;

const MessageBubble = ({ message }) => {
  const isUser = message.role === 'user' || message.type === 'user';
  const isAssistant = message.role === 'assistant' || message.type === 'assistant' || message.type === 'agent';
  const isTool = message.role === 'tool' || message.type === 'tool';

  // ✅ Check if this message has user input requirements
  const hasUserInputFields =
    message.paused && message.userInputRequired && message.userInputFields?.length > 0;

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

    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  // ✅ Handle role label & icon dynamically
  const getRoleLabel = () => {
    if (isUser) return 'You';
    if (isAssistant) return 'Assistant';
    if (isTool) return 'Tool';
    return 'System';
  };

  const getRoleIcon = () => {
    if (isUser) return <UserOutlined />;
    if (isAssistant) return <RobotOutlined />;
    if (isTool) return <ToolOutlined />;
    return <ExclamationCircleOutlined />;
  };

  // ✅ Handle content (including code blocks)
  const renderContent = (content) => {
    if (content && content.includes('```')) {
      const parts = content.split(/(```[\s\S]*?```)/);

      return parts.map((part, index) => {
        if (part.startsWith('```')) {
          const lines = part.split('\n');
          const language = lines[0].replace('```', '').trim();
          const code = lines.slice(1).join('\n').replace(/```$/, '');

          return (
            <pre
              key={index}
              style={{
                backgroundColor: '#f5f5f5',
                padding: '12px',
                borderRadius: '4px',
                overflow: 'auto',
                marginTop: '8px',
                fontSize: '14px',
                border: '1px solid #d9d9d9'
              }}
            >
              <code
                style={{
                  fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace',
                  whiteSpace: 'pre-wrap'
                }}
              >
                {code}
              </code>
            </pre>
          );
        } else {
          return (
            <span key={index} style={{ whiteSpace: 'pre-wrap' }}>
              {part}
            </span>
          );
        }
      });
    }

    return <span style={{ whiteSpace: 'pre-wrap' }}>{content}</span>;
  };

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: isUser ? 'flex-end' : 'flex-start',
        marginBottom: '16px',
        width: '100%'
      }}
    >
      {/* Role and timestamp header */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          marginBottom: '4px',
          flexDirection: isUser ? 'row-reverse' : 'row'
        }}
      >
        <Avatar
          size="small"
          icon={getRoleIcon()}
          style={{
            backgroundColor: isUser ? '#1890ff' : isTool ? '#52c41a' : '#722ed1',
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
          {getRoleLabel()}
        </Text>
        {message.timestamp && (
          <Text
            style={{
              fontSize: '10px',
              color: '#999',
              marginLeft: '8px'
            }}
          >
            {formatTimestamp(message.timestamp)}
          </Text>
        )}
      </div>

      {/* Message bubble */}
      <div
        style={{
          maxWidth: isUser ? '70%' : '90%',
          padding: '12px 16px',
          borderRadius: isUser ? '18px 18px 4px 18px' : '18px 18px 18px 4px',
          backgroundColor: isUser ? '#1890ff' : '#f5f5f5',
          color: isUser ? 'white' : 'black',
          wordWrap: 'break-word',
          wordBreak: 'break-word',
          boxShadow: '0 1px 2px rgba(0,0,0,0.1)'
        }}
      >
        <div style={{ margin: 0, color: isUser ? 'white' : 'black' }}>
          {renderContent(message.content)}
        </div>

        {/* Tools used */}
        {message.tools && message.tools.length > 0 && (
          <div style={{ marginTop: '8px' }}>
            <Space wrap>
              {message.tools.map((tool, index) => (
                <Tag key={index} color={isUser ? 'blue' : 'green'}>
                  <ToolOutlined style={{ marginRight: '4px' }} />
                  {tool}
                </Tag>
              ))}
            </Space>
          </div>
        )}

        {/* User input required indicator */}
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
