package br.com.fiap.mshistorico.config;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType localDateTimeScalarType) {
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalarType);
    }
}