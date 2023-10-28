package com.folautech.batch.entity;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class SecurityDAO {
    @Autowired
    SecurityRepository securityRepository;
    @Transactional
    public Security save(Security security){
        return securityRepository.saveAndFlush(security);
    }
}
