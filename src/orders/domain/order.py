from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Optional

from strenum import StrEnum

from src.products.domain.product import ProductStatus


@dataclass
class Order:
    product_id: int
    buyer_id: str
    quantity: int
    price: int
    status: ProductStatus
    created_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    vbank: Optional[dict] = None
    id: Optional[int] = None


@dataclass
class OrderLog:
    order_id: int
    status: ProductStatus
    created_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    id: Optional[int] = None
