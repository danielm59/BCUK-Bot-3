package com.expiredminotaur.bcukbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableCaching
@EnableScheduling
public class Application extends SpringBootServletInitializer
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)
    {
        removeConfigFolder();
        SpringApplication.run(Application.class, args);
    }

    private static void removeConfigFolder()
    {
        String directoryPath = "./config";
        File file = new File(directoryPath);

        if (!FileSystemUtils.deleteRecursively(file))
        {
            logger.warn("Problem occurs when deleting the directory : " + directoryPath);
        }
    }
}
