import os

import requests

from src.orders.domain.payments_repo import IPaymentRepository


class MockPaymentRepository(IPaymentRepository):
    def __init__(self):
        self.payments = {}

    def create_vbank(self, order_id, amount, vbank_code="004", vbank_due=3):
        if order_id in self.payments:
            raise ValueError("Merchant UID already exists")
        self.payments[order_id] = {
            "merchant_uid": order_id,
            "amount": amount,
            "vbank_code": vbank_code,
            "vbank_due": vbank_due,
            "status": "ready",
        }
        return self.payments[order_id]

    def confirm_payment(self, order_id):
        payment = self.payments.get(order_id)
        if not payment:
            raise ValueError("Payment not found")
        payment["status"] = "paid"
        return payment

    def cancel_payment(self, order_id):
        payment = self.payments.get(order_id)
        if not payment:
            raise ValueError("Cannot cancel")
        payment["status"] = "cancelled"
        return payment


class IamportPaymentGateway(IPaymentRepository):
    def __init__(self):
        self.base_url = os.getenv("IMP_BASE_URL")
        self.imp_key = os.getenv("IMP_KEY")
        self.imp_secret = os.getenv("IMP_SECRET")
        self.access_token = self._get_access_token()

    def _get_access_token(self):
        url = f"{self.base_url}/users/getToken"
        payload = {"imp_key": self.imp_key, "imp_secret": self.imp_secret}
        response = requests.post(url, json=payload)
        response.raise_for_status()
        return response.json()["response"]["access_token"]

    def create_vbank(self, order_id, amount, vbank_code="004", vbank_due=3):
        url = f"{self.base_url}/vbanks"
        headers = {"Authorization": f"Bearer {self.access_token}"}
        payload = {
            "merchant_uid": order_id,
            "amount": amount,
            "vbank_code": vbank_code,
            "vbank_due": vbank_due,
        }
        response = requests.post(url, json=payload, headers=headers)
        response.raise_for_status()
        return response.json()["response"]

    def confirm_payment(self, imp_uid):
        url = f"{self.base_url}/payments/{imp_uid}"
        headers = {"Authorization": f"Bearer {self.access_token}"}
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        payment = response.json()["response"]
        if payment["status"] != "paid":
            raise ValueError(f"Payment is not completed: {payment['status']}")
        return payment

    def cancel_payment(self, order_id):
        url = f"{self.base_url}/payments/cancel"
        headers = {"Authorization": f"Bearer {self.access_token}"}
        payload = {"order_id": order_id}
        response = requests.post(url, json=payload, headers=headers)
        response.raise_for_status()
        return response.json()["response"]
