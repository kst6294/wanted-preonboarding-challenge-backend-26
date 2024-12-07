from typing import List

from fastapi import HTTPException
from sqlalchemy.orm import Session

from src.common.db_utils import row_to_dict
from src.orders.domain.order import OrderLog as OrderLogDto
from src.orders.domain.order_repo import IOrderLogRepository
from src.orders.infra.db_models.order_log import OrderLog


class OrderLogRepository(IOrderLogRepository):
    def __init__(self, db: Session):
        self.db = db

    def save(self, order: OrderLogDto, is_commit=True):
        try:
            new_order = OrderLog(
                order_id=order.order_id,
                status=order.status,
                created_at=order.created_at,
            )
            self.db.add(new_order)
            if is_commit:
                self.db.commit()
        except Exception as e:
            self.db.rollback()
            raise e

    def get_by_order_id(self, order_id) -> List[OrderLog]:
        orders = self.db.query(OrderLog).filter(order_id == OrderLog.order_id).all()

        if not orders:
            raise HTTPException(status_code=422)

        return [OrderLogDto(**row_to_dict(order)) for order in orders]
