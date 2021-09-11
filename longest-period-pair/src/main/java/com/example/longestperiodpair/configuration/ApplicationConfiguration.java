package com.example.longestperiodpair.configuration;

import com.example.longestperiodpair.application.ApplicationBeanInterface;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Application configuration.
 */
@Configuration
@ComponentScan(basePackageClasses = ApplicationBeanInterface.class)
public class ApplicationConfiguration {
}
