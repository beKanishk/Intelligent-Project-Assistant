import React from 'react';
import { Avatar, Spin } from 'antd';
import { RobotOutlined } from '@ant-design/icons';

const AILoadingIndicator = () => {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'flex-start',
      marginBottom: '16px',
      width: '100%',
      justifyContent: 'flex-start'
    }}>
      <Avatar 
        icon={<RobotOutlined />}
        style={{ 
          backgroundColor: '#722ed1',
          marginRight: '12px',
          flexShrink: 0
        }}
      />
      
      <div style={{
        maxWidth: '70%',
        padding: '12px 16px',
        borderRadius: '12px',
        backgroundColor: '#f5f5f5',
        color: 'black',
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        gap: '8px'
      }}>
        <Spin size="small" />
        <span style={{ 
          fontSize: '14px',
          color: '#666',
          fontStyle: 'italic'
        }}>
          Thinking...
        </span>
      </div>
    </div>
  );
};

export default AILoadingIndicator;
