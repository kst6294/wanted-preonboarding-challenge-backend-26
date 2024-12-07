from typing import List

from dependency_injector.wiring import Provide, inject
from fastapi import APIRouter, HTTPException, status
from fastapi.params import Depends
from fastapi_utils.cbv import cbv

from src.common.auth import CurrentUser, get_current_user
from src.containers import OrderContainer
from src.orders.application.interface import IOrderService
from src.orders.interface.dto import (
    ReserveOrderBody,
    ReserveOrderResponse,
    OrderLogResponse,
    OrderResponse,
)

router = APIRouter(prefix="/orders", tags=["orders"])


@cbv(router)
class OrderController:
    @inject
    def __init__(
        self,
        order_service: IOrderService = Depends(Provide[OrderContainer.order_service]),
    ):
        self.order_service = order_service

    @router.post("/", response_model=ReserveOrderResponse)
    def reserve_order(
        self,
        order: ReserveOrderBody,
        current_user: CurrentUser = Depends(get_current_user),
    ):
        try:
            order =  self.order_service.reserve_order(
                product_id=order.product_id,
                buyer_id=current_user.user_id,
                quantity=order.quantity,
            )
            return order
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))

    @router.get("/", response_model=List[OrderResponse])
    def get_orders(self, current_user: CurrentUser = Depends(get_current_user)):
        return self.order_service.get_orders(current_user.user_id)

    @router.post("/{order_id}/complete", response_model=OrderResponse)
    def complete_order(
        self, order_id: int, current_user: CurrentUser = Depends(get_current_user)
    ):
        return self.order_service.complete_order(order_id, current_user.user_id)


    @router.post("/{order_id}/cancel", response_model=OrderResponse)
    def cancel_order(
        self, order_id: int, current_user: CurrentUser = Depends(get_current_user)
    ):
        return self.order_service.cancel_order(order_id, current_user.user_id)

    @router.get("/{order_id}", response_model=OrderResponse)
    def get_order(
        self, order_id: int, current_user: CurrentUser = Depends(get_current_user)
    ):
        return self.order_service.get_order(order_id, current_user.user_id)

    @router.get("/{order_id}/log", response_model=List[OrderLogResponse])
    def get_order_log(self, order_id: int):
        return self.order_service.get_order_log(order_id)
