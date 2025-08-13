import requests
import io
import pandas as pd
from typing import Any, Optional, Dict
import json
from urllib.parse import urlparse
import logging
from agno.tools import Toolkit

class CloudFileReader(Toolkit):
    """
    Custom toolkit to read various file types from cloud storage URLs.
    Supports CSV, JSON, TXT, PDF (basic text extraction), and image files.
    """
    
    def __init__(self, timeout: int = 30, max_file_size: int = 50 * 1024 * 1024):
        super().__init__(name="CloudFileReader")
        self.timeout = timeout
        self.max_file_size = max_file_size
        self.logger = logging.getLogger(__name__)
        
        # Register tools in the toolkit
        self.register(self.read_file_from_url)
        self.register(self.read_csv_from_url)
        self.register(self.read_json_from_url)
        self.register(self.get_file_info)
    
    def read_file_from_url(self, url: str, file_type: Optional[str] = None) -> str:
        """
        Download and read file content from a cloud URL.
        
        Args:
            url (str): The cloud storage URL to read from
            file_type (str, optional): Force specific file type (csv, json, txt, pdf)
            
        Returns:
            str: File content or error message
        """
        try:
            # Validate URL
            parsed_url = urlparse(url)
            if not parsed_url.scheme in ['http', 'https']:
                return f"Error: Invalid URL scheme. Only HTTP/HTTPS URLs are supported."
            
            # Make request with headers
            headers = {
                'User-Agent': 'Agno-CloudFileReader/1.0',
                'Accept': '*/*'
            }
            
            response = requests.get(url, headers=headers, timeout=self.timeout, stream=True)
            response.raise_for_status()
            
            # Check file size
            content_length = response.headers.get('content-length')
            if content_length and int(content_length) > self.max_file_size:
                return f"Error: File size ({content_length} bytes) exceeds maximum limit ({self.max_file_size} bytes)"
            
            # Determine file type
            if not file_type:
                content_type = response.headers.get('content-type', '')
                file_extension = self._get_file_extension(url)
                file_type = self._determine_file_type(content_type, file_extension)
            
            # Read content
            content = response.content
            
            # Process based on file type
            return self._process_file_content(content, file_type, url)
            
        except requests.exceptions.RequestException as e:
            return f"Error fetching file from URL: {str(e)}"
        except Exception as e:
            return f"Error processing file: {str(e)}"
    
    def read_csv_from_url(self, url: str, delimiter: str = ",", max_rows: Optional[int] = None) -> str:
        """
        Specifically read and process CSV files from cloud URLs.
        
        Args:
            url (str): CSV file URL
            delimiter (str): CSV delimiter (default: comma)
            max_rows (int, optional): Maximum rows to read (for large files)
            
        Returns:
            str: CSV analysis results
        """
        try:
            response = requests.get(url, timeout=self.timeout)
            response.raise_for_status()
            
            # Read CSV with pandas
            csv_data = pd.read_csv(io.StringIO(response.text), delimiter=delimiter, nrows=max_rows)
            
            # Generate analysis
            analysis = []
            analysis.append(f"**CSV File Analysis for: {url}**\n")
            analysis.append(f"Shape: {csv_data.shape[0]} rows × {csv_data.shape[1]} columns")
            analysis.append(f"Columns: {list(csv_data.columns)}")
            
            # Data types
            analysis.append("\n**Data Types:**")
            for col, dtype in csv_data.dtypes.items():
                analysis.append(f"- {col}: {dtype}")
            
            # First few rows
            analysis.append("\n**First 5 rows:**")
            analysis.append(csv_data.head().to_string(index=False))
            
            # Basic statistics for numeric columns
            numeric_cols = csv_data.select_dtypes(include=['number']).columns
            if len(numeric_cols) > 0:
                analysis.append("\n**Numeric Summary:**")
                analysis.append(csv_data[numeric_cols].describe().to_string())
            
            return "\n".join(analysis)
            
        except Exception as e:
            return f"Error reading CSV from URL: {str(e)}"
    
    def read_json_from_url(self, url: str) -> str:
        """
        Read and format JSON files from cloud URLs.
        
        Args:
            url (str): JSON file URL
            
        Returns:
            str: Formatted JSON content
        """
        try:
            response = requests.get(url, timeout=self.timeout)
            response.raise_for_status()
            
            json_data = response.json()
            
            # Format JSON for better readability
            formatted_json = json.dumps(json_data, indent=2, ensure_ascii=False)
            
            # Add analysis
            analysis = []
            analysis.append(f"**JSON File Analysis for: {url}**\n")
            analysis.append(f"Type: {type(json_data).__name__}")
            
            if isinstance(json_data, dict):
                analysis.append(f"Keys: {list(json_data.keys())}")
            elif isinstance(json_data, list):
                analysis.append(f"Array length: {len(json_data)}")
                if json_data and isinstance(json_data[0], dict):
                    analysis.append(f"First item keys: {list(json_data[0].keys())}")
            
            analysis.append("\n**Content:**")
            analysis.append(formatted_json[:2000] + "..." if len(formatted_json) > 2000 else formatted_json)
            
            return "\n".join(analysis)
            
        except Exception as e:
            return f"Error reading JSON from URL: {str(e)}"
    
    def get_file_info(self, url: str) -> str:
        """
        Get basic information about a file without downloading its full content.
        
        Args:
            url (str): File URL
            
        Returns:
            str: File information
        """
        try:
            response = requests.head(url, timeout=self.timeout)
            response.raise_for_status()
            
            headers = response.headers
            info = []
            info.append(f"**File Information for: {url}**")
            info.append(f"Content-Type: {headers.get('content-type', 'Unknown')}")
            info.append(f"Content-Length: {headers.get('content-length', 'Unknown')} bytes")
            info.append(f"Last-Modified: {headers.get('last-modified', 'Unknown')}")
            info.append(f"Status Code: {response.status_code}")
            
            return "\n".join(info)
            
        except Exception as e:
            return f"Error getting file info: {str(e)}"
    
    def _get_file_extension(self, url: str) -> str:
        """Extract file extension from URL."""
        parsed_url = urlparse(url)
        path = parsed_url.path.lower()
        return path.split('.')[-1] if '.' in path else ''
    
    def _determine_file_type(self, content_type: str, file_extension: str) -> str:
        """Determine file type from content type and extension."""
        content_type = content_type.lower()
        
        if 'csv' in content_type or file_extension == 'csv':
            return 'csv'
        elif 'json' in content_type or file_extension == 'json':
            return 'json'
        elif 'pdf' in content_type or file_extension == 'pdf':
            return 'pdf'
        elif 'image' in content_type or file_extension in ['jpg', 'jpeg', 'png', 'gif', 'bmp']:
            return 'image'
        else:
            return 'text'
    
    def _process_file_content(self, content: bytes, file_type: str, url: str) -> str:
        """Process file content based on file type."""
        try:
            if file_type == 'csv':
                csv_data = pd.read_csv(io.BytesIO(content))
                return f"CSV file loaded with shape: {csv_data.shape}\nColumns: {list(csv_data.columns)}\nFirst 3 rows:\n{csv_data.head(3).to_string(index=False)}"
            
            elif file_type == 'json':
                json_data = json.loads(content.decode('utf-8'))
                return f"JSON file loaded. Type: {type(json_data).__name__}\nContent preview:\n{json.dumps(json_data, indent=2)[:1000]}{'...' if len(str(json_data)) > 1000 else ''}"
            
            elif file_type == 'image':
                return f"Image file detected from URL: {url}\nFile size: {len(content)} bytes\nContent type: Image binary data (use appropriate image processing tools for analysis)"
            
            elif file_type == 'pdf':
                return f"PDF file detected from URL: {url}\nFile size: {len(content)} bytes\nNote: For detailed PDF text extraction, use specialized PDF tools"
            
            else:  # text
                text_content = content.decode('utf-8', errors='ignore')
                return f"Text file content:\n{text_content[:2000]}{'...' if len(text_content) > 2000 else ''}"
                
        except Exception as e:
            return f"Error processing {file_type} file: {str(e)}"
