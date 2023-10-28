package com.folautech.batch.writer;

import com.folautech.batch.entity.Security;
import com.folautech.batch.entity.SecurityDAO;
import com.folautech.batch.entity.SecurityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class SecurityItemWriter implements ItemWriter<Security> {

    private SecurityDAO securityDAO;

    @Override
    public void write(Chunk<? extends Security> securities) throws Exception {
        log.info("write...");
        System.out.println("\n");

        for(Security security: securities){
            log.info("security={}", security.toString());

//            securityDAO.save(security);
        }
        System.out.println("\n");
    }

    public void setSecurityDAO(SecurityDAO securityDAO){
        this.securityDAO = securityDAO;
    }
}
