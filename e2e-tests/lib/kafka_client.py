"""Kafka Consumer Client for E2E tests using docker exec"""
import json
import logging
import subprocess
from typing import Optional, Callable, Dict, Any, List

logger = logging.getLogger(__name__)


class KafkaTestConsumer:
    """Kafka consumer using docker exec for E2E tests"""

    def __init__(self, bootstrap_servers: str = "localhost:9093"):
        self.bootstrap_servers = bootstrap_servers
        self.container = "knight-kafka"

    def consume_one(self,
                    topic: str,
                    timeout: int = 10,
                    filter_fn: Optional[Callable[[Dict[str, Any]], bool]] = None,
                    from_beginning: bool = True) -> Optional[Dict[str, Any]]:
        """
        Consume a single message from Kafka topic with optional filtering.

        Args:
            topic: Kafka topic name
            timeout: Max seconds to wait for message
            filter_fn: Optional function to filter messages (returns True to accept)
            from_beginning: Start from beginning of topic

        Returns:
            Parsed JSON message or None if timeout
        """
        try:
            offset = "--from-beginning" if from_beginning else ""
            cmd = [
                "docker", "exec", self.container,
                "kafka-console-consumer",
                "--bootstrap-server", "localhost:9092",
                "--topic", topic,
                "--max-messages", "100",  # Limit to avoid hanging
                offset,
                "--timeout-ms", str(timeout * 1000)
            ]

            logger.info(f"Consuming from topic '{topic}' (timeout: {timeout}s)")
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=timeout + 5  # Add buffer to subprocess timeout
            )

            if result.returncode != 0 and result.returncode != 1:  # Exit code 1 is timeout (normal)
                logger.warning(f"Kafka consumer failed with exit code {result.returncode}: {result.stderr}")
                return None

            # Parse messages
            for line in result.stdout.strip().split('\n'):
                if not line:
                    continue

                try:
                    message = json.loads(line)
                    logger.debug(f"Received message: {message}")

                    if filter_fn is None or filter_fn(message):
                        logger.info(f"Message matched filter from topic '{topic}'")
                        return message
                except json.JSONDecodeError:
                    logger.warning(f"Could not parse message as JSON: {line}")
                    continue

            logger.warning(f"Timeout: No matching message found in topic '{topic}'")
            return None

        except subprocess.TimeoutExpired:
            logger.warning(f"Subprocess timeout while consuming from '{topic}'")
            return None
        except Exception as e:
            logger.error(f"Error consuming from Kafka: {e}")
            return None

    def consume_all(self,
                    topic: str,
                    timeout: int = 5,
                    max_messages: int = 100) -> List[Dict[str, Any]]:
        """
        Consume all available messages from topic.

        Args:
            topic: Kafka topic name
            timeout: Max seconds to wait
            max_messages: Maximum messages to consume

        Returns:
            List of parsed JSON messages
        """
        messages = []

        try:
            cmd = [
                "docker", "exec", self.container,
                "kafka-console-consumer",
                "--bootstrap-server", "localhost:9092",
                "--topic", topic,
                "--from-beginning",
                "--max-messages", str(max_messages),
                "--timeout-ms", str(timeout * 1000)
            ]

            logger.info(f"Consuming all messages from topic '{topic}'")
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=timeout + 5
            )

            # Parse messages
            for line in result.stdout.strip().split('\n'):
                if not line:
                    continue

                try:
                    message = json.loads(line)
                    messages.append(message)
                except json.JSONDecodeError:
                    logger.warning(f"Could not parse message as JSON: {line}")
                    continue

            logger.info(f"Consumed {len(messages)} messages from topic '{topic}'")
            return messages

        except subprocess.TimeoutExpired:
            logger.warning(f"Subprocess timeout while consuming from '{topic}'")
            return messages
        except Exception as e:
            logger.error(f"Error consuming from Kafka: {e}")
            return messages

    def topic_exists(self, topic: str) -> bool:
        """Check if topic exists"""
        try:
            cmd = [
                "docker", "exec", self.container,
                "kafka-topics",
                "--bootstrap-server", "localhost:9092",
                "--list"
            ]

            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=5
            )

            exists = topic in result.stdout
            logger.info(f"Topic '{topic}' exists: {exists}")
            return exists

        except Exception as e:
            logger.error(f"Failed to check topic existence: {e}")
            return False
