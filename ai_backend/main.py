# from fastapi import FastAPI, Request
# from pydantic import BaseModel
# from assistant_agent import run_agent
# from utils.session_store import session, user_id

# app = FastAPI()

# class AssistantRequest(BaseModel):
#     message: str
#     preferred_tool: list[str] = None  # Optional
#     session_id: str
#     user_id: str

# @app.post("/assist")
# def ai_assist(req: AssistantRequest):
#     session["id"] = req.session_id
#     user_id["id"] = req.user_id
#     result = run_agent(req.message, req.session_id, req.user_id, req.preferred_tool)
#     return result


# main.py - Add new endpoint for continuing paused runs
from fastapi import FastAPI, Request
from pydantic import BaseModel
from assistant_agent import run_agent, continue_agent_run
from utils.session_store import session, user_id

app = FastAPI()

class AssistantRequest(BaseModel):
    message: str
    preferred_tool: list[str] = None
    session_id: str
    user_id: str

class ContinueRequest(BaseModel):
    run_id: str
    user_inputs: dict  # Field name -> value mapping
    session_id: str
    user_id: str

@app.post("/assist")
async def ai_assist(req: AssistantRequest):
    session["id"] = req.session_id
    user_id["id"] = req.user_id
    result = await run_agent(req.message, req.session_id, req.user_id, req.preferred_tool)
    return result

@app.post("/continue")
async def continue_run(req: ContinueRequest):
    """Continue a paused agent run with user input"""
    session["id"] = req.session_id
    user_id["id"] = req.user_id
    result = await continue_agent_run(req.run_id, req.user_inputs, req.session_id, req.user_id)
    return result
