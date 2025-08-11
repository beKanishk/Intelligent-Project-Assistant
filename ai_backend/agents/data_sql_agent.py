from agno.agent import Agent
from agno.models.google import Gemini
from agno.tools.duckdb import DuckDbTools
from agno.tools.googlesearch import GoogleSearchTools
from agno.tools.reasoning import ReasoningTools
from config import GOOGLE_API_KEY

reasoning_tools = ReasoningTools(
    think=True,
    analyze=True,
    add_instructions=True,
)

# Configure DuckDB tool (customize as needed)
duckdb_tools = DuckDbTools(
    # db_path=":memory:",   # optional: in-memory DB
    # init_commands=["PRAGMA threads=4"],  # optional
    # summarize_tables=False,  # example toggles
)

# Optionally, pre-load a table from a public CSV before creating the agent
# duckdb_tools.create_table_from_path(
#     path="https://agno-public.s3.amazonaws.com/demo_data/IMDB-Movie-Data.csv",
#     table="movies"
# )

data_sql_agent = Agent(
    name="data_sql_agent",
    role="Query data with DuckDB and use Google search for context.",
    model=Gemini(id="gemini-2.5-flash", api_key=GOOGLE_API_KEY),
    tools=[
        duckdb_tools,
        GoogleSearchTools(),
        reasoning_tools,
    ],
    instructions=[
        "Use DuckDbTools to run SQL over local/remote files or existing tables.",
        "For ambiguous data context, search the web using GoogleSearchTools and cite sources.",
        "Always inspect queries if needed before running heavy operations.",
        "Use reasoning tools to think through complex queries or multi-step data tasks.",
        "Return concise results; include table shapes or summaries where helpful.",
    ],
    show_tool_calls=True,
    markdown=True,
)