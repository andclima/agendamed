package br.com.fiap.mshistorico.config;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class LocalDateTimeScalar {

    @Bean
    public GraphQLScalarType localDateTimeScalarType() {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Scalar para Java LocalDateTime (sem offset)")
                .coercing(new Coercing<LocalDateTime, String>() {

                    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                    @Override
                    public String serialize(Object dataFetcherResult) {
                        if (dataFetcherResult instanceof LocalDateTime dateTime) {
                            return dateTime.format(formatter);
                        }
                        throw new CoercingSerializeException("Esperado LocalDateTime, recebido: " + dataFetcherResult.getClass().getSimpleName());
                    }

                    @Override
                    public LocalDateTime parseValue(Object input) {
                        if (input instanceof String str) {
                            try {
                                return LocalDateTime.parse(str, formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseValueException("Formato inválido. Use: yyyy-MM-ddTHH:mm:ss");
                            }
                        }
                        throw new CoercingParseValueException("Esperado String ISO-8601");
                    }

                    @Override
                    public LocalDateTime parseLiteral(Object input) {
                        if (input instanceof StringValue strValue) {
                            try {
                                return LocalDateTime.parse(strValue.getValue(), formatter);
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException("Formato inválido no literal");
                            }
                        }
                        throw new CoercingParseLiteralException("Esperado StringValue literal");
                    }
                })
                .build();
    }
}