package com.placeholder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placeholder.dto.PostDto;
import com.placeholder.service.PostService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommandLineApplication implements CommandLineRunner
{
    private final PostService postService;
    private final ObjectMapper objectMapper;
    private final String basePath;

    @Autowired
    public CommandLineApplication(final PostService postService,
            final ObjectMapper objectMapper,
            final @Value("${post.path:posts}") String basePath)
    {
        this.postService = postService;
        this.objectMapper = objectMapper;
        this.basePath = basePath;
    }

    public void run(final String... args)
    {
        Arrays.stream(postService.getAllPosts())
                .parallel()
                .forEach(this::writePostAsJson);
    }

    private void writePostAsJson(final PostDto post)
    {
        final Integer id = post.getId();
        final Path path = Paths.get(basePath);
        try
        {
            Files.createDirectories(path);
            final File file = path.resolve(String.format("%d.json", id)).toFile();
            log.info("Writing post with id: {} to file.", id);
            objectMapper.writeValue(file, post);
        }
        catch (final IOException exception)
        {
            log.error("Error while writing to file. {}", exception);
        }
    }
}
