"""HTTP API Client for E2E tests"""
import requests
import logging
from typing import Dict, Any, Optional
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

logger = logging.getLogger(__name__)


class ApiClient:
    """HTTP client with retry logic and logging"""

    def __init__(self, base_url: str, timeout: int = 10):
        self.base_url = base_url.rstrip('/')
        self.timeout = timeout
        self.session = self._create_session()

    def _create_session(self) -> requests.Session:
        """Create session with retry strategy"""
        session = requests.Session()
        retry_strategy = Retry(
            total=3,
            backoff_factor=0.5,
            status_forcelist=[500, 502, 503, 504],
            allowed_methods=["GET", "POST", "PUT", "DELETE"]
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        return session

    def post(self, path: str, json: Optional[Dict[str, Any]] = None,
             headers: Optional[Dict[str, str]] = None) -> requests.Response:
        """POST request"""
        url = f"{self.base_url}{path}"
        default_headers = {"Content-Type": "application/json"}
        if headers:
            default_headers.update(headers)

        logger.info(f"POST {url}")
        logger.debug(f"Request body: {json}")

        response = self.session.post(
            url,
            json=json,
            headers=default_headers,
            timeout=self.timeout
        )

        logger.info(f"Response: {response.status_code}")
        logger.debug(f"Response body: {response.text}")

        return response

    def get(self, path: str, params: Optional[Dict[str, Any]] = None,
            headers: Optional[Dict[str, str]] = None) -> requests.Response:
        """GET request"""
        url = f"{self.base_url}{path}"

        logger.info(f"GET {url}")
        if params:
            logger.debug(f"Query params: {params}")

        response = self.session.get(
            url,
            params=params,
            headers=headers,
            timeout=self.timeout
        )

        logger.info(f"Response: {response.status_code}")
        logger.debug(f"Response body: {response.text}")

        return response

    def delete(self, path: str, headers: Optional[Dict[str, str]] = None) -> requests.Response:
        """DELETE request"""
        url = f"{self.base_url}{path}"

        logger.info(f"DELETE {url}")

        response = self.session.delete(
            url,
            headers=headers,
            timeout=self.timeout
        )

        logger.info(f"Response: {response.status_code}")

        return response
