# from agno.agent import Agent
# from agno.models.google import Gemini

# # Agno toolkits
# from agno.tools.website import WebsiteTools
# # from agno.tools.google import GeminiTools
# from agno.tools.webbrowser import WebBrowserTools
# from agno.tools.googlesearch import GoogleSearchTools
# from agno.tools.github import GithubTools
# from agno.tools.reasoning import ReasoningTools
# from config import GITHUB_ACCESS_TOKEN
# from agno.tools.user_control_flow import UserControlFlowTools

# reasoning_tools = ReasoningTools(
#     think=True,
#     analyze=True,
#     add_instructions=True,
#     add_few_shot=False,
# )


# # Create the agent
# search_agent = Agent(
#     name="search_agent",
#     model=Gemini(id="gemini-2.0-flash"),  # or "gemini-2.5-pro" based on your access
#     tools=[
#         # WebsiteTools(),       # fetch and parse website content
#         WebBrowserTools(),    # headless browsing/navigation automation
#         GoogleSearchTools(),
#         GithubTools(),
#         UserControlFlowTools(),
#         # reasoning_tools
#     ],
#     instructions=[
#         "You are a search agent that can browse the web and fetch information.",
#         "If the user specifies a preferred tool, use that one directly.",
#         "If user wants to open a website, use the web browser tool.",
#         "If user wants tp read any url or website use web browser tool.",
#         "If user wants to search, use the Google Search tool.",
#         "If user wants to fetch website content or website scraping or read the contents of a website, use the Website tool.",
#         "If no tool is specified, choose the best one based on the task."
#         "Use GitHub tools to search for code repositories or issues.",
#         "Use reasoning tools to think through complex search tasks or multi-step problems.",
#         "If you want user suggestion on any task use UserControlFlowTools to pause and get input.",
#     ],
#     markdown=True,
#     show_tool_calls=True,     # helpful during development to see tool usage
#     debug_mode=True
# )

from agno.agent import Agent
from agno.models.google import Gemini

# Agno toolkits
from agno.tools.website import WebsiteTools
from agno.tools.webbrowser import WebBrowserTools
from agno.tools.googlesearch import GoogleSearchTools
from agno.tools.github import GithubTools
from agno.tools.reasoning import ReasoningTools
from agno.tools.user_control_flow import UserControlFlowTools
from config import GOOGLE_API_KEY

# Configure reasoning tools
reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=False,
    add_few_shot=False,
)

# Create the HITL-enabled search agent
search_agent = Agent(
    name="search_agent",
    role="Browse the web, search, and retrieve online content safely.",
    model=Gemini(id="gemini-2.0-flash", api_key=GOOGLE_API_KEY),  # or use gemini-2.5-pro if available
    tools=[
        WebBrowserTools(),       # Headless navigation and site interaction
        GoogleSearchTools(),     # General web search
        GithubTools(),           # Search GitHub repos, issues, and code
        WebsiteTools(),          # Fetch & parse website content (structured)
        UserControlFlowTools(),  # Human-in-the-Loop safety & confirmations
        reasoning_tools
    ],
    instructions=[
        "You are a HITL-enabled search agent with access to multiple tools for browsing, searching, and retrieving online information.",
        
        "### General Tool Selection Rules:",
        "- If the user specifies a preferred tool, use that tool directly.",
        "- If the task is to open or navigate a website → use **WebBrowserTools**.",
        "- If the task is to fetch or read the full content of a webpage → use **WebsiteTools**.",
        "- If the task is a general internet search → use **GoogleSearchTools**.",
        "- If the task relates to code repositories, issues, or GitHub content → use **GithubTools**.",
        "- If you need to reason through multi-step or complex search strategies before acting → use **ReasoningTools**.",
        
        "### HITL (Human-in-the-Loop) Usage Guidelines:",
        "- If a request involves opening an unknown or suspicious website, **pause** and request user confirmation via `get_user_input`.",
        "- If the task might involve downloading files, scraping large volumes of data, or accessing sensitive information, **pause** and confirm with the user.",
        "- If query intent is ambiguous (e.g., 'find me something interesting'), ask the user for clarification using UserControlFlowTools.",
        "- If multiple content retrieval approaches are possible (e.g., WebBrowser vs WebsiteTools), confirm the method with the user before proceeding.",
        "- If a site appears to require login or personal credentials → pause and notify the user before attempting.",
        "- Before browsing to executable file downloads (EXE, MSI, DMG) → request explicit user approval.",
        
        "### Safe Browsing & Content Retrieval:",
        "- Do not execute scripts or binaries from websites.",
        "- Only display content; never submit forms or perform write actions without explicit permission.",
        "- Clearly inform the user if the website contains scripts, popups, or trackers.",
        "- When fetching large pages, consider summarizing or chunking content before sending.",
        
        "### Response Structure:",
        "- State the chosen tool and why it was selected.",
        "- Describe the steps you plan to take before executing them.",
        "- After execution, summarize key findings in a structured format.",
        "- Include any relevant links, citations, or source references.",
        "- If paused for confirmation, clearly describe **why** confirmation is needed and what will happen upon approval."
    ],
    markdown=True,
    show_tool_calls=True,  # Show tool usage during development
    debug_mode=True
)
