package com.spring.review.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.spring.review.entityView.AuthUserView;
import com.spring.review.entityView.UserView;
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

        return config.createEntityViewManager(cbf);
    }
}