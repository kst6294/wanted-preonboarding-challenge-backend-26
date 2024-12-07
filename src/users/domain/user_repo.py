from abc import ABCMeta, abstractmethod

from src.users.domain.user import User


class IUserRepository(metaclass=ABCMeta):
    @abstractmethod
    def save(self, user: User):
        raise NotImplementedError

    @abstractmethod
    def get_by_id(self, user_id) -> User:
        raise NotImplementedError

    @abstractmethod
    def get_users(self, page: int, item_per_page: int) -> list[User]:
        raise NotImplementedError

    @abstractmethod
    def get_user_id_by_email(self, email) -> str:
        raise NotImplementedError

    @abstractmethod
    def update(self, user: User):
        raise NotImplementedError

    @abstractmethod
    def delete(self, user_id):
        raise NotImplementedError
