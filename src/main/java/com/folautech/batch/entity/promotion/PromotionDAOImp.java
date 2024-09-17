package com.folautech.batch.entity.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class PromotionDAOImp implements PromotionDAO{

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public Promotion save(Promotion promotion) {
        return promotionRepository.saveAndFlush(promotion);
    }
}
