from datetime import datetime, timezone
from typing import List, Optional

from src.products.application.interface import IProductService
from src.products.domain.product import Product, ProductStatus
from src.products.domain.product_repo import IProductRepository


class ProductService(IProductService):
    def __init__(self, product_repo: IProductRepository):
        self.product_repo = product_repo

    def create_product(
        self,
        name: str,
        price: int,
        quantity: int,
        seller_id: str,
    ) -> Product:
        if price <= 0:
            raise ValueError("Price must be greater than 0")

        if quantity <= 0:
            raise ValueError("Quantity must be greater than 0")

        product = Product(
            name=name,
            price=price,
            quantity=quantity,
            status=ProductStatus.SELLING,
            seller_id=seller_id,
            created_at=datetime.now(timezone.utc),
            updated_at=datetime.now(timezone.utc),
        )
        self.product_repo.save(product)
        return product

    def get_products(
        self, page: int = None, item_per_page: int = None
    ) -> List[Product]:
        return self.product_repo.get_all(page, item_per_page)

    def get_product(self, product_id: int) -> Optional[Product]:
        return self.product_repo.get_by_id(product_id)

    def update_product(
        self,
        product_id: int,
        name: str = None,
        price: int = None,
        quantity: int = None,
        status: str = None,
        is_commit: bool = True,
    ) -> Product:
        product = self.product_repo.get_by_id(product_id)
        if name is not None:
            product.name = name
        if price is not None:
            product.price = price
        if quantity is not None:
            product.quantity = quantity
        if status is not None:
            product.status = status
        product.updated_at = datetime.now(timezone.utc)

        self.product_repo.update(product, is_commit)

        return product
