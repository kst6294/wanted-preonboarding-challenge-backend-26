from typing import List, Tuple

from fastapi import HTTPException
from sqlalchemy.orm import Session

from src.common.db_utils import row_to_dict
from src.products.domain.product import Product as ProductDto
from src.products.domain.product_repo import IProductRepository
from src.products.infra.db_models.product import Product


class ProductRepository(IProductRepository):
    def __init__(self, db: Session):
        self.db = db

    def save(self, product: ProductDto, is_commit=True):
        try:
            new_product = Product(
                id=product.id,
                name=product.name,
                price=product.price,
                quantity=product.quantity,
                status=product.status,
                seller_id=product.seller_id,
                created_at=product.created_at,
                updated_at=product.updated_at,
            )
            self.db.add(new_product)
            if is_commit:
                self.db.commit()
        except Exception as e:
            self.db.rollback()
            raise e

    def get_by_id(self, product_id) -> ProductDto:
        product = self.db.query(Product).filter(Product.id == product_id).first()

        if not product:
            raise HTTPException(status_code=422)

        return ProductDto(**row_to_dict(product))

    def get_all(
        self, page: int = None, item_per_page: int = None
    ) -> Tuple[int, List[ProductDto]]:
        query = self.db.query(Product)
        total_count = query.count()
        if page and item_per_page:
            product = (
                query.offset((page - 1) * item_per_page).limit(item_per_page).all()
            )
        else:
            product = query.all()

        return total_count, [ProductDto(**row_to_dict(product)) for product in product]

    def update(self, product: ProductDto, is_commit=True):
        try:
            self.db.query(Product).filter(product.id == Product.id).update(
                {
                    "name": product.name,
                    "price": product.price,
                    "quantity": product.quantity,
                    "updated_at": product.updated_at,
                    "status": product.status,
                }
            )
            if is_commit:
                self.db.commit()
        except Exception as e:
            self.db.rollback()
            raise e

    def delete(self, product_id, is_commit=True):
        try:
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if product:
                self.db.delete(product)
                if is_commit:
                    self.db.commit()

            return product
        except Exception as e:
            self.db.rollback()
            raise e
