from typing import List, Tuple

from fastapi import HTTPException
from sqlalchemy.orm import Session

from src.common.db_utils import row_to_dict
from src.users.domain.user import User as UserDto
from src.users.domain.user_repo import IUserRepository
from src.users.infra.db_models.user import User


class UserRepository(IUserRepository):
    def __init__(self, db: Session):
        self.db = db

    def save(self, user: UserDto):
        new_user = User(
            id=user.id,
            name=user.name,
            email=user.email,
            password=user.password,
            created_at=user.created_at,
            updated_at=user.updated_at,
        )
        self.db.add(new_user)
        self.db.commit()

    def get_by_id(self, user_id) -> UserDto:
        user = self.db.query(User).filter(User.id == user_id).first()

        if not user:
            raise HTTPException(status_code=422)

        return UserDto(**row_to_dict(user))

    def get_users(self, page: int, item_per_page: int) -> Tuple[int, List[UserDto]]:
        query = self.db.query(User)
        total_count = query.count()
        if page and item_per_page:
            user = query.offset((page - 1) * item_per_page).limit(item_per_page).all()
        else:
            user = query.all()

        return total_count, [UserDto(**row_to_dict(user)) for user in user]

    def get_user_id_by_email(self, email) -> str:
        user = self.db.query(User).filter(User.email == email).first()

        if not user:
            raise HTTPException(status_code=422)

        return UserDto(**row_to_dict(user)).id

    def update(self, user: UserDto):
        self.db.query(User).filter(user.id == User.id).update(
            {
                "name": user.name,
                "email": user.email,
                "password": user.password,
                "updated_at": user.updated_at,
            }
        )
        self.db.commit()

    def delete(self, user_id):
        self.db.query(User).filter(User.id == user_id).delete()
        self.db.commit()
