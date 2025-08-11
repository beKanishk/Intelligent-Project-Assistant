from agno.agent import Agent
from agno.models.google import Gemini
from tools.heavy_calc_tools import HeavyCalculationTools
from agno.tools.calculator import CalculatorTools
from agno.tools.reasoning import ReasoningTools
from config import GOOGLE_API_KEY

reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=True,
    add_few_shot=False,
)

calc_tools = CalculatorTools(
    add=True,
    subtract=True,
    multiply=True,
    divide=True,
    exponentiate=True,  
    factorial=True,      
    is_prime=True,       
    square_root=True,
)


calculation_agent = Agent(
    name="calculation_agent",
    role="Performs heavy numerical computations with tool calls.",
    model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
    tools=[HeavyCalculationTools(timeout_sec=25.0), calc_tools, reasoning_tools],
    show_tool_calls=True,
    markdown=True,
    instructions=[
        "Use the heavy_calc_tools methods for numeric work.",
        "For simple math, use the calc_tools.",
        "For matrices, pass JSON arrays for A and B to matmul.",
        "Return concise results; include shape or timing where helpful.",
        "Use reasoning tools to think through complex calculations or multi-step problems.",
    ],
)

