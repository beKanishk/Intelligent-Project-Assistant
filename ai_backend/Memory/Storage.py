from agno.storage.mongodb import MongoDbStorage

# MongoDB storage for chat history persistence this will also save session_id and user_id
mongo_storage = MongoDbStorage(
    collection_name="agent_sessions",
    db_url="mongodb://localhost:27017/project_assistant",
    db_name="project_assistant"
)