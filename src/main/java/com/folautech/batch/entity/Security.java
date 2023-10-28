package com.folautech.batch.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@DynamicUpdate
@Entity
@Table(name = "securities")
public class Security implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long              id;

    @Column(name = "ticker")
    private String            ticker;

    @Column(name = "name")
    private String            name;

    @Column(name = "exchange")
    private String            exchange;

    @Column(name = "description", columnDefinition = "TEXT")
    private String            description;

    // hourly change rate in last 2 days
    @Column(name = "hourly_change_rate")
    private Double            hourlyChangeRate;

    @Column(name = "daily_change_rate")
    private Double            dailyChangeRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SecurityType      type;

    @ElementCollection
    @CollectionTable(name = "security_tags", joinColumns = @JoinColumn(name = "security_id"))
    @Column(name = "tag")
    private Set<String>       tags;

    @Column(name = "active", nullable = false)
    private boolean           active;

    @Column(name = "deleted", nullable = false)
    private boolean           deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime     createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime     updatedAt;

    public Security(Long id) {
        this.id = id;
    }

    public Security(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return ToStringBuilder.reflectionToString(this);
    }

    @PrePersist
    private void preCreate() {
    }

    @PreUpdate
    private void preUpdate() {
    }

    public void addTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return;
        }
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        this.tags.add(tag.toLowerCase());
    }

}
