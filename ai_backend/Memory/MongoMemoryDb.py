from agno.memory.v2.memory import Memory
from agno.memory.v2.db.mongodb import MongoMemoryDb

memory_db = MongoMemoryDb(
    collection_name="ai_user_memories",
    db_url="mongodb://localhost:27017/project_assistant",  # or your cloud URI
    db_name="project_assistant"
)

memory = Memory(db=memory_db)