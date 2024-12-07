from typing import List, Tuple

from fastapi import HTTPException
from sqlalchemy.orm import Session

from src.common.db_utils import row_to_dict
from src.orders.domain.order import Order as OrderDto
from src.orders.domain.order_repo import IOrderRepository
from src.orders.infra.db_models.order import Order


class OrderRepository(IOrderRepository):
    def __init__(self, db: Session):
        self.db = db

    def save(self, order: OrderDto, is_commit=True):
        try:
            new_order = Order(
                product_id=order.product_id,
                quantity=order.quantity,
                price=order.price,
                buyer_id=order.buyer_id,
                status=order.status,
                created_at=order.created_at,
                updated_at=order.updated_at,
                vbank=order.vbank,
            )
            self.db.add(new_order)
            self.db.flush()
            if is_commit:
                self.db.commit()
            return new_order
        except Exception as e:
            self.db.rollback()
            raise e

    def get_by_id(self, order_id) -> OrderDto:
        order = self.db.query(Order).filter(order_id == Order.id).first()

        if not order:
            raise HTTPException(status_code=422)

        return OrderDto(**row_to_dict(order))

    def update(self, order: OrderDto, is_commit=True):
        try:
            self.db.query(Order).filter(order.id == Order.id).update(
                {
                    "status": order.status,
                }
            )
            if is_commit:
                self.db.commit()
        except Exception as e:
            self.db.rollback()
            raise e

    def get_all(self, buyer_id: str) -> List[OrderDto]:
        orders = self.db.query(Order).filter(buyer_id == Order.buyer_id).all()

        return [OrderDto(**row_to_dict(order)) for order in orders]
