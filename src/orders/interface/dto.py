from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field


class ReserveOrderBody(BaseModel):
    product_id: int = Field(ge=0)
    quantity: int = Field(ge=0)

class ReserveOrderResponse(BaseModel):
    id: int
    price: Optional[int]
    quantity: Optional[int]
    created_at: Optional[datetime]
    updated_at: Optional[datetime]
    vbank: Optional[str]

class OrderResponse(BaseModel):
    id: int
    # name: Optional[str]
    price: Optional[int]
    quantity: Optional[int]
    created_at: Optional[datetime]
    updated_at: Optional[datetime]
    vbank: Optional[str]



class OrderLogResponse(BaseModel):
    id: str
    order_id: str
    status: str
    created_at: datetime
