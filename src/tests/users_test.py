import pytest
from fastapi.testclient import TestClient

from src.app import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_create_user():
    response = client.post(
        "/users",
        json={
            "name": "testuser",
            "email": "testuser@ggamil.com",
            "password": "testpassword",
        },
    )
    assert (
        response.status_code == 201 or response.status_code == 422
    )  # 422 if user already exists


#
def test_login():
    response = client.post(
        "/users/login",
        data={"username": "testuser@ggamil.com", "password": "testpassword"},
    )
    assert response.status_code == 200
    assert "access_token" in response.json()
    assert response.json()["token_type"] == "bearer"


#
# def test_access_protected_route_without_token():
#     response = client.get("/orders")
#     assert response.status_code == 401
