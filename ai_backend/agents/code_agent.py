from agno.agent import Agent
from agno.models.google import Gemini  # use any supported model/provider
from agno.tools.python import PythonTools
from agno.tools.reasoning import ReasoningTools
from config import GOOGLE_API_KEY

reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=True,
    add_few_shot=True,
)

code_agent = Agent(
    name="code_agent",
    role="Execute code, evaluate expressions, and run short scripts safely.",
    model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
    tools=[PythonTools(
        pip_install=True, 
        run_code=True,
        ), reasoning_tools],
    instructions=[
        "You are a code generation and execution agent.",
        "Use PythonTools for python code and writing execution.",
        "Show outputs based on user prompts; include errors if execution fails.",
        "Keep computations short; avoid long-running loops.",
        "Use reasoning tools to think through complex coding tasks or multi-step problems.",
    ],
    show_tool_calls=True,
    markdown=True,
)

