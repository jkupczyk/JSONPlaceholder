package com.placeholder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.placeholder.dto.PostDto;
import com.placeholder.exception.PostException;

import reactor.core.publisher.Mono;

@Service
public class PostService
{
    private final WebClient webClient;
    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";

    @Autowired
    public PostService(final WebClient webClient)
    {
        this.webClient = webClient;
    }

    public PostDto[] getAllPosts()
    {
        return webClient.get()
                .uri(POSTS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PostDto[].class)
                .onErrorResume(throwable -> Mono.error(new PostException("Unexpected error while consuming posts request")))
                .block();
    }
}
