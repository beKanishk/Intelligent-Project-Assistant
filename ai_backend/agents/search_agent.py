from agno.agent import Agent
from agno.models.google import Gemini

# Agno toolkits
from agno.tools.website import WebsiteTools
# from agno.tools.google import GeminiTools
from agno.tools.webbrowser import WebBrowserTools
from agno.tools.googlesearch import GoogleSearchTools
from agno.tools.github import GithubTools
from agno.tools.reasoning import ReasoningTools
from config import GITHUB_ACCESS_TOKEN

reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=True,
    add_few_shot=False,
)


# Create the agent
search_agent = Agent(
    name="search_agent",
    model=Gemini(id="gemini-2.0-flash"),  # or "gemini-2.5-pro" based on your access
    tools=[
        # WebsiteTools(),       # fetch and parse website content
        WebBrowserTools(),    # headless browsing/navigation automation
        GoogleSearchTools(),
        GithubTools(),
        # reasoning_tools
    ],
    instructions=[
        "You are a search agent that can browse the web and fetch information.",
        "If the user specifies a preferred tool, use that one directly.",
        "If user wants to open a website, use the web browser tool.",
        "If user wants tp read any url or website use web browser tool.",
        "If user wants to search, use the Google Search tool.",
        "If user wants to fetch website content or website scraping or read the contents of a website, use the Website tool.",
        "If no tool is specified, choose the best one based on the task."
        "Use GitHub tools to search for code repositories or issues.",
        "Use reasoning tools to think through complex search tasks or multi-step problems.",
    ],
    markdown=True,
    show_tool_calls=True,     # helpful during development to see tool usage
    debug_mode=True
)