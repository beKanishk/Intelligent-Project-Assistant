from agno.run.team import TeamRunResponse
from agents.master_agent import master_agent
from typing import List, Dict, Any
import re

# Remove the problematic import and handle it gracefully
try:
    from agno.tools.user_control_flow import UserInputField
    HITL_AVAILABLE = True
except ImportError:
    print("⚠️ UserInputField not available in this Agno version")
    HITL_AVAILABLE = False
    UserInputField = None


def extract_response_for_user(result):
    """
    Enhanced function to extract tools used and response content from TeamRunResponse
    with proper HITL detection and improved content extraction for ANY programming language/framework
    """
    
    # Extract main content
    response_content = getattr(result, 'content', '') or ''
    
    # Extract tools used from various sources
    tools_used = []
    
    # Initialize HITL detection variables
    is_paused = False
    user_input_required = []
    hitl_available = False
    needs_confirmation = False
    run_id = getattr(result, 'run_id', None)
    paused_member_info = None
    
    print(f"🔍 Main content length: {len(response_content)}")
    print(f"🔍 Main content preview: {response_content[:200]}...")
    
    # Check if this is a TeamRunResponse with tools requiring user input
    if hasattr(result, 'tools') and result.tools:
        for tool_execution in result.tools:
            tool_name = getattr(tool_execution, 'tool_name', '')
            
            # Track tool usage
            if tool_name == "transfer_task_to_member":
                # Extract member agent from tool result
                tool_result = getattr(tool_execution, 'result', '')
                if any(keyword in tool_result.lower() for keyword in [
                    'code', 'java', 'python', 'javascript', 'html', 'css', 'react', 
                    'node', 'php', 'c++', 'c#', 'go', 'rust', 'swift', 'kotlin'
                ]):
                    tools_used.append("Code Execution")
                elif 'search' in tool_result.lower():
                    tools_used.append("Web Search")
                elif any(keyword in tool_result.lower() for keyword in ['data', 'sql', 'database', 'query']):
                    tools_used.append("Data Analysis")
                elif any(keyword in tool_result.lower() for keyword in ['math', 'calculate', 'compute']):
                    tools_used.append("Mathematical Computation")
                else:
                    tools_used.append("Task Transfer")
                    
            elif tool_name == "get_user_input":
                tools_used.append("User Input Request")
                hitl_available = True
                
                # Check if this tool requires user input
                requires_input = getattr(tool_execution, 'requires_user_input', False)
                if requires_input:
                    is_paused = True
                    
                    # Extract user input schema from the Team-level tool
                    user_input_schema = getattr(tool_execution, 'user_input_schema', [])
                    if user_input_schema:
                        for field in user_input_schema:
                            # Handle UserInputField objects directly
                            if hasattr(field, 'name'):
                                field_name = getattr(field, 'name', 'input')
                                field_type = getattr(field, 'field_type', str).__name__ if hasattr(field, 'field_type') else 'str'
                                description = getattr(field, 'description', 'User input required')
                                
                                user_input_required.append({
                                    "field_name": field_name,
                                    "field_type": field_type,
                                    "description": description,
                                    "required": getattr(field, 'value', None) is None
                                })
                    
                    # Fallback: Parse from tool_args if schema extraction fails
                    if not user_input_required:
                        tool_args = getattr(tool_execution, 'tool_args', {})
                        if 'user_input_fields' in tool_args:
                            fields = tool_args['user_input_fields']
                            if isinstance(fields, list):
                                for field in fields:
                                    if isinstance(field, dict):
                                        user_input_required.append({
                                            "field_name": field.get('field_name', 'input'),
                                            "field_type": field.get('field_type', 'str'),
                                            "description": field.get('field_description', 'User input required'),
                                            "required": True
                                        })
                    
                    # Last resort: Create default confirmation field
                    if not user_input_required:
                        user_input_required.append({
                            "field_name": "confirmation",
                            "field_type": "bool",
                            "description": "User confirmation required to continue",
                            "required": True
                        })
                        
            else:
                # Add other tool names
                tools_used.append(tool_name.replace("_", " ").title())
    
    # ✅ Enhanced member response extraction with GENERAL code detection
    if hasattr(result, 'member_responses') and result.member_responses:
        for i, member_response in enumerate(result.member_responses):
            member_content = getattr(member_response, 'content', '')
            print(f"🔍 Member {i} content length: {len(member_content)}")
            print(f"🔍 Member {i} content preview: {member_content[:200]}...")
            
            # ✅ Enhanced content extraction - detect ANY programming content
            if member_content:
                # Check for code blocks or programming-related content
                has_code = any(keyword in member_content for keyword in [
                    '```',
                    # Configuration files
                    'package.json', 'pom.xml', 'requirements.txt', 'Dockerfile', 'docker-compose.yml',
                    'Makefile', 'CMakeLists.txt', 'build.gradle', 'Cargo.toml', 'go.mod',
                    # Programming languages
                    'def ', 'function ', 'class ', 'import ', 'from ', 'include ', 'using ',
                    'public class', 'private class', 'interface ', 'enum ', 'struct ',
                    # Web technologies
                    '<html>', '<head>', '<body>', '<div>', '<script>', '<style>',
                    'const ', 'let ', 'var ', 'function(', '=>', 'export ', 'module.exports',
                    # Frameworks and libraries
                    '@SpringBootApplication', '@RestController', '@Component', '@Service',
                    'React.', 'useState', 'useEffect', 'ComponentDidMount',
                    'app.get', 'app.post', 'express()', 'router.',
                    # Database
                    'SELECT ', 'INSERT ', 'UPDATE ', 'DELETE ', 'CREATE TABLE',
                    # Other common patterns
                    'if __name__', 'main()', 'console.log', 'print(', 'System.out'
                ])
                
                has_detailed_content = (
                    len(member_content) > 500 or
                    member_content.count('\n') > 10 or
                    any(phrase in member_content for phrase in [
                        "Here's", "Below is", "Following", "I will", "Let me",
                        "To run", "To execute", "Installation", "Setup", "Configuration"
                    ])
                )
                
                # If main content is just a summary and member has detailed content, use member content
                main_is_summary = (
                    len(response_content.strip()) < 200 or
                    any(phrase in response_content.lower() for phrase in [
                        'provided the code', 'generated the code', 'here\'s the code',
                        'created the', 'built the', 'developed the', 'wrote the',
                        'completed the task', 'finished the'
                    ])
                )
                
                if (has_code or has_detailed_content) and (not response_content.strip() or main_is_summary):
                    print(f"🔍 Using member {i} content as main response")
                    response_content = member_content
                elif has_code or has_detailed_content:
                    # Append member content to main content
                    response_content += "\n\n" + member_content
            
            # Check if member agent is paused
            if hasattr(member_response, 'status'):
                member_status = getattr(member_response, 'status', '')
                if member_status and str(member_status).upper() == 'PAUSED':
                    is_paused = True
                    hitl_available = True
                    paused_member_info = {
                        "agent_name": getattr(member_response, 'agent_name', 'unknown'),
                        "agent_id": getattr(member_response, 'agent_id', 'unknown'),
                        "run_id": getattr(member_response, 'run_id', None)
                    }
                    
                    # Extract user input from paused member
                    if hasattr(member_response, 'tools') and member_response.tools:
                        for tool in member_response.tools:
                            if getattr(tool, 'tool_name', '') == 'get_user_input':
                                if getattr(tool, 'requires_user_input', False):
                                    # Extract from member tool
                                    tool_args = getattr(tool, 'tool_args', {})
                                    if 'user_input_fields' in tool_args:
                                        fields = tool_args['user_input_fields']
                                        if isinstance(fields, list):
                                            for field in fields:
                                                if isinstance(field, dict):
                                                    user_input_required.append({
                                                        "field_name": field.get('field_name', 'input'),
                                                        "field_type": field.get('field_type', 'str'),
                                                        "description": field.get('field_description', 'User input required'),
                                                        "required": True
                                                    })
            
            # Extract tool usage from member responses - GENERALIZED
            if any(keyword in member_content.lower() for keyword in [
                'code', 'python', 'java', 'javascript', 'html', 'css', 'react', 'node',
                'php', 'c++', 'c#', 'go', 'rust', 'swift', 'kotlin', 'ruby', 'scala'
            ]):
                tools_used.append("Code Execution")
            elif 'search' in member_content.lower():
                tools_used.append("Web Search")
            elif any(keyword in member_content.lower() for keyword in ['data', 'sql', 'database', 'table']):
                tools_used.append("Data Analysis")
            elif any(keyword in member_content.lower() for keyword in ['math', 'calculate', 'compute', 'equation']):
                tools_used.append("Mathematical Computation")
    
    # ✅ Enhanced message content extraction - GENERALIZED
    if hasattr(result, 'messages') and result.messages:
        for message in result.messages:
            # Check tool messages for actual generated content
            if message.role == "tool" and hasattr(message, 'content') and message.content:
                for content_item in message.content:
                    if isinstance(content_item, str):
                        # Check if this content has programming-related substance
                        has_programming_content = any(keyword in content_item for keyword in [
                            '```', 'package.json', 'pom.xml', 'requirements.txt',
                            'def ', 'function ', 'class ', 'import ', '<html>', 'const '
                        ])
                        
                        if (has_programming_content or len(content_item) > len(response_content)):
                            if not response_content.strip() or len(content_item) > len(response_content):
                                print(f"🔍 Using tool message content as main response")
                                response_content = content_item
            
            # Check assistant messages with tool calls
            if message.role == "assistant" and hasattr(message, 'tool_calls') and message.tool_calls:
                for tool_call in message.tool_calls:
                    if isinstance(tool_call, dict):
                        function_info = tool_call.get("function", {})
                        tool_name = function_info.get("name", "")
                        
                        if tool_name == "transfer_task_to_member":
                            args_str = function_info.get("arguments", "")
                            if "code-agent" in args_str or any(keyword in args_str.lower() for keyword in [
                                'code', 'programming', 'development', 'build', 'create'
                            ]):
                                tools_used.append("Code Execution")
                            elif "search-agent" in args_str or "search" in args_str.lower():
                                tools_used.append("Web Search")
                            elif any(keyword in args_str.lower() for keyword in ['data', 'sql', 'database']):
                                tools_used.append("Data Analysis")
                            elif any(keyword in args_str.lower() for keyword in ['math', 'calculation']):
                                tools_used.append("Mathematical Computation")
                        elif tool_name == "get_user_input":
                            tools_used.append("User Input Request")
                            hitl_available = True
    
    # ✅ If no run_id at top level, try to get from paused member
    if not run_id and paused_member_info:
        run_id = paused_member_info.get("run_id")
    
    # Generate appropriate response content if empty
    if not response_content.strip() and is_paused and user_input_required:
        # Create a descriptive response based on the user input required
        first_field = user_input_required[0]
        response_content = first_field.get('description', 'User confirmation required')
        needs_confirmation = True
    elif not response_content.strip():
        # If still no content, provide default message
        response_content = "Task completed successfully."
    
    # Check for natural language confirmation in response
    all_content = response_content
    if hasattr(result, 'member_responses') and result.member_responses:
        for member_response in result.member_responses:
            member_content = getattr(member_response, 'content', '')
            all_content += " " + member_content
    
    if all_content:
        content_lower = all_content.lower()
        needs_confirmation = any(phrase in content_lower for phrase in [
            "do you want", "confirm", "approve", "permission", 
            "are you sure", "proceed with", "should i continue",
            "user input required", "please confirm", "dangerous operation"
        ])
    
    # Fallback: if no tools detected, try to detect from content - GENERALIZED
    if not tools_used:
        content_lower = response_content.lower()
        if any(keyword in content_lower for keyword in [
            'code', 'programming', 'script', 'function', 'class', 'variable',
            'java', 'python', 'javascript', 'html', 'css', 'react', 'node',
            'php', 'c++', 'c#', 'go', 'rust', 'swift', 'kotlin'
        ]):
            tools_used = ["Code Execution"]
        elif any(keyword in content_lower for keyword in ['search', 'find', 'look up', 'browse']):
            tools_used = ["Web Search"]
        elif any(keyword in content_lower for keyword in ['data', 'sql', 'database', 'table', 'query']):
            tools_used = ["Data Analysis"]
        elif any(keyword in content_lower for keyword in ['math', 'calculate', 'compute', 'equation', 'formula']):
            tools_used = ["Mathematical Computation"]
        else:
            tools_used = ["AI Assistant"]
    
    # Remove duplicates and clean up
    tools_used = list(dict.fromkeys(tools_used))
    
    print(f"🔍 Final response content length: {len(response_content)}")
    print(f"🔍 Final tools used: {tools_used}")
    print(f"🔍 Is paused: {is_paused}")
    
    return {
        "tools_used": tools_used,
        "response": response_content.strip(),
        "content": response_content.strip(),
        "paused": is_paused,
        "user_input_required": user_input_required if user_input_required else None,
        "run_id": run_id,
        "hitl_available": hitl_available,
        "needs_confirmation": needs_confirmation,
        "paused_member": paused_member_info
    }


