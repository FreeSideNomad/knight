"""PostgreSQL Database Client for E2E tests"""
import psycopg
import logging
from typing import Optional, List, Dict, Any, Tuple
from contextlib import contextmanager

logger = logging.getLogger(__name__)


class PostgresClient:
    """PostgreSQL client for database assertions in E2E tests"""

    def __init__(self,
                 host: str = "localhost",
                 port: int = 5432,
                 database: str = "knight",
                 user: str = "knight",
                 password: str = "knight"):
        self.connection_params = {
            "host": host,
            "port": port,
            "dbname": database,  # psycopg3 uses 'dbname' not 'database'
            "user": user,
            "password": password
        }
        logger.info(f"PostgreSQL client initialized for {user}@{host}:{port}/{database}")

    @contextmanager
    def _get_connection(self):
        """Context manager for database connections"""
        conn = None
        try:
            conn = psycopg.connect(**self.connection_params)
            yield conn
        except psycopg.Error as e:
            logger.error(f"Database error: {e}")
            raise
        finally:
            if conn:
                conn.close()

    def execute(self, query: str, params: Optional[Tuple] = None) -> None:
        """Execute a query without returning results (INSERT, UPDATE, DELETE)"""
        with self._get_connection() as conn:
            with conn.cursor() as cursor:
                logger.debug(f"Executing query: {query}")
                if params:
                    logger.debug(f"Parameters: {params}")
                cursor.execute(query, params)
                conn.commit()
                logger.info(f"Query executed successfully")

    def query(self, query: str, params: Optional[Tuple] = None) -> List[Tuple]:
        """Execute a query and return all results"""
        with self._get_connection() as conn:
            with conn.cursor() as cursor:
                logger.debug(f"Querying: {query}")
                if params:
                    logger.debug(f"Parameters: {params}")
                cursor.execute(query, params)
                results = cursor.fetchall()
                logger.info(f"Query returned {len(results)} rows")
                return results

    def query_one(self, query: str, params: Optional[Tuple] = None) -> Optional[Tuple]:
        """Execute a query and return one result or None"""
        with self._get_connection() as conn:
            with conn.cursor() as cursor:
                logger.debug(f"Querying one: {query}")
                if params:
                    logger.debug(f"Parameters: {params}")
                cursor.execute(query, params)
                result = cursor.fetchone()
                if result:
                    logger.info(f"Query returned 1 row")
                else:
                    logger.info(f"Query returned no rows")
                return result

    def scalar(self, query: str, params: Optional[Tuple] = None) -> Any:
        """Execute a query and return single scalar value"""
        result = self.query_one(query, params)
        if result:
            return result[0]
        return None

    def count(self, table: str, schema: str, where: Optional[str] = None,
              params: Optional[Tuple] = None) -> int:
        """Count rows in a table with optional WHERE clause"""
        query = f"SELECT COUNT(*) FROM {schema}.{table}"
        if where:
            query += f" WHERE {where}"

        count = self.scalar(query, params)
        logger.info(f"Count for {schema}.{table}: {count}")
        return count if count is not None else 0

    def exists(self, table: str, schema: str, where: str,
               params: Optional[Tuple] = None) -> bool:
        """Check if at least one row exists matching criteria"""
        count = self.count(table, schema, where, params)
        return count > 0

    def query_dict(self, query: str, params: Optional[Tuple] = None) -> List[Dict[str, Any]]:
        """Execute a query and return results as list of dicts"""
        with self._get_connection() as conn:
            with conn.cursor() as cursor:
                logger.debug(f"Querying (dict): {query}")
                if params:
                    logger.debug(f"Parameters: {params}")
                cursor.execute(query, params)

                # Get column names
                columns = [desc[0] for desc in cursor.description]

                # Convert rows to dicts
                results = []
                for row in cursor.fetchall():
                    results.append(dict(zip(columns, row)))

                logger.info(f"Query returned {len(results)} rows as dicts")
                return results

    def truncate(self, table: str, schema: str, cascade: bool = False) -> None:
        """Truncate a table (useful for test cleanup)"""
        query = f"TRUNCATE TABLE {schema}.{table}"
        if cascade:
            query += " CASCADE"

        logger.warning(f"Truncating table {schema}.{table}")
        self.execute(query)

    def table_exists(self, table: str, schema: str) -> bool:
        """Check if table exists in schema"""
        query = """
            SELECT EXISTS (
                SELECT FROM information_schema.tables
                WHERE table_schema = %s
                AND table_name = %s
            )
        """
        exists = self.scalar(query, (schema, table))
        logger.info(f"Table {schema}.{table} exists: {exists}")
        return exists
