from pydantic import BaseModel, EmailStr, Field


class CreateUserBody(BaseModel):
    name: str = Field(min_length=2, max_length=32)
    email: EmailStr = Field(max_length=64)
    password: str = Field(min_length=2, max_length=64)


class UpdateUserBody(BaseModel):
    name: str or None = Field(min_length=2, max_length=32, default=None)
    email: EmailStr or None = Field(max_length=64, default=None)
    password: str or None = Field(min_length=2, max_length=64, default=None)


class UserResponse(BaseModel):
    id: str
    name: str
    email: EmailStr
    created_at: str
    updated_at: str


class UsersResponse(BaseModel):
    users: list[UserResponse]
    total_count: int
    page: int or None
    item_per_page: int or None
