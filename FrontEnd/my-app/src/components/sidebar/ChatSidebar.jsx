import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Layout, Menu, Button, Typography, Avatar, Dropdown } from 'antd';
import { 
  PlusOutlined, 
  MessageOutlined, 
  UserOutlined, 
  HistoryOutlined,
  SettingOutlined,
  LogoutOutlined 
} from '@ant-design/icons';
import { createSession, loadSessionHistory, setSessionData } from '@/reduxStore/session/Action';
import { logout } from '@/reduxStore/auth/Action';
import { 
  clearMessages, 
  loadMessages, 
  subscribeToSession, 
  disconnectWebSocket,
  connectWebSocket 
} from '@/reduxStore/message/Action';

const { Sider } = Layout;
const { Text } = Typography;

const ChatSidebar = ({ collapsed, onCollapse }) => {
  const dispatch = useDispatch();
  const { user } = useSelector(state => state.auth);
  const { history, sessionId } = useSelector(state => state.session);
  
  // Local state to control menu selection
  const [selectedKey, setSelectedKey] = useState(null);

  // Load user's session history when component mounts
  useEffect(() => {
    if (user?.id) {
      dispatch(loadSessionHistory(user?.id));
    }
  }, [dispatch, user?.id]);

  // Initialize WebSocket connection when component mounts
  useEffect(() => {
    dispatch(connectWebSocket());
    
    return () => {
      dispatch(disconnectWebSocket());
    };
  }, [dispatch]);

  // Sync selectedKey with sessionId from Redux
  useEffect(() => {
    if (sessionId) {
      setSelectedKey(sessionId.toString());
    }
  }, [sessionId]);

  // Safe date formatting function
  const formatDate = (dateStr) => {
    if (!dateStr) return 'Unknown Date';
    const date = new Date(dateStr);
    return isNaN(date.getTime()) ? 'Unknown Date' : date.toLocaleDateString();
  };

  const handleNewChat = () => {
    dispatch(clearMessages());
    dispatch(createSession()).then((newSession) => {
      if (newSession?.id) {
        dispatch(subscribeToSession(newSession.id));
        setSelectedKey(newSession.id.toString());
      }
    });
  };

  const handleMenuClick = (e) => {
    const clickedSessionId = e.key;
    setSelectedKey(clickedSessionId);
    
    // Find the session object by ID
    const session = history.find(s => s.id.toString() === clickedSessionId);
    
    if (session) {
      dispatch(clearMessages());
      dispatch(setSessionData(session));
      localStorage.setItem("sessionId", session.id);
      dispatch(loadMessages(session.id));
      dispatch(subscribeToSession(session.id));
    }
  };

  const handleLogout = () => {
    dispatch(disconnectWebSocket());
    dispatch(logout());
  };

  const userMenu = (
    <Menu>
      <Menu.Item key="profile" icon={<UserOutlined />}>
        Profile
      </Menu.Item>
      <Menu.Item key="settings" icon={<SettingOutlined />}>
        Settings
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="logout" icon={<LogoutOutlined />} onClick={handleLogout}>
        Logout
      </Menu.Item>
    </Menu>
  );

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={onCollapse}
      width={280}
      collapsedWidth={0}
      breakpoint="lg"
      style={{
        background: '#fff',
        borderRight: '1px solid #f0f0f0'
      }}
      trigger={null}
    >
      <div style={{ 
        height: '100vh',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {/* Header */}
        <div style={{ 
          padding: '16px',
          borderBottom: '1px solid #f0f0f0'
        }}>
          <Button 
            type="primary" 
            icon={<PlusOutlined />}
            block
            onClick={handleNewChat}
          >
            New Chat
          </Button>
        </div>

        {/* Chat History */}
        <div style={{ 
          flex: 1,
          overflowY: 'auto',
          padding: '8px 0'
        }}>
          {history && history.length > 0 ? (
            <Menu
              mode="inline"
              style={{ border: 'none' }}
              selectedKeys={selectedKey ? [selectedKey] : []}
              onClick={handleMenuClick}
            >
              {history.map((session, index) => (
                <Menu.Item 
                  key={session.id.toString()}
                  icon={<MessageOutlined />}
                >
                  <div style={{ 
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}>
                    <Text ellipsis style={{ maxWidth: '180px' }}>
                      {session.title || `Session ${session.id}`}
                    </Text>
                    <Text type="secondary" style={{ fontSize: '12px' }}>
                      {formatDate(session.createdAt || session.timestamp)}
                    </Text>
                  </div>
                </Menu.Item>
              ))}
            </Menu>
          ) : (
            <div style={{ 
              padding: '20px', 
              textAlign: 'center', 
              color: '#999' 
            }}>
              <HistoryOutlined style={{ fontSize: '24px', marginBottom: '8px' }} />
              <div>No chat history</div>
              <Text type="secondary" style={{ fontSize: '12px' }}>
                Start a new conversation
              </Text>
            </div>
          )}
        </div>

        {/* User Profile */}
        <div style={{ 
          padding: '16px',
          borderTop: '1px solid #f0f0f0'
        }}>
          <Dropdown overlay={userMenu} placement="topRight" trigger={['click']}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              cursor: 'pointer',
              padding: '8px',
              borderRadius: '8px',
              transition: 'background-color 0.3s'
            }}
            className="user-profile-hover"
            >
              <Avatar 
                size="small" 
                icon={<UserOutlined />}
                style={{ backgroundColor: '#1890ff' }}
              />
              {!collapsed && (
                <div style={{ marginLeft: '12px' }}>
                  <Text strong>{user?.firstName || 'User'}</Text>
                  <br />
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    {user?.email}
                  </Text>
                </div>
              )}
            </div>
          </Dropdown>
        </div>
      </div>
    </Sider>
  );
};

export default ChatSidebar;
