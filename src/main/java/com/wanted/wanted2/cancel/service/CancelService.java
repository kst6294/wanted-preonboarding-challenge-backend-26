package com.wanted.wanted2.cancel.service;

import com.wanted.wanted2.cancel.model.CancelDto;
import com.wanted.wanted2.cancel.model.CancelEntity;
import com.wanted.wanted2.users.model.UserDetail;
import org.springframework.http.ResponseEntity;

public interface CancelService {

    ResponseEntity<CancelEntity> save(UserDetail userDetail, CancelDto cancel);
}
