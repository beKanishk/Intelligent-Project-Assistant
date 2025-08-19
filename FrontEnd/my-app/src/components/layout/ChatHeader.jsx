import React from 'react';
import { useSelector } from 'react-redux';
import { Layout, Button, Typography, Space } from 'antd';
import { MenuOutlined, RobotOutlined } from '@ant-design/icons';

const { Header } = Layout;
const { Title, Text } = Typography;

const ChatHeader = ({ onMenuClick }) => {
  const { sessionId } = useSelector(state => state.session);

  return (
    <Header style={{
      background: '#fff',
      borderBottom: '1px solid #f0f0f0',
      padding: '0 16px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between'
    }}>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <Button
          type="text"
          icon={<MenuOutlined />}
          onClick={onMenuClick}
          style={{ marginRight: '16px' }}
        />
        
        <Space>
          <RobotOutlined style={{ fontSize: '20px', color: '#1890ff' }} />
          <Title level={4} style={{ margin: 0 }}>
            Intelligent Project Assistant
          </Title>
        </Space>
      </div>

      {sessionId && (
        <Text type="secondary" style={{ fontSize: '12px' }}>
          Session: {sessionId.slice(0, 8)}...
        </Text>
      )}
    </Header>
  );
};

export default ChatHeader;
