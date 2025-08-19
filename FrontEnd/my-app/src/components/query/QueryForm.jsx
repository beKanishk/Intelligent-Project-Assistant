import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Input, Button, Select, Space } from 'antd';
import { SendOutlined, ToolOutlined } from '@ant-design/icons';
import { sendMessage } from '@/reduxStore/message/Action';

const { TextArea } = Input;
const { Option } = Select;

const QueryForm = () => {
  const dispatch = useDispatch();
  const { loading } = useSelector(state => state.message);
  const { user } = useSelector(state => state.auth);

  const [query, setQuery] = useState('');
  const [selectedTool, setSelectedTool] = useState(null);

  const availableTools = [
    'GoogleSearchTools',
    'WebBrowserTools',
    'GithubTools',
    'ReasoningTools'
  ];

  const handleSubmit = () => {
    if (query.trim()) {
      dispatch(sendMessage({
        content: query,
        userId: user?.id,
        tools: selectedTool ? [selectedTool] : []
      }));
      
      setQuery('');
      setSelectedTool(null);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit();
    }
  };

  return (
    <div style={{ width: '100%' }}>
      <Space.Compact style={{ width: '100%' }}>
        <TextArea
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Ask your AI assistant anything..."
          autoSize={{ minRows: 1, maxRows: 4 }}
          style={{ 
            flex: 1,
            resize: 'none'
          }}
          disabled={loading}
        />
        
        <Select
          value={selectedTool}
          onChange={setSelectedTool}
          placeholder="Tool"
          allowClear
          style={{ width: '150px' }}
          suffixIcon={<ToolOutlined />}
          disabled={loading}
        >
          {availableTools.map(tool => (
            <Option key={tool} value={tool}>
              {tool}
            </Option>
          ))}
        </Select>
        
        <Button
          type="primary"
          icon={<SendOutlined />}
          onClick={handleSubmit}
          loading={loading}
          disabled={!query.trim()}
        >
          Send
        </Button>
      </Space.Compact>

      <div style={{ 
        marginTop: '8px',
        fontSize: '12px',
        color: '#8c8c8c',
        textAlign: 'center'
      }}>
        Press Enter to send, Shift + Enter for new line
      </div>
    </div>
  );
};

export default QueryForm;
