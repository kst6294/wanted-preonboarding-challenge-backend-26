from random import randint

import pytest
from dns.e164 import query
from fastapi.testclient import TestClient

from src.app import app

client = TestClient(app)


def test_create_product():
    response = client.post(
        "/users/login",
        data={"username": "testuser@ggamil.com", "password": "testpassword"},
    )
    token = response.json()["access_token"]
    for i in range(10):
        product_no = randint(1000, 10000000)
        price = randint(-100, 100)
        quantity = randint(-100, 100)
        response = client.post(
            "/products",
            headers={"Authorization": f"Bearer {token}"},
            json={"name": f"product{product_no}", "price": price, "quantity": quantity},
        )
        if price > 0 and quantity > 0:
            assert response.status_code == 201
            assert response.json() == {"message": "success"}
        else:
            assert response.status_code == 400


def test_get_products():
    response = client.get("/products")
    assert response.status_code == 200


def test_get_product():
    response = client.get("/products/1")
    assert response.status_code == 200
    assert response.json()["id"] == 1


def test_non_member_create_product():
    response = client.post(
        "/products", json={"name": "product", "price": 100, "quantity": 10}
    )
    assert response.status_code == 401
