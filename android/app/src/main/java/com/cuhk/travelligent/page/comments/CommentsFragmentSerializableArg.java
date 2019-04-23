package com.cuhk.travelligent.page.comments;

import io.swagger.client.models.CommentDto;

import java.io.Serializable;
import java.util.List;

public class CommentsFragmentSerializableArg implements Serializable {

    public List<CommentDto> comments;

    public CommentsFragmentSerializableArg(List<CommentDto> comments) {
        this.comments = comments;
    }

}

