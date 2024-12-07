from abc import ABC, abstractmethod
from typing import List, Optional

from src.products.domain.product import Product


class IProductService(ABC):
    @abstractmethod
    def create_product(
        self, name: str, price: int, quantity: int, seller_id: str
    ) -> Product:
        raise NotImplementedError

    @abstractmethod
    def get_products(
        self, page: int = None, item_per_page: int = None
    ) -> List[Product]:
        raise NotImplementedError

    @abstractmethod
    def get_product(self, product_id: int) -> Optional[Product]:
        raise NotImplementedError

    @abstractmethod
    def update_product(
        self,
        product_id: int,
        name: str = None,
        price: int = None,
        quantity: int = None,
        status: str = None,
        is_commit: bool = True,
    ) -> Product:
        raise NotImplementedError
