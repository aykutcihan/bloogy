package com.example.bloogy.payload.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ArticleRequest {

    @NotBlank(message = "Title is mandatory")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Content is mandatory")
    @Size(min = 20, max = 2000, message = "Content must be between 20 and 2000 characters")
    private String content;
}