async def run_agent(objective: str, session_id: str, user_id, preferred_tool: list[str] = None):
    """
    Run the master agent with enhanced content extraction for ANY programming task
    """
    if isinstance(preferred_tool, list) and preferred_tool:
        objective += "\nTools user want to use: " + ", ".join(preferred_tool) + " (if available)"
        preferred_tool = None

    try:
        result: TeamRunResponse = await master_agent.arun(objective, user_id=user_id, session_id=session_id)
        print("🔍 Raw result:")
        print(result)
        
        # Extract response information with enhanced content detection
        extracted = extract_response_for_user(result)
        
        # Check if any member agent is paused
        if extracted["paused"]:
            return {
                "tool_used": extracted["tools_used"],
                "response": extracted["response"],
                "content": extracted["content"],
                "error": False,
                "paused": True,
                "user_input_required": extracted["user_input_required"],
                "user_input_fields": extracted["user_input_required"],  # ✅ Alias for compatibility
                "run_id": extracted["run_id"],
                "hitl_available": True,
                "paused_member": extracted.get("paused_member"),
                "needs_confirmation": extracted["needs_confirmation"]
            }
        
        # Normal response
        return {
            "tool_used": extracted["tools_used"],
            "response": extracted["response"],
            "content": extracted["content"],
            "error": False,
            "paused": False,
            "needs_confirmation": extracted["needs_confirmation"],
            "hitl_available": extracted["hitl_available"]
        }
        
    except Exception as e:
        print(f"❌ Error in run_agent: {e}")
        return {
            "tool_used": ["Error"],
            "response": f"An error occurred: {str(e)}",
            "content": f"Failed to process request: {str(e)}",
            "error": True,
            "paused": False,
            "hitl_available": False
        }


