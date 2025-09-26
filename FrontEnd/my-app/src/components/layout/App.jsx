import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Layout, ConfigProvider, App as AntdApp, Spin } from 'antd'; // ✅ Import App as AntdApp
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ChatPage from '../../pages/ChatPage';
import AuthPage from '../../pages/AuthPage';
import { getUser, logout } from '../../reduxStore/auth/Action';
import 'antd/dist/reset.css';

const AppContent = () => { // ✅ Extract main app logic to separate component
  const dispatch = useDispatch();
  const { isAuthenticated, token, user, loading } = useSelector(state => state.auth);
  const [verifying, setVerifying] = useState(true);

  useEffect(() => {
    const verifyUser = async () => {
      // If there's a token in localStorage but no user data, verify the user
      if (token && !user) {
        console.log('Token found, verifying user...');
        try {
          const result = await dispatch(getUser(token));
          if (!result.success) {
            console.log('User verification failed, logging out...');
            dispatch(logout());
          } else {
            console.log('User verified successfully');
          }
        } catch (error) {
          console.error('User verification error:', error);
          dispatch(logout());
        }
      }
      
      setVerifying(false);
    };

    verifyUser();
  }, [dispatch, token]);

  // Show loading spinner while verifying user
  if (verifying || loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      }}>
        <div style={{ textAlign: 'center', color: 'white' }}>
          <Spin size="large" />
          <div style={{ marginTop: '16px', fontSize: '16px' }}>
            Verifying user...
          </div>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        <Route 
          path="/login" 
          element={!isAuthenticated ? <AuthPage /> : <Navigate to="/chat" replace />} 
        />
        <Route 
          path="/chat" 
          element={isAuthenticated && user ? <ChatPage /> : <Navigate to="/login" replace />} 
        />
        <Route 
          path="/" 
          element={<Navigate to={isAuthenticated && user ? "/chat" : "/login"} replace />} 
        />
      </Routes>
    </Router>
  );
};

const App = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
          borderRadius: 8,
        },
      }}
    >
      {/* ✅ Wrap with AntdApp component to enable useApp() */}
      <AntdApp>
        <AppContent />
      </AntdApp>
    </ConfigProvider>
  );
};

export default App;
