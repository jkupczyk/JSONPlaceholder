package com.placeholder;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placeholder.dto.PostDto;
import com.placeholder.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommandLineApplicationTest
{
    @InjectMocks
    private CommandLineApplication commandLineApplication;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PostService postService;

    @Test
    void shouldWriteAllPostsToJsonFiles() throws IOException
    {
        //given
        final PostDto postDto = PostDto.builder()
                .id(1)
                .userId(1)
                .title("title")
                .body("body")
                .build();

        given(postService.getAllPosts()).willReturn(new PostDto[] { postDto });

        //when
        commandLineApplication.run();

        //then
        verify(objectMapper, times(1)).writeValue(any(File.class), refEq(postDto));
    }

    @Test
    void shouldNeverWriteWhenGetAllPostsReturnEmptyList() throws IOException
    {
        given(postService.getAllPosts()).willReturn(new PostDto[] {});

        //when
        commandLineApplication.run();

        //then
        verify(objectMapper, never()).writeValue(any(File.class), any(PostDto.class));
    }
}