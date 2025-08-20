import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Input, Button, Space } from 'antd';
import { SendOutlined } from '@ant-design/icons';
import { sendMessage } from '@/reduxStore/message/Action';

const { TextArea } = Input;

const QueryForm = () => {
  const dispatch = useDispatch();
  const [inputValue, setInputValue] = useState('');
  const { loading } = useSelector(state => state.message);
  const { sessionId } = useSelector(state => state.session);

  const handleSendMessage = () => {
    if (inputValue.trim() && sessionId) {
      dispatch(sendMessage({ content: inputValue }));
      setInputValue('');
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <Space.Compact style={{ width: '100%' }}>
      <TextArea
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        onKeyPress={handleKeyPress}
        placeholder="Type your message..."
        autoSize={{ minRows: 1, maxRows: 3 }}
        disabled={loading}
      />
      <Button
        type="primary"
        icon={<SendOutlined />}
        onClick={handleSendMessage}
        disabled={loading || !inputValue.trim()}
        loading={loading}
      >
        Send
      </Button>
    </Space.Compact>
  );
};

export default QueryForm;
