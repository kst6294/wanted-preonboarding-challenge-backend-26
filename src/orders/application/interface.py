from abc import ABC, abstractmethod
from typing import List, Optional

from src.orders.domain.order import Order, OrderLog


class IOrderService(ABC):
    @abstractmethod
    def reserve_order(self, product_id: int, buyer_id: str, quantity: int) -> Order:
        raise NotImplementedError

    @abstractmethod
    def complete_order(self, order_id: int, seller_id: str) -> Order:
        raise NotImplementedError

    @abstractmethod
    def cancel_order(self, order_id: int, seller_id: str) -> Order:
        raise NotImplementedError

    @abstractmethod
    def get_order(self, order_id: int, buyer_id: str) -> Optional[Order]:
        raise NotImplementedError

    @abstractmethod
    def get_order_log(self, order_id: int) -> List[OrderLog]:
        raise NotImplementedError

    @abstractmethod
    def get_orders(self, buyer_id: str) -> List[Order]:
        raise NotImplementedError
