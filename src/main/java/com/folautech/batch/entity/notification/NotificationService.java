package com.folautech.batch.entity.notification;

import com.folautech.batch.entity.promotion.Promotion;
import com.folautech.batch.entity.user.User;

public interface NotificationService {

    Notification sendPromotionNotification(Notification notification);
}
