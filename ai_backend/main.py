from fastapi import FastAPI, Request
from pydantic import BaseModel
from assistant_agent import run_agent

app = FastAPI()

class AssistantRequest(BaseModel):
    message: str
    preferred_tool: list[str] = None  # Optional
    session_id: str

@app.post("/assist")
def ai_assist(req: AssistantRequest):
    result = run_agent(req.message, req.session_id, req.preferred_tool)
    return result