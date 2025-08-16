# from agno.agent import Agent
# from agno.models.google import Gemini
# from tools.heavy_calc_tools import HeavyCalculationTools
# from agno.tools.calculator import CalculatorTools
# from agno.tools.reasoning import ReasoningTools
# from config import GOOGLE_API_KEY
# from agno.tools.user_control_flow import UserControlFlowTools

# reasoning_tools = ReasoningTools(
#     think=True,
#     analyze=True,
#     add_instructions=True,
#     add_few_shot=False,
# )

# calc_tools = CalculatorTools(
#     add=True,
#     subtract=True,
#     multiply=True,
#     divide=True,
#     exponentiate=True,  
#     factorial=True,      
#     is_prime=True,       
#     square_root=True,
# )


# calculation_agent = Agent(
#     name="calculation_agent",
#     role="Performs heavy numerical computations with tool calls.",
#     model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
#     tools=[HeavyCalculationTools(timeout_sec=25.0), calc_tools, reasoning_tools, UserControlFlowTools()],
#     instructions=[
#         "Use the heavy_calc_tools methods for numeric work.",
#         "For simple math, use the calc_tools.",
#         "For matrices, pass JSON arrays for A and B to matmul.",
#         "Return concise results; include shape or timing where helpful.",
#         "Use reasoning tools to think through complex calculations or multi-step problems.",
#         "If you want user suggestion on any task use UserControlFlowTools to pause and get input.",
#     ],
#     show_tool_calls=True,
#     markdown=True,
#     debug_mode=True,
# )



from agno.agent import Agent
from agno.models.google import Gemini
from tools.heavy_calc_tools import HeavyCalculationTools
from agno.tools.calculator import CalculatorTools
from agno.tools.reasoning import ReasoningTools
from agno.tools.user_control_flow import UserControlFlowTools
from config import GOOGLE_API_KEY

# Reasoning tools
reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=True,
    add_few_shot=False,
)

# Basic calculator tools
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

# HITL-enabled calculation agent
calculation_agent = Agent(
    name="calculation_agent",
    role="Performs heavy numerical computations with tool calls, with HITL safety controls.",
    model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
    tools=[
        HeavyCalculationTools(timeout_sec=25.0),
        calc_tools,
        reasoning_tools,
        UserControlFlowTools()
    ],
    instructions=[
        "You are a HITL-enabled calculation agent capable of both heavy numerical computations and basic math operations.",
        
        "### Tool Usage Rules:",
        "- For simple arithmetic → use **CalculatorTools**.",
        "- For large or complex computations → use **HeavyCalculationTools**.",
        "- For matrix operations, pass JSON arrays for A and B to `matmul`.",
        "- Always state which tool you're using and why.",
        "- After computation, return concise results along with any relevant metadata (e.g., matrix shape, execution time).",
        "- Use **ReasoningTools** before attempting multi-step or complex mathematical workflows.",
        
        "### HITL (Human-in-the-Loop) Guidelines:",
        "- If the calculation might be very large or resource-intensive (e.g., huge matrices, large factorials, Monte Carlo simulations with many iterations), **pause** execution and request user confirmation with `get_user_input`.",
        "- If the operation could take more than ~5–10 seconds or risk timing out, warn the user and confirm they want to proceed.",
        "- If numeric parameters are missing, ambiguous, or unusually large, ask the user to clarify or confirm using UserControlFlowTools.",
        "- If the output could be very lengthy (thousands of numbers), confirm the preferred output format (summary, stats, sample).",
        "- For probabilistic or approximate computations (e.g., Monte Carlo), confirm desired precision or iteration count before running.",
        
        "### Safety & Efficiency Practices:",
        "- Suggest optimized parameters if the user's requested values could be impractical or slow.",
        "- For factorial or exponentiation of large numbers, warn about execution time and memory usage before running.",
        "- For matrix multiplication, report expected output shape beforehand and confirm with the user if dimensions are large.",
        "- Use sampling, reduced iteration counts, or partial results if the user prefers quicker execution.",
        
        "### Response Format:",
        "- Step 1: State planned computation and tool choice.",
        "- Step 2: If pausing, clearly explain what will be computed, potential risks, and fields you need confirmation for.",
        "- Step 3: Present computed result along with key stats or shape info.",
        "- Step 4: Offer next steps or possible follow-up computations."
    ],
    show_tool_calls=True,
    markdown=True,
    debug_mode=True,
)
