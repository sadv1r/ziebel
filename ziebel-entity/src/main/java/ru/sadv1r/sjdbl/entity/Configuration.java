package ru.sadv1r.sjdbl.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
@Setter(AccessLevel.NONE)
public class Configuration {
    private final Properties properties = new Properties();

    private final String url;
    private final String login;
    private final String pwd;
    private final String lang;

    private final SessionFactoryOptions.SessionFactoryOptionsBuilder sessionFactoryOptionsBuilder;

    public Configuration(final String propFileName) throws IOException {
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
        }

        url = properties.getProperty("url");
        login = properties.getProperty("login");
        pwd = properties.getProperty("pwd");
        lang = properties.getProperty("lang");

        sessionFactoryOptionsBuilder = SessionFactoryOptions.builder()
                .sessionFactoryName(properties.getProperty("name", null))
                .cachingEnabled(Boolean.parseBoolean(properties.getProperty("cache.use_second_level_cache", "FALSE")));
    }

    public SessionFactory buildSessionFactory() {
        return new SessionFactory(getUrl(), getLogin(), getPwd(), getLang(), sessionFactoryOptionsBuilder.build());
    }
}