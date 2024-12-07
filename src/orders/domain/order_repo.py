from abc import ABC, abstractmethod
from typing import List, Optional

from src.orders.domain.order import Order, OrderLog


class IOrderRepository(ABC):
    @abstractmethod
    def save(self, order: Order, is_commit: bool = True) -> Order:
        raise NotImplementedError

    @abstractmethod
    def get_by_id(self, order_id: int) -> Optional[Order]:
        raise NotImplementedError

    @abstractmethod
    def update(self, order: Order, is_commit: bool = True) -> None:
        raise NotImplementedError

    @abstractmethod
    def get_all(self, buyer_id: str) -> List[Order]:
        raise NotImplementedError


class IOrderLogRepository(ABC):
    @abstractmethod
    def save(self, order_log: OrderLog, is_commit: bool = True) -> None:
        raise NotImplementedError

    @abstractmethod
    def get_by_order_id(self, order_id: int) -> List[OrderLog]:
        raise NotImplementedError
