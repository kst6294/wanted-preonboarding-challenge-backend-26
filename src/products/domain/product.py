from dataclasses import dataclass, field
from datetime import datetime, timezone
from typing import Optional

from strenum import StrEnum


class ProductStatus(StrEnum):
    SELLING = "판매중"
    RESERVED = "예약중"
    COMPLETED = "완료"
    CANCELLED = "취소"


@dataclass
class Product:
    name: str
    price: int
    quantity: int
    status: ProductStatus
    seller_id: str
    created_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    updated_at: datetime = field(default_factory=lambda: datetime.now(timezone.utc))
    id: Optional[int] = None
