from typing import Annotated, Dict, List, Union

from dependency_injector.wiring import Provide, inject
from fastapi import APIRouter, Depends, Query
from fastapi.security import OAuth2PasswordRequestForm
from fastapi_utils.cbv import cbv

from src.common.auth import CurrentUser, get_admin_user, get_current_user
from src.containers import UserContainer
from src.users.application.interface import IUserService
from src.users.interface.dto import (CreateUserBody, UpdateUserBody,
                                     UserResponse, UsersResponse)

router = APIRouter(prefix="/users", tags=["users"])


@cbv(router)
class UserController:
    @inject
    def __init__(
        self, user_service: IUserService = Depends(Provide[UserContainer.user_service])
    ):
        self.user_service = user_service

    @router.post("/", status_code=201)
    def create_user(
        self,
        user: CreateUserBody,
    ):
        self.user_service.create_user(user.name, user.email, user.password)
        return {"message": "success"}

    @router.get("/", response_model=UsersResponse)
    def get_users(
        self,
        page: int = Query(None, description="The page number"),
        item_per_page: int = Query(None, description="The number of items in a page"),
        current_user: CurrentUser = Depends(get_admin_user),
    ):
        total_count, users = self.user_service.get_users(page, item_per_page)
        return {
            "total_count": total_count,
            "users": users,
            "page": page,
            "item_per_page": item_per_page,
        }

    @router.get("/user_id")
    def get_user_id(
        self, email: str = Query(..., description="The email of the users")
    ):
        user_id = self.user_service.get_user_id_by_email(email)
        return self.user_service.get_user(user_id).id

    @router.get("/{user_id}", response_model=UserResponse)
    def get_user(self, user_id: str):
        return self.user_service.get_user(user_id)

    @router.put("/")
    def update_user(
        self,
        current_user: Annotated[CurrentUser, Depends(get_current_user)],
        body: UpdateUserBody,
    ):
        self.user_service.update_user(
            user_id=current_user.user_id,
            name=body.name,
            email=body.email,
            password=body.password,
        )
        return {"message": "success"}

    @router.delete("/")
    def delete_user(
        self, current_user: Annotated[CurrentUser, Depends(get_current_user)]
    ):
        self.user_service.delete_user(current_user.user_id)
        return {"message": "success"}

    @router.post("/login")
    def login(
        self,
        form_data: Annotated[OAuth2PasswordRequestForm, Depends()],
    ):
        access_token = self.user_service.login(form_data.username, form_data.password)
        return {"access_token": access_token, "token_type": "bearer"}
