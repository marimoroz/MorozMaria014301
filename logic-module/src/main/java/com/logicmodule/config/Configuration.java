package com.logicmodule.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.datamodule",
        "com.logicmodule","com.file","com.security","com.logicmodule.mappers"})
public class Configuration {
}
