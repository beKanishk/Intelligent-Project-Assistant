import os
import google.generativeai as genai

from dotenv import load_dotenv
load_dotenv()

GOOGLE_API_KEY = os.getenv("GOOGLE_API_KEY")
GITHUB_ACCESS_TOKEN = os.getenv("GITHUB_ACCESS_TOKEN")

genai.configure(api_key=GOOGLE_API_KEY) 

__all__ = ["genai"]
