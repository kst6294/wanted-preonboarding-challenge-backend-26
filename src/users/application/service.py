from datetime import datetime, timezone

from fastapi import HTTPException, status
from ulid import ULID

from src.common.auth import Role, create_access_token
from src.common.crypto import Crypto
from src.users.application.interface import IUserService
from src.users.domain.user import User
from src.users.domain.user_repo import IUserRepository


class UserService(IUserService):
    def __init__(self, user_repo: IUserRepository):
        self.user_repo = user_repo
        self.ulid = ULID()
        self.crypto = Crypto()

    def create_user(self, name: str, email: str, password: str):
        if self.__is_exist_user(email):
            raise HTTPException(status_code=422, detail="Already Exist Email")

        now = datetime.now(tz=timezone.utc)
        user: User = User(
            id=self.ulid.generate(),
            name=name,
            email=email,
            password=self.crypto.encrypt(password),
            created_at=now,
            updated_at=now,
        )
        self.user_repo.save(user)

        return user

    def update_user(
        self,
        user_id: str,
        name: str or None = None,
        email: str or None = None,
        password: str or None = None,
    ):
        user = self.user_repo.get_by_id(user_id)
        if name:
            user.name = name
        if email:
            user.email = email
        if password:
            user.password = self.crypto.encrypt(password)
        user.updated_at = datetime.now(tz=timezone.utc)

        self.user_repo.update(user)

        return user

    def get_user(self, user_id: str):
        return self.user_repo.get_by_id(user_id)

    def get_users(self, page: int, item_per_page: int):
        return self.user_repo.get_users(page, item_per_page)

    def delete_user(self, user_id: str):
        return self.user_repo.delete(user_id)

    def get_user_id_by_email(self, email: str):
        return self.user_repo.get_user_id_by_email(email)

    def login(self, email: str, password: str):
        user_id = self.get_user_id_by_email(email)
        user = self.get_user(user_id)
        if not self.crypto.verify(password, user.password):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED, detail="Incorrect Password"
            )

        return create_access_token(payload={"user_id": user.id}, role=Role.USER)

    def __is_exist_user(self, email: str) -> bool:
        try:
            self.user_repo.get_user_id_by_email(email)
            return True
        except HTTPException as e:
            if e.status_code != 422:
                raise e
            return False
