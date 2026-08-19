package com.spring.review.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.spring.review.entityView.AuthUserView;
import com.spring.review.entityView.AuditLogView;
import com.spring.review.entityView.DepartmentView;
import com.spring.review.entityView.EmployeeView;
import com.spring.review.entityView.PositionView;
import com.spring.review.entityView.UserView;
import com.spring.review.entityView.WebhookLogView;
import com.spring.review.entityView.WebhookSubscriptionView;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlazeConfig {

    @Bean
    public CriteriaBuilderFactory criteriaBuilderFactory(
            EntityManagerFactory entityManagerFactory
    ) {

        CriteriaBuilderConfiguration config =
                Criteria.getDefault();

        return config.createCriteriaBuilderFactory(
                entityManagerFactory
        );
    }

    @Bean
    public EntityViewManager entityViewManager(
            CriteriaBuilderFactory cbf
    ) {

        EntityViewConfiguration config =
                EntityViews.createDefaultConfiguration();

        config.addEntityView(UserView.class);
        config.addEntityView(AuthUserView.class);
        config.addEntityView(EmployeeView.class);
        config.addEntityView(DepartmentView.class);
        config.addEntityView(PositionView.class);
        config.addEntityView(AuditLogView.class);
        config.addEntityView(WebhookSubscriptionView.class);
        config.addEntityView(WebhookLogView.class);

        return config.createEntityViewManager(cbf);
    }
}
