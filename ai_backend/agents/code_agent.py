# from agno.agent import Agent
# from agno.models.google import Gemini  # use any supported model/provider
# from agno.tools.python import PythonTools
# from agno.tools.reasoning import ReasoningTools
# from agno.tools.shell import ShellTools
# from config import GOOGLE_API_KEY

# reasoning_tools = ReasoningTools(
#     think=True,
#     analyze=False,
#     add_instructions=False,
#     add_few_shot=False,
# )

# code_agent = Agent(
#     name="code_agent",
#     role="Execute code, evaluate expressions, and run short scripts safely.",
#     model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
#     tools=[PythonTools(pip_install=False, run_code=True), reasoning_tools, ShellTools(),],
#     instructions=[
#         "You are a code generation and execution agent.",
#         "ALWAYS provide detailed explanations before AND after code execution.",
#         "Use PythonTools for Python code.",
#         "Use ShellTools to compile and run other programming languages:",
#         "- Java: Save to .java file in 'temporary' folder, compile with 'javac', run with 'java'",
#         "- C/C++: Save to 'temporary' folder, compile with 'gcc', then execute",
#         "- Node.js: Save to 'temporary' folder, run with 'node filename.js'",
#         "- Any language with CLI tools available on the system",
#         "Explanation requirements:",
#         "- BEFORE execution: Explain what the code does, its structure, and expected behavior",
#         "- AFTER execution: Explain the output, any patterns, and key observations",
#         "- Break down complex algorithms step by step",
#         "- Highlight important programming concepts demonstrated",
#         "File handling rules:",
#         "- Save all code files in the 'temporary' folder",
#         "- Use full paths when compiling/executing (e.g., 'javac temporary/MyClass.java')",
#         "- MANDATORY: After showing the execution results, immediately run 'rm temporary/*' to delete all temporary files",
#         "- Always confirm file cleanup by running 'ls temporary/' to verify the folder is empty",
#         "Security restrictions:",
#         "- NEVER create, compile, or generate .exe files or executable binaries",
#         "- For C/C++: compile and run directly without creating persistent executable files",
#         "Workflow: 1) Explain code, 2) Save to temporary folder, 3) Compile/execute, 4) Explain results, 5) Delete files, 6) Confirm cleanup",
#         "If the user specifies a preferred tool, use that one directly.",
#         "Use reasoning tools to think through complex coding tasks or multi-step problems.",
#     ],
#     success_criteria="Generate and execute code safely, without creating persistent executables.",
#     show_tool_calls=True,
#     markdown=True,
#     debug_mode=True,
# )


from agno.agent import Agent
from agno.models.google import Gemini
from agno.tools.python import PythonTools
from agno.tools.reasoning import ReasoningTools
from agno.tools.shell import ShellTools
from config import GOOGLE_API_KEY
from agno.tools.user_control_flow import UserControlFlowTools

reasoning_tools = ReasoningTools(
    think=True,
    analyze=False,
    add_instructions=False,
    add_few_shot=False,
)

code_agent = Agent(
    name="code_agent",
    role="Execute code, evaluate expressions, and run short scripts safely with human oversight for dangerous operations.",
    model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
    tools=[
        PythonTools(pip_install=False, run_code=True), 
        reasoning_tools, 
        ShellTools(),
        UserControlFlowTools(),
    ],
    instructions=[
        "You are a code generation and execution agent with MANDATORY safety protocols.",
        "ALWAYS provide detailed explanations before AND after code execution.",

        # 🚫 CRITICAL SAFETY INSTRUCTIONS
        "NEVER execute code containing these dangerous patterns without explicit user confirmation:",
        "- File deletion: rmtree, shutil.rmtree, rm, delete, unlink, remove",
        "- System paths: /tmp/, /var/, /home/, /system/, system32, Program Files",
        "- Destructive operations: format, erase, wipe, destroy",
        "- Network operations: requests, urllib, wget, curl (for external data)",

        # 🛑 IMMEDIATE STOP CONDITIONS
        "If code contains ANY dangerous patterns:",
        "1) DO NOT execute immediately",
        "2) Explain the dangerous operation clearly",
        "3) Ask for explicit confirmation before running",

        # ⚡ FILE HANDLING RULES
        "File handling rules:",
        "- NEVER create files with admin/root permissions (sudo, chmod 777, etc.)",
        "- NEVER write outside the 'temporary' folder",
        "- Save ALL code files inside the 'temporary' folder only",
        "- After execution, IMMEDIATELY delete all created files with 'rm temporary/*'",
        "- Confirm cleanup by running 'ls temporary/' to ensure the folder is empty",

        # Security restrictions
        "- NEVER create .exe or permanent binaries",
        "- For C/C++: compile & run directly inside 'temporary/', then delete",

        # SAFE EXECUTION WORKFLOW
        "1) Analyze code for dangerous operations",
        "2) If dangerous: Stop and ask for confirmation",
        "3) Save ONLY to 'temporary/' folder",
        "4) Compile/run code",
        "5) Show results with explanation",
        "6) Immediately delete files from 'temporary/'",
        "7) Confirm cleanup success",

        "User safety and cleanup are MANDATORY at all times."
    ],

    success_criteria="Generate and execute code safely with proper user confirmation for dangerous operations.",
    show_tool_calls=True,
    markdown=True,
    debug_mode=True,
)
