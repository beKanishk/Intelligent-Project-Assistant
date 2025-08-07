# assistant_agent.py
from agno.agent import Agent, RunResponse
from tools import github_repo_creator, db_schema_designer
from agno.models.google import Gemini

TOOLS = [github_repo_creator, db_schema_designer]

agent = Agent(
    tools=TOOLS,
    description="Project Assistant that chooses the right plugin to achieve developer goals.",
    instructions=[
        "Choose the appropriate tool to fulfill the user's objective.",
        "If the user explicitly specifies a tool, use that one directly."
    ],
    model=Gemini(id="gemini-2.0-flash"),  # using Gemini model provider
    show_tool_calls=True
)

# def run_agent(objective: str, preferred_tool: str = None):
#     if preferred_tool:
#         for tool in TOOLS:
#             if tool.name == preferred_tool:
#                 result = tool.call(objective)
#                 return {"tool_used": tool.name, "response": result}

#         return {"error": f"Tool '{preferred_tool}' not found."}

#     # Let the AI choose
#     result: RunResponse = agent.run(objective)  # Not streaming

#     # Safely extract the useful parts
#     return {
#         "tool_used": "AI",                      # agent doesn't expose which tool was used
#         "response": result.content or "",       # main generated content
#     }
    
# def run_agent(objective: str, preferred_tool: str = None):
#     if preferred_tool:
#         for tool in TOOLS:
#             if tool.name == preferred_tool:
#                 result = tool.call(objective)
#                 return {"tool_used": tool.name, "response": result}
#         return {"error": f"Tool '{preferred_tool}' not found."}

#     result: RunResponse = agent.run(objective)

#     tool_used = "AI"
#     response_text = ""
#     print(f"Agent run completed with {result.messages} messages.")
#     for message in result.messages:
#         if message.tool_calls:
#             first_call = message.tool_calls[0]
#             tool_used = first_call.get("name") or first_call.get("tool_name") or "AI"
#         if message.content:
#             if isinstance(message.content, list):
#                 response_text += ''.join(message.content)
#             else:
#                 response_text += message.content

#     return {
#         "tool_used": tool_used,
#         "response": response_text.strip()
#     }

def run_agent(objective: str, preferred_tool: str = None):
    if preferred_tool:
        for tool in TOOLS:
            if tool.name == preferred_tool:
                return {"tool_used": tool.name, "response": tool.call(objective)}
        return {"error": f"Tool '{preferred_tool}' not found."}

    result: RunResponse = agent.run(objective)

    tool_used = "AI"
    tool_response = ""
    final_assistant_response = ""

    for msg in result.messages:
        if msg.role == "tool" and msg.content:
            # It's a list containing a string based on your dump
            contents = msg.content
            tool_response = contents[0] if isinstance(contents, list) else contents
        elif msg.role == "assistant" and msg.content:
            final_assistant_response = msg.content

    return {
        "tool_used": tool_used,
        "response": tool_response or final_assistant_response or ""
    }
