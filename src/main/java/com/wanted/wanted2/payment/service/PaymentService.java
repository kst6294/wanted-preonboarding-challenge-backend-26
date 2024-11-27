package com.wanted.wanted2.payment.service;

import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.payment.model.PaymentDto;
import com.wanted.wanted2.payment.model.PaymentEntity;
import com.wanted.wanted2.users.model.UserDetail;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    ResponseEntity<?> findByUser(UserDetail userDetail);

    ResponseEntity<PaymentEntity> findById(UserDetail userDetail, String id);

    ResponseEntity<PaymentEntity> save(UserDetail userDetail, PaymentDto order);
}
