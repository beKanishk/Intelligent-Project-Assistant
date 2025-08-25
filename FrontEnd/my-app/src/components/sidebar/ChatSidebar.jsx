import React, { useState, useEffect } from 'react';
import { 
  Layout,
  Menu, 
  Button, 
  Typography, 
  Space, 
  Dropdown, 
  App,  // ✅ Import App
  Empty,
  Spin
} from 'antd';
import { 
  MenuFoldOutlined, 
  MenuUnfoldOutlined, 
  MessageOutlined, 
  EllipsisOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  EditOutlined
} from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { 
  deleteSession, 
  loadSessions, 
  switchSession, 
  createSession,
  renameSession 
} from '@/reduxStore/session/Action';
import { loadMessages } from '@/reduxStore/message/Action';

const { Sider } = Layout;
const { Text } = Typography;

const ChatSidebar = ({ collapsed, onCollapse }) => {
  const dispatch = useDispatch();
  
  // ✅ Use App.useApp() instead of Modal.confirm
  const { modal, message } = App.useApp();
  
  const { 
    sessions = [], 
    currentSessionId, 
    deleteLoading, 
    loading,
    sessionId: stateSessionId 
  } = useSelector(state => {
    console.log('🔍 SELECTOR - Full session state:', state.session);
    console.log('🔍 SELECTOR - Sessions array:', state.session.sessions);
    console.log('🔍 SELECTOR - Sessions length:', state.session.sessions?.length);
    return state.session;
  });

  const [deletingSessionId, setDeletingSessionId] = useState(null);

  // Load sessions on component mount
  useEffect(() => {
    dispatch(loadSessions());
  }, [dispatch]);

  // Toggle function
  const handleToggle = () => {
    console.log('Toggle clicked, current collapsed:', collapsed);
    if (onCollapse) {
      onCollapse(!collapsed);
    }
  };


const handleDeleteSession = (sessionId, sessionName) => {
  console.log('🔍 handleDeleteSession called with:', sessionId, sessionName);
  
  modal.confirm({
    title: 'Delete Session',
    icon: <ExclamationCircleOutlined />,
    content: (
      <div>
        <p>Are you sure you want to delete this session?</p>
        <Text strong>{sessionName || `Session ${sessionId.slice(-8)}`}</Text>
        <p style={{ marginTop: 8, color: '#666' }}>
          This action cannot be undone. All messages will be lost.
        </p>
      </div>
    ),
    okText: 'Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: async () => {
      console.log('🔍 Modal onOk triggered');
      setDeletingSessionId(sessionId);
      
      try {
        console.log('🔍 About to dispatch deleteSession action');
        const result = await dispatch(deleteSession(sessionId));
        console.log('🔍 Action dispatch result:', result);
        
        if (result?.success) {
          message.success('Session deleted successfully');
          
          // ✅ Only handle current session deletion logic
          if (sessionId === currentSessionId) {
            // ✅ Check if there are other sessions remaining after deletion
            const remainingSessions = sessions.filter(s => s.id !== sessionId);
            console.log("Remaining sessions after deletion:", remainingSessions.length);
            
            if (remainingSessions.length > 0) {
              // ✅ Switch to the first remaining session instead of creating new
              const nextSession = remainingSessions[0];
              console.log('🔍 Switching to existing session:', nextSession.id);
              dispatch(switchSession(nextSession.id));
              dispatch(loadMessages(nextSession.id));
            } else {
              // ✅ Only create new session if NO sessions remain
              console.log('🔍 No remaining sessions, creating new one');
              setTimeout(() => {
                dispatch(createSession()).then((newSessionResult) => {
                  if (newSessionResult?.success) {
                    dispatch(switchSession(newSessionResult.sessionData.id));
                    dispatch(loadMessages(newSessionResult.sessionData.id));
                  }
                });
              }, 500);
            }
          }
          // ✅ If deleting a non-current session, do nothing - just let it be deleted
        } else {
          message.error(result?.error || 'Failed to delete session');
        }
      } catch (error) {
        console.error('🔍 Error in dispatch:', error);
        message.error('Error deleting session');
      } finally {
        setDeletingSessionId(null);
      }
    }
  });
};


  // ✅ Fixed rename session handler using modal from useApp
  const handleRenameSession = (sessionId, currentName) => {
    let newName = '';
    
    modal.confirm({
      title: 'Rename Session',
      icon: <EditOutlined />,
      content: (
        <div style={{ marginTop: 16 }}>
          <Text>Enter new session name:</Text>
          <input
            type="text"
            defaultValue={currentName}
            placeholder="Session name"
            style={{
              width: '100%',
              padding: '8px',
              marginTop: '8px',
              border: '1px solid #d9d9d9',
              borderRadius: '4px'
            }}
            onChange={(e) => newName = e.target.value}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                e.preventDefault();
                document.querySelector('.ant-modal .ant-btn-primary').click();
              }
            }}
            autoFocus
          />
        </div>
      ),
      okText: 'Rename',
      cancelText: 'Cancel',
      onOk: async () => {
        if (newName.trim() && newName.trim() !== currentName) {
          const result = await dispatch(renameSession(sessionId, newName.trim()));
          if (result?.success) {
            message.success('Session renamed successfully');
          } else {
            message.error('Failed to rename session');
          }
        }
      }
    });
  };

  // Session menu options
  const getSessionMenu = (session) => {
  return {
    items: [
      {
        key: 'rename',
        label: (
          <span>
            <EditOutlined style={{ marginRight: 8 }} />
            Rename Session
          </span>
        ),
        onClick: (info) => { // ✅ Use info parameter to access event
          console.log('🔍 Rename clicked for session:', session.id);
          info.domEvent?.stopPropagation(); // ✅ Stop event bubbling
          info.domEvent?.preventDefault(); // ✅ Prevent default behavior
          handleRenameSession(session.id, session?.name);
        },
        disabled: deletingSessionId === session.id || deleteLoading
      },
      {
        type: 'divider'
      },
      {
        key: 'delete',
        label: (
          <span style={{ color: '#ff4d4f' }}>
            <DeleteOutlined style={{ marginRight: 8 }} />
            Delete Session
          </span>
        ),
        onClick: (info) => { // ✅ Use info parameter to access event
          console.log('🔍 Delete menu item clicked for session:', session.id);
          info.domEvent?.stopPropagation(); // ✅ Stop event bubbling
          info.domEvent?.preventDefault(); // ✅ Prevent default behavior
          handleDeleteSession(session.id, session?.name);
        },
        disabled: deletingSessionId === session.id || deleteLoading
      }
    ]
  };
};

  // Handle session click/selection
  const handleSessionClick = ({ key }) => {
    if (key !== currentSessionId) {
      console.log('🔍 Switching to session:', key);
      dispatch(switchSession(key));
      dispatch(loadMessages(key));
    }
  };

  // Handle new session creation
  const handleCreateNewSession = async () => {
    try {
      const result = await dispatch(createSession());
      if (result?.success) {
        message.success('New session created');
        console.log('🔍 New session created:', result.sessionData);
        
        dispatch(switchSession(result.sessionData.id));
        dispatch(loadMessages(result.sessionData.id));
      } else {
        message.error('Failed to create session');
      }
    } catch (error) {
      message.error('Error creating session');
    }
  };

  // Generate session menu items
  const sessionMenuItems = sessions?.length > 0 ? sessions.map(session => ({
    key: session.id,
    icon: <MessageOutlined />,
    label: (
      <div 
        style={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          width: '100%',
          paddingRight: '8px'
        }}
      >
        <Text 
          ellipsis 
          title={session.name || `Chat ${session.id.slice(-8)}`}
          style={{ 
            maxWidth: collapsed ? 0 : '140px',
            transition: 'all 0.2s',
            flex: 1
          }}
        >
          {session.name || `Chat ${session.id.slice(-8)}`}
        </Text>
        
        {!collapsed && (
          <Dropdown
            menu={getSessionMenu(session)}
            placement="bottomRight"
            trigger={['click']}
            arrow={false}
          >
            <Button
              type="text"
              size="small"
              icon={<EllipsisOutlined />}
              style={{ 
                opacity: 0.6,
                padding: '2px 4px',
                minWidth: 'unset',
                height: '20px',
                marginLeft: '8px'
              }}
              className="session-menu-btn"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation(); // ✅ This is correct
              }}
              loading={deletingSessionId === session.id}
            />
          </Dropdown>
        )}
      </div>
    )
  })) : [];

  return (
    <Sider 
      trigger={null} 
      collapsible 
      collapsed={collapsed}
      width={280}
      collapsedWidth={80}
      onCollapse={(collapsed, type) => {
        console.log('Sider onCollapse triggered:', collapsed, type);
        if (onCollapse) {
          onCollapse(collapsed);
        }
      }}
      style={{
        background: '#fff',
        borderRight: '1px solid #f0f0f0',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        zIndex: 1000,
        boxShadow: collapsed ? 'none' : '2px 0 8px rgba(0,0,0,0.1)'
      }}
    >
      {/* Sidebar Header */}
      <div style={{ 
        padding: '16px', 
        borderBottom: '1px solid #f0f0f0',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        minHeight: '64px'
      }}>
        <Text strong style={{ fontSize: '16px' }}>
          {collapsed ? 'C' : 'Chat Sessions'}
        </Text>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={handleToggle}
          size="small"
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        />
      </div>

      {/* Sessions List Container */}
      <div style={{ 
        height: 'calc(100vh - 140px)', 
        overflowY: 'auto',
        overflowX: 'hidden'
      }}>
        {loading ? (
          <div style={{ padding: '20px', textAlign: 'center' }}>
            <Spin size="small" />
            {!collapsed && (
              <Text style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                Loading sessions...
              </Text>
            )}
          </div>
        ) : sessions.length === 0 ? (
          !collapsed && (
            <div style={{ padding: '20px' }}>
              <Empty 
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description={
                  <Text style={{ fontSize: '12px', color: '#666' }}>
                    No chat sessions yet
                  </Text>
                }
              />
            </div>
          )
        ) : (
          <Menu
            mode="inline"
            selectedKeys={[currentSessionId]}
            style={{
              borderRight: 0,
              background: 'transparent'
            }}
            items={sessionMenuItems}
            onClick={handleSessionClick}
          />
        )}
      </div>

      {/* Add New Session Button */}
      <div style={{ 
        position: 'absolute', 
        bottom: '16px', 
        left: '16px', 
        right: '16px',
        padding: '0'
      }}>
        <Button 
          type="primary" 
          block
          icon={<PlusOutlined />}
          onClick={handleCreateNewSession}
          loading={loading}
          disabled={loading}
          style={{
            height: '40px',
            fontSize: '14px'
          }}
        >
          {collapsed ? undefined : 'New Chat'}
        </Button>
      </div>
    </Sider>
  );
};

export default ChatSidebar;
