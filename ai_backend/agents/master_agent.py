# from agno.team.team import Team
# from agno.models.google import Gemini
# from config import GOOGLE_API_KEY
# from agents.data_sql_agent import data_sql_agent
# from agents.search_agent import search_agent
# from agents.code_agent import code_agent
# from agents.calculation_agent import calculation_agent

# # Master agent that coordinates between different specialized agents
# # This agent will route queries to the appropriate specialized agent based on the task
# master_agent = Team(
#     members=[data_sql_agent, search_agent, code_agent, calculation_agent],
#     model=Gemini(id="gemini-2.0-flash", api_key=GOOGLE_API_KEY),
#     mode="coordinate",
#     success_criteria="Select the best agent to handle the query",
#     instructions=[
#         "Route queries to the appropriate specialized agent based on the task."
#         "If the task involves data or SQL, use the data_sql_agent.",
#         "If the task involves searching or browsing, use the search_agent.",
#         "If the task involves code execution or evaluation, use the code_agent.",
#         "If the task involves heavy calculations, use the calculation_agent.",
#     ],
#     markdown=True,
#     show_tool_calls=True,
# )


# from agno.team.team import Team
# from agno.models.google import Gemini
# from config import GOOGLE_API_KEY

# from agents.data_sql_agent import data_sql_agent
# from agents.search_agent import search_agent
# from agents.code_agent import code_agent
# from agents.calculation_agent import calculation_agent
# from agno.tools.reasoning import ReasoningTools
# from Memory.MongoMemoryDb import memory
# from Memory.Storage import mongo_storage
# from agno.tools.user_control_flow import UserControlFlowTools

# # Configure ReasoningTools per docs:
# # - think/analyze enabled (the toolkit provides these tools)
# # - add_instructions adds built-in guidance on how to reason
# # - add_few_shot adds example usage to help the agent use the tools effectively
# reasoning_tools = ReasoningTools(
#     think=True,
#     analyze=False,  # Disable analyze to reduce steps
#     add_instructions=False,  # Disable extra instructions
#     add_few_shot=False,  # Disable few-shot examples
# )

# user_control_tools = UserControlFlowTools()

# # Master agent that coordinates between different specialized agents
# # This agent can also call ReasoningTools.think/analyze while deciding routing.
# master_agent = Team(
#     members=[data_sql_agent, search_agent, code_agent, calculation_agent],
#     model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
#     mode="coordinate",
#     success_criteria="Select the best agent to handle the query",
#     storage=mongo_storage,  # Use MongoDB for chat history persistence
#     # Use MongoDB for user memories
#     memory=memory,
#     enable_user_memories=True, # auto-extract/store user facts tied to user_id
#     enable_agentic_memory=True, # expose tools to manage memories
#     enable_session_summaries=True, # keep session summaries keyed by (user_id, session_id)
#     num_history_runs=3,
#     add_history_to_messages=True,
#     tools=[reasoning_tools, user_control_tools],  # <-- enable reasoning tools for coordination
#     instructions=[
#         "Route queries to the appropriate specialized agent based on the task.",
#         "If the task involves data or SQL, use the data_sql_agent.",
#         "If the task involves searching or browsing or any Github related task, use the search_agent.",
#         "If the task involves code execution or evaluation, use the code_agent.",
#         "If the task involves heavy calculations, use the calculation_agent.",
#         "Use ReasoningTools to think through ambiguous or multi-step routing decisions before selecting an agent.",
#         "After acting, analyze the result and adjust the plan if needed using ReasoningTools.",
        
#         "Use UserControlFlowTools when you need clarification or approval from the user.",
#         "Ask for user input when the task is ambiguous or potentially risky.",
#         "Request confirmation before executing potentially dangerous operations.",
#         "Gather missing information from users when needed to complete tasks effectively.",
#     ],
#     markdown=True,
#     show_tool_calls=True,
# )


from agno.team.team import Team
from agno.models.google import Gemini
from config import GOOGLE_API_KEY

from agents.data_sql_agent import data_sql_agent
from agents.search_agent import search_agent
from agents.code_agent import code_agent
from agents.calculation_agent import calculation_agent
from agno.tools.reasoning import ReasoningTools
from Memory.MongoMemoryDb import memory
from Memory.Storage import mongo_storage
from agno.tools.user_control_flow import UserControlFlowTools

# Add error handling for UserControlFlowTools
try:
    user_control_tools = UserControlFlowTools()
    print("✅ UserControlFlowTools loaded successfully")
    HITL_ENABLED = True
except Exception as e:
    print(f"⚠️ UserControlFlowTools failed to load: {e}")
    user_control_tools = None
    HITL_ENABLED = False

reasoning_tools = ReasoningTools(
    think=True,
    analyze=False,
    add_instructions=False,
    add_few_shot=False,
)

# Build tools list conditionally
tools_list = [reasoning_tools, user_control_tools]
# if user_control_tools:
#     tools_list.append(user_control_tools)

master_agent = Team(
    members=[data_sql_agent, search_agent, code_agent, calculation_agent],
    model=Gemini(id="gemini-2.0-flash", api_key=GOOGLE_API_KEY),
    mode="coordinate",
    success_criteria="Select the best agent to handle the query",
    storage=mongo_storage,
    memory=memory,
    enable_user_memories=True,
    enable_agentic_memory=True,
    enable_session_summaries=True,
    num_history_runs=3,
    add_history_to_messages=True,
    tools=tools_list,
    instructions=[
        "Route queries to the appropriate specialized agent based on the task.",
        "If the task involves data or SQL, use the data_sql_agent.",
        "If the task involves searching or browsing or any Github related task, use the search_agent.",
        "If the task involves code execution or evaluation, use the code_agent.",
        "If the task involves heavy calculations, use the calculation_agent.",
        "Use ReasoningTools to think through ambiguous or multi-step routing decisions before selecting an agent.",
        
        # Enhanced HITL instructions
        "CRITICAL SAFETY PROTOCOL:",
        "- BEFORE routing to any agent, analyze the request for dangerous operations",
        "- If request contains file deletion, system commands, or risky operations, PAUSE immediately",
        "- Use UserControlFlowTools to get user confirmation for dangerous operations",
        "- NEVER delegate dangerous operations without explicit user approval",
        
        "MANDATORY PAUSE SCENARIOS:",
        "- File deletion operations (rm, rmtree, delete, unlink)",
        "- System directory access (/tmp, /var, /home, system paths)",
        "- Code that modifies or deletes existing files",
        "- Shell commands that could affect system state",
        "- Operations involving 'important-data', 'system32', or critical paths",
        
        "HITL WORKFLOW:",
        "1) Detect dangerous operation in user request",
        "2) Use get_user_input tool to pause and request confirmation",
        "3) Clearly explain the risks and what will be affected",
        "4) Only proceed after receiving explicit user approval",
        "5) If no approval, refuse to execute and explain why",
        
        "Use UserControlFlowTools when you need clarification or approval from the user.",
        "Ask for user input when the task is ambiguous or potentially risky.",
        "Request confirmation before executing potentially dangerous operations.",
        "Gather missing information from users when needed to complete tasks effectively.",
    ] + (["UserControlFlowTools are available for HITL operations."] if HITL_ENABLED else ["Using instruction-based safety confirmations."]),
    markdown=True,
    show_tool_calls=True,
)

print(f"🤖 Master agent initialized with HITL {'enabled' if HITL_ENABLED else 'disabled'}")
