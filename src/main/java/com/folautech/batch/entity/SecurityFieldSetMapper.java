package com.folautech.batch.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

@Slf4j
public class SecurityFieldSetMapper implements FieldSetMapper<Security> {

    @Override
    public Security mapFieldSet(FieldSet fieldSet) throws BindException {

        if (fieldSet == null) {
            return null;
        }

        Security security = Security.builder()
                .id(Long.parseLong(fieldSet.readString("ID")))
                .ticker(fieldSet.readString("Ticker"))
                .name(fieldSet.readString("Name"))
                .description(fieldSet.readString("Description"))
                .type(SecurityType.valueOf(fieldSet.readString("Type"))).build();

        log.info("security={}", security.toString());

        return security;

    }
}
