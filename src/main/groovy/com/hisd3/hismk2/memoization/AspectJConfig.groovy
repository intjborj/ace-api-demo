package com.hisd3.hismk2.memoization

import org.springframework.beans.factory.config.CustomScopeConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.support.SimpleThreadScope

@Configuration
@EnableAspectJAutoProxy
class AspectJConfig {

    @Bean
      MemoizerAspect memoizerAspect() {
        return new MemoizerAspect()
    }

    @Bean
    CustomScopeConfigurer customScope () {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer ();
        configurer.addScope("request",new SimpleThreadScope())
        return configurer
    }

}
