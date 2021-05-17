package com.placeholder.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.placeholder.dto.PostDto;
import com.placeholder.exception.PostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PostServiceTest
{
    private PostService postService;

    @Mock
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void init()
    {
        final WebClient webClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        postService = new PostService(webClient);
    }

    @Nested
    class GetAllPosts
    {
        @Test
        void shouldReturnExpectedPosts()
        {
            //given
            final PostDto postDto = PostDto.builder()
                    .id(1)
                    .userId(1)
                    .title("title")
                    .body("body")
                    .build();

            final String expectedBody = "[{"
                    + "\"userId\": 1,"
                    + "\"id\": 1,"
                    + "\"title\": \"title\","
                    + "\"body\": \"body\""
                    + "}]";

            given(exchangeFunction.exchange(any(ClientRequest.class)))
                    .willReturn(Mono.just(ClientResponse.create(HttpStatus.OK)
                            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(expectedBody)
                            .build()));

            //when
            final PostDto[] allPosts = postService.getAllPosts();

            //then
            assertThat(allPosts)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(postDto);
        }

        @Test
        void shouldReturnNullWhenExceptionThrown()
        {
            //given
            given(exchangeFunction.exchange(any(ClientRequest.class)))
                    .willReturn(Mono.error(new Exception()));

            //when
            final Throwable throwable = catchThrowable(() -> postService.getAllPosts());

            //then
            assertThat(throwable).isInstanceOf(PostException.class);
        }
    }
}