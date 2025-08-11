from fastapi import FastAPI, Request
from pydantic import BaseModel
from assistant_agent import run_agent
from utils.session_store import session, user_id

app = FastAPI()

class AssistantRequest(BaseModel):
    message: str
    preferred_tool: list[str] = None  # Optional
    session_id: str
    user_id: str

@app.post("/assist")
def ai_assist(req: AssistantRequest):
    session["id"] = req.session_id
    user_id["id"] = req.user_id
    print(req.user_id)
    result = run_agent(req.message, req.session_id, req.user_id, req.preferred_tool)
    return result