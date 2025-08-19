import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Input, Button, Checkbox, Select, InputNumber, Card, Typography, Space, Alert } from 'antd';
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { continueAgent } from '@/reduxStore/message/Action';

const { Option } = Select;
const { Title, Text } = Typography;

const DynamicInputForm = () => {
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const { userInputFields, messages } = useSelector(state => state.message);
  const { user } = useSelector(state => state.auth);

  const handleSubmit = async (values) => {
    setLoading(true);
    
    // Get the last message to find runId
    const lastMessage = messages[messages.length - 1];
    const runId = lastMessage?.runId;

    try {
      await dispatch(continueAgent(values, runId));
      form.resetFields();
    } catch (error) {
      console.error('Error continuing agent:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    // You might want to dispatch an action to clear input fields
  };

  const renderInputField = (field) => {
    const { field_name, field_type, field_description, required, options, placeholder } = field;

    switch (field_type) {
      case 'str':
        return (
          <Input 
            placeholder={placeholder || field_description}
            maxLength={field.max_length}
          />
        );

      case 'int':
      case 'number':
        return (
          <InputNumber
            placeholder={placeholder || field_description}
            min={field.min_value}
            max={field.max_value}
            style={{ width: '100%' }}
          />
        );

      case 'boolean':
        return (
          <Checkbox>
            {field_description}
          </Checkbox>
        );

      case 'select':
        return (
          <Select placeholder={placeholder || `Select ${field_description.toLowerCase()}`}>
            {options?.map(option => (
              <Option key={option} value={option}>
                {option}
              </Option>
            ))}
          </Select>
        );

      case 'textarea':
        return (
          <Input.TextArea
            placeholder={placeholder || field_description}
            autoSize={{ minRows: 2, maxRows: 6 }}
            maxLength={field.max_length}
          />
        );

      default:
        return (
          <Input 
            placeholder={placeholder || field_description}
          />
        );
    }
  };

  return (
    <Card
      style={{ 
        border: '2px solid #1890ff',
        borderRadius: '12px'
      }}
      bodyStyle={{ padding: '16px' }}
    >
      <div style={{ marginBottom: '16px' }}>
        <Alert
          message="Additional Information Required"
          description="Please provide the following information to continue:"
          type="info"
          showIcon
          style={{ marginBottom: '16px' }}
        />
      </div>

      <Form
        form={form}
        onFinish={handleSubmit}
        layout="vertical"
        style={{ marginBottom: 0 }}
      >
        {userInputFields.map((field) => (
          <Form.Item
            key={field.field_name}
            name={field.field_name}
            label={
              <Space>
                <Text strong>{field.field_description}</Text>
                {field.required && <Text type="danger">*</Text>}
              </Space>
            }
            rules={[
              {
                required: field.required,
                message: `Please provide ${field.field_description.toLowerCase()}!`
              }
            ]}
            initialValue={field.default_value}
            tooltip={field.placeholder}
          >
            {renderInputField(field)}
          </Form.Item>
        ))}

        <Form.Item style={{ marginBottom: 0, marginTop: '16px' }}>
          <Space>
            <Button
              type="primary"
              htmlType="submit"
              icon={<CheckOutlined />}
              loading={loading}
            >
              Continue
            </Button>
            
            <Button
              onClick={handleCancel}
              icon={<CloseOutlined />}
              disabled={loading}
            >
              Cancel
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default DynamicInputForm;
