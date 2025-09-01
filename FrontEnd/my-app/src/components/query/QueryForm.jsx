import { sendMessage } from "@/reduxStore/message/Action";
import { SendOutlined, ToolOutlined } from "@ant-design/icons";
import { Space, Button, Input, Select, Tag } from "antd";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";

const { TextArea } = Input;
const { Option } = Select;

const QueryForm = () => {
  const dispatch = useDispatch();
  const [inputValue, setInputValue] = useState('');
  const [selectedTools, setSelectedTools] = useState([]);
  const { loading, aiProcessing } = useSelector(state => state.message);
  const { sessionId } = useSelector(state => state.session);

  // Available tools
  const availableTools = [
    'search_web',
    'fetch_url', 
    'execute_python',
    'create_chart',
    'search_memory'
  ];

  const handleSendMessage = () => {
    if (inputValue.trim() && sessionId) {
      const messageData = {
        content: inputValue.trim(),
        tools: selectedTools,
      };
      
      dispatch(sendMessage(messageData));
      setInputValue('');
    }
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    handleSendMessage();
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div style={{ padding: '16px' }}>
      {/* Optional Tools Selection */}
      <div style={{ marginBottom: '12px' }}>
        <Select
          mode="multiple"
          placeholder="Select tools (optional)"
          value={selectedTools}
          onChange={setSelectedTools}
          style={{ width: '100%' }}
          maxTagCount="responsive"
        >
          {availableTools.map(tool => (
            <Option key={tool} value={tool}>
              <ToolOutlined style={{ marginRight: '8px' }} />
              {tool}
            </Option>
          ))}
        </Select>
      </div>

      {/* Selected Tools Display */}
      {selectedTools.length > 0 && (
        <div style={{ marginBottom: '12px' }}>
          {selectedTools.map(tool => (
            <Tag key={tool} color="blue" style={{ marginBottom: '4px' }}>
              {tool}
            </Tag>
          ))}
        </div>
      )}

      {/* Message Input */}
      <form onSubmit={handleFormSubmit}>
        <Space.Compact style={{ width: '100%' }}>
          <TextArea
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Type your message... (Press Enter to send, Shift+Enter for new line)"
            autoSize={{ minRows: 1, maxRows: 4 }}
            // disabled={loading || aiProcessing}
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
      </form>
    </div>
  );
};

export default QueryForm;
