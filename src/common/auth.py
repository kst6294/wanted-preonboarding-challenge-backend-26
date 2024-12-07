from datetime import datetime, timedelta, timezone
from typing import Annotated

from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from jose import JWTError, jwt
from pydantic.dataclasses import dataclass
from strenum import StrEnum

SECRET_KEY = "SECRET _KEY"
ALGORITHMS = "HS256"
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/users/login")


class Role(StrEnum):
    ADMIN = "ADMIN"
    USER = "USER"


@dataclass
class CurrentUser:
    user_id: str
    role: Role


def create_access_token(
    payload: dict, role: Role, expires_delta: timedelta = timedelta(minutes=30)
):
    expire = datetime.now(tz=timezone.utc) + expires_delta
    payload.update({"role": role, "exp": expire})
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHMS)


def decode_access_token(token: str):
    try:
        return jwt.decode(token, SECRET_KEY, algorithms=ALGORITHMS)
    except JWTError:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED)


def get_current_user(token: Annotated[str, Depends(oauth2_scheme)]):
    payload = decode_access_token(token)
    user_id = payload.get("user_id")
    role = payload.get("role")
    if not user_id or not role or role != Role.USER:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN)

    return CurrentUser(user_id=user_id, role=role)


def get_admin_user(token: Annotated[str, Depends(oauth2_scheme)]):
    payload = decode_access_token(token)
    role = payload.get("role")
    if not role or role != Role.ADMIN:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN)

    return CurrentUser(user_id="admin", role=role)
