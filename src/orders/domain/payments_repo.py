from abc import ABC, abstractmethod


class IPaymentRepository(ABC):
    @abstractmethod
    def create_vbank(
        self, order_id: str, amount: int, vbank_code: str = "004", vbank_due: int = 3
    ) -> dict:
        raise NotImplementedError

    @abstractmethod
    def confirm_payment(self, order_id: str) -> dict:
        raise NotImplementedError

    @abstractmethod
    def cancel_payment(self, order_id: str) -> dict:
        raise NotImplementedError
