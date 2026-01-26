package com.bmcho.springbootmongodb.config;

import org.bson.types.Decimal128;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

//    public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
//        @Override
//        public BigDecimal convert(Decimal128 source) {
//            return source == null ? null : source.bigDecimalValue();
//        }
//    }
//
//    public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {
//        @Override
//        public Decimal128 convert(BigDecimal source) {
//            return source == null ? null : new Decimal128(source);
//        }
//    }
//
//    @Bean
//    public MongoCustomConversions mongoCustomConversions() {
//        return new MongoCustomConversions(List.of(
//                new Decimal128ToBigDecimalConverter(),
//                new BigDecimalToDecimal128Converter()
//        ));
//    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoCustomConversions conversions
    ) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);

        MappingMongoConverter converter =
                new MappingMongoConverter(dbRefResolver, new MongoMappingContext());

        converter.setCustomConversions(conversions);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null)); //_class 제거

        return converter;
    }

    @Bean
    public MongoTemplate mongoTemplate(
            MongoDatabaseFactory factory,
            MappingMongoConverter converter
    ) {
        return new MongoTemplate(factory, converter);
    }
}
