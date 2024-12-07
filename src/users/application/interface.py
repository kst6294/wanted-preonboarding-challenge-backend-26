from abc import ABC, abstractmethod
from typing import List

from src.users.domain.user import User


class IUserService(ABC):
    @abstractmethod
    def get_user(self, user_id: str) -> User:
        raise NotImplementedError

    @abstractmethod
    def get_users(self, page: int, item_per_page: int) -> List[User]:
        raise NotImplementedError

    @abstractmethod
    def create_user(self, name: str, email: str, password: str) -> User:
        raise NotImplementedError

    @abstractmethod
    def update_user(self, user_id: str, name: str, email: str, password: str) -> User:
        raise NotImplementedError

    @abstractmethod
    def get_user_id_by_email(self, email: str) -> str:
        raise NotImplementedError

    @abstractmethod
    def delete_user(self, user_id: str) -> None:
        raise NotImplementedError

    @abstractmethod
    def login(self, email: str, password: str) -> str:
        raise NotImplementedError
