from fastapi import FastAPI, Request
from pydantic import BaseModel
from assistant_agent import run_agent

app = FastAPI()

class AssistantRequest(BaseModel):
    message: str
    preferred_tool: str = None  # Optional

@app.post("/assist")
def ai_assist(req: AssistantRequest):
    result = run_agent(req.message, req.preferred_tool)
    return result