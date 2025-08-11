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

# Configure ReasoningTools per docs:
# - think/analyze enabled (the toolkit provides these tools)
# - add_instructions adds built-in guidance on how to reason
# - add_few_shot adds example usage to help the agent use the tools effectively
reasoning_tools = ReasoningTools(
    think=True,
    analyze=False,  # Disable analyze to reduce steps
    add_instructions=False,  # Disable extra instructions
    add_few_shot=False,  # Disable few-shot examples
)

# Master agent that coordinates between different specialized agents
# This agent can also call ReasoningTools.think/analyze while deciding routing.
master_agent = Team(
    members=[data_sql_agent, search_agent, code_agent, calculation_agent],
    model=Gemini(id="gemini-2.0-flash", api_key=GOOGLE_API_KEY),
    mode="coordinate",
    success_criteria="Select the best agent to handle the query",
    storage=mongo_storage,  # Use MongoDB for chat history persistence
    # Use MongoDB for user memories
    memory=memory,
    enable_user_memories=True, # auto-extract/store user facts tied to user_id
    enable_agentic_memory=True, # expose tools to manage memories
    enable_session_summaries=True, # keep session summaries keyed by (user_id, session_id)
    num_history_runs=3,
    add_history_to_messages=True,
    tools=[reasoning_tools],  # <-- enable reasoning tools for coordination
    instructions=[
        "Route queries to the appropriate specialized agent based on the task.",
        "If the task involves data or SQL, use the data_sql_agent.",
        "If the task involves searching or browsing or any Github related task, use the search_agent.",
        "If the task involves code execution or evaluation, use the code_agent.",
        "If the task involves heavy calculations, use the calculation_agent.",
        "Use ReasoningTools to think through ambiguous or multi-step routing decisions before selecting an agent.",
        "After acting, analyze the result and adjust the plan if needed using ReasoningTools.",
    ],
    markdown=True,
    show_tool_calls=True,
)
