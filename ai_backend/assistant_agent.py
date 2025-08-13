from agno.run.team import TeamRunResponse
from agents.master_agent import master_agent
from typing import List, Dict, Any

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
    with proper HITL detection (Team-level HITL support)
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
    
    # Check if this is a TeamRunResponse with tools requiring user input
    if hasattr(result, 'tools') and result.tools:
        for tool_execution in result.tools:
            tool_name = getattr(tool_execution, 'tool_name', '')
            
            # Track tool usage
            if tool_name == "transfer_task_to_member":
                # Extract member agent from tool result
                tool_result = getattr(tool_execution, 'result', '')
                if 'code' in tool_result.lower():
                    tools_used.append("Code Execution")
                elif 'search' in tool_result.lower():
                    tools_used.append("Web Search")
                elif 'data' in tool_result.lower():
                    tools_used.append("Data Analysis")
                else:
                    tools_used.append("Transfer Task To Member")
                    
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
                            "description": "User confirmation required for this dangerous operation",
                            "required": True
                        })
                        
            else:
                # Add other tool names
                tools_used.append(tool_name.replace("_", " ").title())
    
    # Check for member-level HITL (if implemented at agent level)
    if hasattr(result, 'member_responses') and result.member_responses:
        for member_response in result.member_responses:
            # Check if any member agent is paused for user input
            if hasattr(member_response, 'is_paused') and member_response.is_paused:
                is_paused = True
                hitl_available = True
                paused_member_info = {
                    "agent_name": getattr(member_response, 'agent_name', 'unknown'),
                    "agent_id": getattr(member_response, 'agent_id', 'unknown'),
                    "run_id": getattr(member_response, 'run_id', None)
                }
                
                # Use member response content if main content is empty
                if not response_content.strip():
                    response_content = getattr(member_response, 'content', '')
            
            # Extract tool usage from member responses
            member_content = getattr(member_response, 'content', '')
            if 'code' in member_content.lower() or 'python' in member_content.lower():
                tools_used.append("Code Execution")
            elif 'search' in member_content.lower():
                tools_used.append("Web Search")
            elif 'data' in member_content.lower() or 'sql' in member_content.lower():
                tools_used.append("Data Analysis")
    
    # Check messages for additional tool usage (fallback)
    if hasattr(result, 'messages') and result.messages:
        for message in result.messages:
            if message.role == "assistant" and message.tool_calls:
                for tool_call in message.tool_calls:
                    if isinstance(tool_call, dict):
                        function_info = tool_call.get("function", {})
                        tool_name = function_info.get("name", "")
                        
                        if tool_name == "transfer_task_to_member":
                            args_str = function_info.get("arguments", "")
                            if "code-agent" in args_str:
                                tools_used.append("Code Execution")
                        elif tool_name == "get_user_input":
                            tools_used.append("User Input Request")
                            hitl_available = True
    
    # Generate appropriate response content if empty
    if not response_content.strip() and is_paused and user_input_required:
        # Create a descriptive response based on the user input required
        first_field = user_input_required[0]
        response_content = first_field.get('description', 'User confirmation required')
        needs_confirmation = True
    
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
    
    # Fallback: if no tools detected, assume AI reasoning
    if not tools_used:
        tools_used = ["AI"]
    
    # Remove duplicates and clean up
    tools_used = list(dict.fromkeys(tools_used))
    
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
    if isinstance(preferred_tool, list) and preferred_tool:
        objective += "\nTools user want to use: " + ", ".join(preferred_tool) + " (if available)"
        preferred_tool = None

    result: TeamRunResponse = await master_agent.arun(objective, user_id=user_id, session_id=session_id)
    print(result)
    # Extract response information
    extracted = extract_response_for_user(result)
    
    # Check if any member agent is paused (NEW approach)
    if extracted["paused"]:
        return {
            "tool_used": extracted["tools_used"],
            "response": extracted["response"],
            "content": extracted["content"],
            "error": False,
            "paused": True,
            "user_input_required": extracted["user_input_required"],
            "run_id": extracted["run_id"],
            "hitl_available": True,
            "paused_member": extracted.get("paused_member"),  # NEW: Include member info
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


async def continue_agent_run(run_id: str, user_inputs: dict, session_id: str, user_id):
    """Continue a paused member agent run with user input"""
    try:
        # For Teams with individual agent HITL, we need to continue the specific member agent
        # Since Teams don't have continue_run, we'll use a workaround
        
        # Option 1: Try to continue if the Team somehow supports it
        if hasattr(master_agent, 'continue_run'):
            result = await master_agent.acontinue_run(
                run_id=run_id,
                user_inputs=user_inputs,
                user_id=user_id,
                session_id=session_id
            )
        else:
            # Option 2: Create a new run with user input context
            # Include the user inputs in the objective
            user_input_str = ", ".join([f"{k}: {v}" for k, v in user_inputs.items()])
            context = f"User provided the following input for the previous task: {user_input_str}. Please continue with the task using this input."
            
            result: TeamRunResponse = await master_agent.arun(context, user_id=user_id, session_id=session_id)
            print(result)
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
        print(f"Error in continue_agent_run: {e}")
        return {
            "tool_used": ["Error"],
            "response": f"Error continuing run: {str(e)}",
            "content": "Failed to continue execution",
            "error": True,
            "paused": False
        }
