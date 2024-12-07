from abc import ABC, abstractmethod
from typing import List

from src.products.domain.product import Product


class IProductRepository(ABC):
    @abstractmethod
    def save(self, product: Product, is_commit=True):
        raise NotImplementedError

    @abstractmethod
    def get_by_id(self, product_id) -> Product:
        raise NotImplementedError

    @abstractmethod
    def get_all(self, page: int = None, item_per_page: int = None) -> List[Product]:
        raise NotImplementedError

    @abstractmethod
    def update(self, product: Product, is_commit=True):
        raise NotImplementedError

    @abstractmethod
    def delete(self, product_id, is_commit=True):
        raise NotImplementedError
