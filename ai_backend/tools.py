from agno.tools import tool

@tool(name="GitHubRepoCreator", description="Creates a GitHub repository based on project specs.")
def github_repo_creator(objective: str) -> str:
    return f"[Simulated] Created GitHub repo for: {objective}"

@tool(name="DatabaseSchemaDesigner", description="Generates a database schema from project requirements.")
def db_schema_designer(objective: str) -> str:
    return f"[Simulated] Designed DB schema for: {objective}"

@tool(name="SpringBootServiceGenerator", description="Generates boilerplate Spring Boot services.")
def spring_service_generator(objective: str) -> str:
    return f"[Simulated] Generated Spring Boot services for: {objective}"

# Add more tools as needed
