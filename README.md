# Intelligent Project Assistant

An AI-powered project assistant with Human-in-the-Loop (HITL) safety controls that can execute code, analyze data, perform web searches, and handle calculations while ensuring user safety through intelligent dangerous operation detection.

## 🚀 Features

- **Multi-Agent AI System**: Specialized agents for different tasks (code execution, data analysis, web search, calculations)
- **Human-in-the-Loop (HITL) Safety**: Automatic detection and user confirmation for dangerous operations
- **Team-Based Agent Coordination**: Master agent intelligently routes tasks to appropriate specialized agents
- **Memory & Session Management**: Persistent conversation history and user memory using MongoDB
- **Real-time Communication**: RESTful API with proper error handling and validation
- **Security-First Approach**: Comprehensive dangerous operation detection and user confirmation workflows

## 🏗️ Architecture
Frontend (optional)

Spring Boot backend (Java) — API/Session layer

Python AI backend (FastAPI) — Agent orchestration and HITL

PostgreSQL/MySQL — application data (choose one)

MongoDB — AI memory and session summaries

## 🛡️ HITL Safety System

The project implements a comprehensive Human-in-the-Loop system that:

- **Detects dangerous operations** (file deletion, system commands, data modifications)
- **Pauses execution** when risky operations are detected
- **Requests user confirmation** with clear risk explanations
- **Provides structured input schemas** for user decisions
- **Continues or cancels** operations based on user approval

### Dangerous Operation Detection

The system automatically detects and pauses for:
- File deletion (`rm`, `rmtree`, `delete`, `unlink`)
- System directory access (`/tmp/`, `/var/`, `/home/`, system paths)
- Database operations (`DROP TABLE`, `DELETE FROM`)
- Package installations and system modifications
- Network operations with external files
- Shell commands affecting system state

## 📋 Prerequisites

- **Java 17+** (Spring Boot backend)
- **Python 3.9+** (AI backend)
- **PostgreSQL 13+**
- **MongoDB 5.0+**
- **Node.js 16+** (if frontend is used)

## 🚦 Getting Started

### 1. Clone the Repository
git clone https://github.com/beKanishk/Intelligent-Project-Assistant.git
cd Intelligent-Project-Assistant

### 2. Setup Python AI Backend
cd ai_backend
python -m venv myenv

# Activate
# Windows
myenv\Scripts\activate
# macOS/Linux
source myenv/bin/activate

pip install -r requirements.txt

# Environment variables
cp .env.example .env
# Edit .env to set:
# GOOGLE_API_KEY=your_gemini_api_key
# MONGODB_URI=mongodb://localhost:27017/intelligent_assistant



### 3. Setup Spring Boot Backend
cd backend
mvn install # or ./gradlew build
cp src/main/resources/application.properties.example src/main/resources/application.properties

### 4. Database Setup
**MySQL**
CREATE DATABASE intelligent_assistant;
CREATE USER ai_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE intelligent_assistant TO ai_user;
**MongoDB**
sudo systemctl start mongod

### 5. Start the Applications
**Python backend:**
cd ai_backend
uvicorn main:app --reload --port 8001
**Spring backend:**
Access:
- Spring Boot API: `http://localhost:8080`
- Python AI Backend: `http://localhost:8001/docs`

## 🎯 API Usage

### Send Message
`POST /api/send/{sessionId}`
{
"content": "Create a Python script to analyze a CSV file",
"tools": ["Code Execution"],
"userId": "1"
}

**Response (normal):**
{
"response": "I'll create a Python script for CSV analysis...",
"paused": false,
"tool_used": ["Code Execution"]
}

**Response (HITL):**
{
"response": "This operation will delete files. Do you want to proceed?",
"paused": true,
"user_input_required": [
{
"field_name": "confirm_deletion",
"field_type": "bool",
"description": "Confirm dangerous file deletion",
"required": true
}
],
"run_id": "run-12345"
}

### Continue Paused Execution
`POST /api/continue/{sessionId}`
{
"runId": "run-12345",
"userInputs": { "confirm_deletion": true },
"sessionId": "session-id",
"userId": "1"
}

## 🤖 Agents
- **Code Agent** — Coding tasks, HITL for file/system operations
- **Data SQL Agent** — Database queries, HITL for destructive SQL
- **Search Agent** — Web/GitHub searches
- **Calculation Agent** — Heavy compute/math tasks

## 🧪 Testing HITL
Dangerous:
{
"content": "Delete all files in /tmp/important-data",
"tools": ["Code Execution"],
"sessionId": "hitl-test",
"userId": "1"
}
Safe:
{
"content": "Add two numbers",
"tools": ["Code Execution"],
"sessionId": "safe-test",
"userId": "1"
}

## 📂 Project Structure
backend/ (Spring Boot)
ai_backend/ (Python FastAPI AI logic + agents)

## 🔒 Security
- HITL confirmation before dangerous actions
- Whitelisting of allowed actions
- SQL injection prevention
- Secure session handling



