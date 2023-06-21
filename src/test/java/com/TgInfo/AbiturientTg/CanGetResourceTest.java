package com.TgInfo.AbiturientTg;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

public class CanGetResourceTest {

    @SneakyThrows
    @Test
    public void resourceTest() {
        String pathToResource = "./test/комната.jpg";
        ClassPathResource classPathResource = new ClassPathResource(pathToResource);
        Assert.notNull(classPathResource.getFile(), "?");
    }
}
