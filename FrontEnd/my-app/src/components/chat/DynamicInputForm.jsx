import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Form, Input, Select, InputNumber, Switch, Button, Space, Typography, Divider, Alert } from 'antd';
import { CheckOutlined, CloseOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { continueAgent, clearUserInputFields } from '@/reduxStore/message/Action';

const { TextArea } = Input;
const { Option } = Select;
const { Title, Text } = Typography;

const DynamicInputForm = () => {
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const [formValues, setFormValues] = useState({});
  
  const { userInputFields, loading, continueLoading } = useSelector(state => state.message);
  const { sessionId } = useSelector(state => state.session);
  const lastMessage = useSelector(state => {
    const messages = state.message.messages;
    return messages[messages.length - 1];
  });

  // ✅ Enhanced runId extraction with better fallbacks
  const runId = lastMessage?.runId || lastMessage?.run_id || null;
  
  // ✅ Debug logging
  console.log('🔍 DynamicInputForm Debug:');
  console.log('  - lastMessage:', lastMessage);
  console.log('  - runId from lastMessage.runId:', lastMessage?.runId);
  console.log('  - runId from lastMessage.run_id:', lastMessage?.run_id);
  console.log('  - final runId:', runId);

  useEffect(() => {
    // Initialize form with default values
    if (userInputFields && userInputFields.length > 0) {
      const initialValues = {};
      userInputFields.forEach(field => {
        if (field.defaultValue !== undefined && field.defaultValue !== '') {
          initialValues[field.fieldName] = field.defaultValue;
        }
      });
      form.setFieldsValue(initialValues);
      setFormValues(initialValues);
    }
  }, [userInputFields, form]);

  const handleSubmit = async () => {
    try {
      // ✅ Validate runId before proceeding
      if (!runId) {
        console.error('❌ No runId found for continue request');
        console.error('Last message:', lastMessage);
        alert('Error: Cannot continue without a valid run ID. Please try sending your message again.');
        return;
      }

      const values = await form.validateFields();
      
      // Convert form values to match backend expectations
      const userInputs = Object.keys(values).reduce((acc, key) => {
        const field = userInputFields.find(f => f.fieldName === key);
        let value = values[key];
        
        // Convert based on field type
        if (field?.fieldType === 'int' || field?.fieldType === 'integer') {
          value = typeof value === 'string' ? parseInt(value, 10) : value;
        } else if (field?.fieldType === 'bool' || field?.fieldType === 'boolean') {
          value = Boolean(value);
        }
        
        acc[key] = value;
        return acc;
      }, {});

      console.log('🔍 Submitting continue request:');
      console.log('  - userInputs:', userInputs);
      console.log('  - runId:', runId);
      console.log('  - sessionId:', sessionId);

      // Send continue request with proper structure
      dispatch(continueAgent(userInputs, runId));
    } catch (error) {
      console.error('Form validation failed:', error);
    }
  };

  const handleCancel = () => {
    dispatch(clearUserInputFields());
  };

  const renderField = (field) => {
    const {
      fieldName,
      fieldDescription,
      fieldType,
      required = false,
      defaultValue,
      options,
      minLength,
      maxLength,
      minValue,
      maxValue,
      pattern,
      placeholder
    } = field;

    // Build validation rules
    const rules = [];
    if (required) {
      rules.push({ 
        required: true, 
        message: `${fieldDescription || fieldName} is required` 
      });
    }
    if (minLength) {
      rules.push({ 
        min: minLength, 
        message: `Minimum length is ${minLength}` 
      });
    }
    if (maxLength) {
      rules.push({ 
        max: maxLength, 
        message: `Maximum length is ${maxLength}` 
      });
    }
    if (pattern) {
      rules.push({ 
        pattern: new RegExp(pattern), 
        message: 'Invalid format' 
      });
    }

    const fieldTypeKey = fieldType?.toLowerCase();

    switch (fieldTypeKey) {
      case 'str':
      case 'string':
        if (maxLength && maxLength > 100) {
          return (
            <Form.Item
              key={fieldName}
              name={fieldName}
              label={
                <span>
                  {fieldDescription || fieldName}
                  {required && <span style={{ color: 'red' }}> *</span>}
                </span>
              }
              rules={rules}
            >
              <TextArea
                placeholder={placeholder || `Enter ${fieldDescription || fieldName}`}
                rows={4}
                showCount={!!maxLength}
                maxLength={maxLength}
              />
            </Form.Item>
          );
        }
        return (
          <Form.Item
            key={fieldName}
            name={fieldName}
            label={
              <span>
                {fieldDescription || fieldName}
                {required && <span style={{ color: 'red' }}> *</span>}
              </span>
            }
            rules={rules}
          >
            <Input
              placeholder={placeholder || `Enter ${fieldDescription || fieldName}`}
              maxLength={maxLength}
            />
          </Form.Item>
        );

      case 'int':
      case 'integer':
      case 'number':
        return (
          <Form.Item
            key={fieldName}
            name={fieldName}
            label={
              <span>
                {fieldDescription || fieldName}
                {required && <span style={{ color: 'red' }}> *</span>}
              </span>
            }
            rules={rules}
          >
            <InputNumber
              style={{ width: '100%' }}
              placeholder={placeholder || `Enter ${fieldDescription || fieldName}`}
              min={minValue}
              max={maxValue}
            />
          </Form.Item>
        );

      case 'bool':
      case 'boolean':
        return (
          <Form.Item
            key={fieldName}
            name={fieldName}
            label={
              <span>
                {fieldDescription || fieldName}
                {required && <span style={{ color: 'red' }}> *</span>}
              </span>
            }
            rules={rules}
          >
            <Select 
              placeholder={`Select ${fieldDescription || fieldName}`}
              allowClear={!required}
            >
              <Option value={true}>True</Option>
              <Option value={false}>False</Option>
            </Select>
          </Form.Item>
        );

      case 'select':
        return (
          <Form.Item
            key={fieldName}
            name={fieldName}
            label={
              <span>
                {fieldDescription || fieldName}
                {required && <span style={{ color: 'red' }}> *</span>}
              </span>
            }
            rules={rules}
          >
            <Select 
              placeholder={placeholder || `Select ${fieldDescription || fieldName}`}
              allowClear={!required}
            >
              {options?.map(option => (
                <Option key={option} value={option}>
                  {option}
                </Option>
              ))}
            </Select>
          </Form.Item>
        );

      default:
        // Default to string input
        return (
          <Form.Item
            key={fieldName}
            name={fieldName}
            label={
              <span>
                {fieldDescription || fieldName}
                {required && <span style={{ color: 'red' }}> *</span>}
              </span>
            }
            rules={rules}
          >
            <Input
              placeholder={placeholder || `Enter ${fieldDescription || fieldName}`}
            />
          </Form.Item>
        );
    }
  };

  if (!userInputFields || userInputFields.length === 0) {
    return null;
  }

  return (
    <div style={{ 
      padding: '16px', 
      border: '1px solid #d9d9d9', 
      borderRadius: '6px',
      backgroundColor: '#fafafa' 
    }}>
      <Title level={5}>
        <ExclamationCircleOutlined style={{ marginRight: '8px', color: '#faad14' }} />
        Additional Information Required
      </Title>
      <Text type="secondary">Please provide the following information to continue:</Text>
      
      {/* ✅ Show warning if no runId */}
      {!runId && (
        <Alert
          message="Warning"
          description="Missing run ID. The continue function may not work properly."
          type="warning"
          showIcon
          style={{ margin: '12px 0' }}
        />
      )}
      
      <Divider />
      
      <Form
        form={form}
        layout="vertical"
        onValuesChange={(_, allValues) => setFormValues(allValues)}
      >
        {userInputFields.map(field => renderField(field))}
        
        <Form.Item style={{ marginBottom: 0, marginTop: '16px' }}>
          <Space>
            <Button 
              type="primary" 
              onClick={handleSubmit}
              loading={continueLoading}
              disabled={!runId} // ✅ Disable if no runId
            >
              Continue
            </Button>
            <Button onClick={handleCancel}>
              Cancel
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  );
};

export default DynamicInputForm;
