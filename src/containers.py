from dependency_injector import containers, providers

from src.database import get_db
from src.orders.application.service import OrderService
from src.orders.infra.order_log_repo import OrderLogRepository
from src.orders.infra.order_repo import OrderRepository
from src.orders.infra.payments_repo import (IamportPaymentGateway,
                                            MockPaymentRepository)
from src.products.application.service import ProductService
from src.products.infra.product_repo import ProductRepository
from src.users.application.service import UserService
from src.users.infra.user_repo import UserRepository


class UserContainer(containers.DeclarativeContainer):
    wiring_config = containers.WiringConfiguration(packages=["src.users"])

    db = providers.Resource(get_db)
    user_repo = providers.Factory(UserRepository, db=db)
    user_service = providers.Factory(UserService, user_repo=user_repo)


class ProductContainer(containers.DeclarativeContainer):
    wiring_config = containers.WiringConfiguration(packages=["src.products"])

    db = providers.Resource(get_db)
    product_repo = providers.Factory(ProductRepository, db=db)
    product_service = providers.Factory(ProductService, product_repo=product_repo)


class OrderContainer(containers.DeclarativeContainer):
    wiring_config = containers.WiringConfiguration(packages=["src.orders"])

    db = providers.Resource(get_db)
    order_repo = providers.Factory(OrderRepository, db=db)
    order_log_repo = providers.Factory(OrderLogRepository, db=db)
    payments_repo = providers.Factory(MockPaymentRepository)
    # payments_repo = providers.Factory(IamportPaymentGateway)

    product_service = providers.Dependency()

    order_service = providers.Factory(
        OrderService,
        order_repo=order_repo,
        order_log_repo=order_log_repo,
        payments_repo=payments_repo,
        product_service=product_service,
    )


class AppContainer(containers.DeclarativeContainer):
    user_container = providers.Container(UserContainer)
    product_container = providers.Container(ProductContainer)
    order_container = providers.Container(
        OrderContainer, product_service=product_container.product_service
    )
