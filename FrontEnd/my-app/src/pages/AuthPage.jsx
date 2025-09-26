import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Input, Button, Typography, Space, Alert, Divider } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, RobotOutlined } from '@ant-design/icons';
import { login, register } from '@/reduxStore/auth/Action';

const { Title, Text } = Typography;

const AuthPage = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { loading, loginLoading, error } = useSelector(state => state.auth);

  const [isLogin, setIsLogin] = useState(true);
  const [form] = Form.useForm();

  const handleSubmit = async (values) => {
    console.log('Form submitted with values:', values); // Debug log
    
    if (isLogin) {
      console.log('Attempting login...'); // Debug log
      const result = dispatch(login({ data: values, navigate })); // Add await here
      console.log('Login result:', result); // Debug log
      
      // The navigation is handled inside the login action, but you can add fallback
      if (result?.success) {
        console.log('Login successful, navigating...'); // Debug log
        navigate('/chat');
      }
    } else {
      console.log('Attempting registration...'); // Debug log
      const result = await dispatch(register(values)); // Add await here
      console.log('Register result:', result); // Debug log
      
      if (result?.success) {
        console.log('Registration successful, navigating...'); // Debug log
        navigate('/chat');
      }
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      padding: '20px'
    }}>
      <Card
        style={{
          width: '100%',
          maxWidth: '400px',
          borderRadius: '16px',
          boxShadow: '0 8px 32px rgba(0,0,0,0.1)'
        }}
        bodyStyle={{ padding: '32px' }}
      >
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <Space direction="vertical" size="small">
            <RobotOutlined style={{ fontSize: '48px', color: '#1890ff' }} />
            <Title level={3} style={{ margin: 0 }}>
              Intelligent Project Assistant
            </Title>
            <Text type="secondary">
              {isLogin ? 'Welcome back!' : 'Create your account'}
            </Text>
          </Space>
        </div>

        {error && (
          <Alert
            message={error}
            type="error"
            style={{ marginBottom: '24px' }}
            closable
          />
        )}

        <Form
          form={form}
          onFinish={handleSubmit}
          layout="vertical"
          size="large"
        >
          {!isLogin && (
            <>
              <Form.Item
                name="name"
                rules={[{ required: true, message: 'Please input your first name!' }]}
              >
                <Input
                  prefix={<UserOutlined />}
                  placeholder="Name"
                />
              </Form.Item>

      
            </>
          )}

          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Please input your email!' },
              { type: 'email', message: 'Please enter a valid email!' }
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="Email"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please input your password!' },
              { min: 6, message: 'Password must be at least 6 characters!' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Password"
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: '16px' }}>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loading || loginLoading}
            >
              {isLogin ? 'Sign In' : 'Sign Up'}
            </Button>
          </Form.Item>
        </Form>

        <Divider />
        
        <div style={{ textAlign: 'center' }}>
          <Text>
            {isLogin ? "Don't have an account? " : "Already have an account? "}
            <Button
              type="link"
              onClick={() => setIsLogin(!isLogin)}
              style={{ padding: 0 }}
            >
              {isLogin ? 'Sign Up' : 'Sign In'}
            </Button>
          </Text>
        </div>
      </Card>
    </div>
  );
};

export default AuthPage;
