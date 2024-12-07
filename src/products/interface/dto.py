from pydantic import BaseModel, EmailStr, Field


class CreateProductBody(BaseModel):
    name: str = Field(min_length=2, max_length=32)
    price: int = Field(ge=0)
    quantity: int = Field(ge=0)
