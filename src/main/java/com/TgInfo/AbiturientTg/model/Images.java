package com.TgInfo.AbiturientTg.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum Images {

    SGGK("СГГК", new ClassPathResource("./static/docs/images/IF9OTnhiHP0.jpg")),
    PHOTO_1("Зал", new ClassPathResource("./static/docs/images/зал.jpg")),
    PHOTO_2("Зал 2", new ClassPathResource("./static/docs/images/зал2.jpg")),
    PHOTO_3("Зал 3", new ClassPathResource("./static/docs/images/зал3.jpg")),
    PHOTO_4("Комната", new ClassPathResource("./static/docs/images/комната.jpg"));

    String name;
    ClassPathResource resource;

    @SneakyThrows(IOException.class)
    public InputStream file() {
        return this.getResource().getInputStream();
    }
}
