from src.orders.interface.controller import router as order_router
from src.products.interface.controller import router as product_router
from src.users.interface.controller import router as user_router


def set_router(app):
    app.include_router(user_router)
    app.include_router(product_router)
    app.include_router(order_router)

    return app
