from datetime import datetime, timezone
from typing import List, Optional

from src.orders.application.interface import IOrderService
from src.orders.domain.order import Order, OrderLog
from src.orders.domain.order_repo import IOrderLogRepository, IOrderRepository
from src.orders.domain.payments_repo import IPaymentRepository
from src.products.application.interface import IProductService
from src.products.domain.product import ProductStatus


class OrderService(IOrderService):
    def __init__(
        self,
        order_repo: IOrderRepository,
        order_log_repo: IOrderLogRepository,
        payments_repo: IPaymentRepository,
        product_service: IProductService,
    ):
        self.order_repo = order_repo
        self.order_log_repo = order_log_repo
        self.payments_repo = payments_repo
        self.product_service = product_service

    def reserve_order(
        self,
        product_id: int,
        buyer_id: str,
        quantity: int,
    ) -> Order:
        if quantity <= 0:
            raise ValueError("Quantity must be greater than 0")
        product = self.product_service.get_product(product_id)
        if product is None:
            raise ValueError("Product not found")
        if product.quantity < quantity:
            raise ValueError("Not enough stock")
        if product.quantity == quantity:
            self.product_service.update_product(
                product_id, status=ProductStatus.RESERVED, quantity=0, is_commit=True
            )
        else:
            self.product_service.update_product(
                product_id, status=ProductStatus.RESERVED, quantity=product.quantity - quantity, is_commit=True
            )

        init_status = ProductStatus.RESERVED

        order = Order(
            product_id=product_id,
            buyer_id=buyer_id,
            quantity=quantity,
            price=product.price,
            status=init_status,
            created_at=datetime.now(timezone.utc),
            updated_at=datetime.now(timezone.utc),
        )
        order = self.order_repo.save(order, is_commit=True)

        vbank = self.payments_repo.create_vbank(
            order_id=str(order.id),
            amount=product.price * quantity,
        )
        order.vbank = str(vbank)

        self.order_log_repo.save(
            OrderLog(
                order_id=order.id,
                status=init_status,
                created_at=datetime.now(timezone.utc),
            ),
            is_commit=True
        )


        return order

    def complete_order(self, order_id: int, seller_id: str) -> Order:
        order = self.order_repo.get_by_id(order_id)
        if order is None:
            raise ValueError("Order not found")

        product = self.product_service.get_product(order.product_id)
        if product is None:
            raise ValueError("Order or Product not found")

        if product.seller_id != seller_id:
            raise PermissionError("Invalid seller")

        if product is None:
            raise ValueError("Order or Product not found")

        vbank = self.payments_repo.confirm_payment(order.vbank["vbank_id"])

        product_status = product.status

        if product.status == ProductStatus.RESERVED:
            product_status = ProductStatus.COMPLETED

        self.product_service.update_product(
            order.product_id,
            status=product_status,
            quantity=product.quantity - order.quantity,
        )

        if order.status != ProductStatus.RESERVED:
            raise ValueError("Invalid order status")

        order.status = ProductStatus.COMPLETED
        order.updated_at = datetime.now(timezone.utc)

        self.order_repo.update(order)
        self.order_log_repo.save(
            OrderLog(
                order_id=order.id,
                status=ProductStatus.COMPLETED,
                created_at=datetime.now(timezone.utc),
            )
        )

        order.vbank = vbank

        return order

    def cancel_order(self, order_id, seller_id):
        order = self.order_repo.get_by_id(order_id)
        if order is None:
            raise ValueError("Order not found")
        product = self.product_service.get_product(order.product_id)
        if product is None:
            raise PermissionError("Invalid buyer")

        if product.seller_id != seller_id:
            raise ValueError("Invalid seller")

        quantity = 0
        if order.status == ProductStatus.COMPLETED:
            quantity = order.quantity

        self.product_service.update_product(
            order.product_id,
            status=ProductStatus.SELLING,
            quantity=product.quantity + quantity,
        )

        order.status = ProductStatus.CANCELLED
        order.updated_at = datetime.now(timezone.utc)

        self.order_repo.update(order)
        self.order_log_repo.save(
            OrderLog(
                order_id=order.id,
                status=ProductStatus.CANCELLED,
                created_at=datetime.now(timezone.utc),
            )
        )

        self.payments_repo.cancel_payment(order.vbank["vbank_id"])

        return order

    def get_order(self, order_id: int, buyer_id: str) -> Optional[Order]:
        order = self.order_repo.get_by_id(order_id)
        if order is None:
            return None
        if order.buyer_id != buyer_id:
            return None
        return order

    def get_orders(self, buyer_id: str) -> List[Order]:
        return self.order_repo.get_all(buyer_id)

    def get_order_log(self, order_id: int) -> List[OrderLog]:
        return self.order_log_repo.get_by_order_id(order_id)