async def continue_agent_run(run_id: str, user_inputs: dict, session_id: str, user_id):
    """
    Continue a paused member agent run with user input - GENERALIZED
    """
    try:
        print(f"🔍 Continuing agent run with ID: {run_id}")
        print(f"🔍 User inputs: {user_inputs}")
        
        # Option 1: Try to continue if the Team somehow supports it
        if hasattr(master_agent, 'continue_run') or hasattr(master_agent, 'acontinue_run'):
            try:
                if hasattr(master_agent, 'acontinue_run'):
                    result = await master_agent.acontinue_run(
                        run_id=run_id,
                        user_inputs=user_inputs,
                        user_id=user_id,
                        session_id=session_id
                    )
                else:
                    result = await master_agent.continue_run(
                        run_id=run_id,
                        user_inputs=user_inputs,
                        user_id=user_id,
                        session_id=session_id
                    )
                print("🔍 Continue run result:")
                print(result)
            except Exception as e:
                print(f"❌ Continue run failed: {e}")
                # Fall back to option 2
                result = None
        else:
            result = None
        
        # Option 2: Create a new run with user input context
        if result is None:
            user_input_str = ", ".join([f"{k}: {v}" for k, v in user_inputs.items()])
            context = f"User provided the following input for the previous task: {user_input_str}. Please continue with the task using this input and provide the complete result with all necessary code, configuration files, and instructions."
            
            result: TeamRunResponse = await master_agent.arun(context, user_id=user_id, session_id=session_id)
            print("🔍 New run with context result:")
            print(result)
        
        # Extract response with enhanced content detection
        extracted = extract_response_for_user(result)
        
        # Check if still paused (in case of multi-step HITL)
        if extracted["paused"]:
            return {
                "tool_used": extracted["tools_used"],
                "response": extracted["response"],
                "content": extracted["content"],
                "error": False,
                "paused": True,
                "user_input_required": extracted["user_input_required"],
                "user_input_fields": extracted["user_input_required"],  # ✅ Alias for compatibility
                "run_id": extracted["run_id"],
                "paused_member": extracted.get("paused_member")
            }
        
        return {
            "tool_used": extracted["tools_used"],
            "response": extracted["response"],
            "content": extracted["content"],
            "error": False,
            "paused": False
        }
        
    except Exception as e:
        print(f"❌ Error in continue_agent_run: {e}")
        return {
            "tool_used": ["Error"],
            "response": f"Error continuing run: {str(e)}",
            "content": "Failed to continue execution",
            "error": True,
            "paused": False
        }
