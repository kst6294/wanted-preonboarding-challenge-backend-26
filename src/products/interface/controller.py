from dependency_injector.wiring import Provide, inject
from fastapi import APIRouter, HTTPException, Response
from fastapi.params import Depends
from fastapi_utils.cbv import cbv

from src.common.auth import CurrentUser, get_current_user
from src.containers import ProductContainer
from src.products.application.interface import IProductService
from src.products.interface.dto import CreateProductBody

router = APIRouter(prefix="/products", tags=["products"])


@cbv(router)
class ProductsController:
    @inject
    def __init__(
        self,
        product_service: IProductService = Depends(
            Provide[ProductContainer.product_service]
        ),
    ):
        self.product_service = product_service

    @router.post("/", status_code=201)
    def create_product(
        self,
        product: CreateProductBody,
        current_user: CurrentUser = Depends(get_current_user),
    ):
        try:
            self.product_service.create_product(
                product.name, product.price, product.quantity, current_user.user_id
            )
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
        return {"message": "success"}

    @router.get("/")
    def get_products(self, page: int = None, item_per_page: int = None):
        return self.product_service.get_products(page, item_per_page)

    @router.get("/{product_id}")
    def get_product(self, product_id: int):
        return self.product_service.get_product(product_id)
