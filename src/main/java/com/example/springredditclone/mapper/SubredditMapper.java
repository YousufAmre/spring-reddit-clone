package com.example.springredditclone.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.springredditclone.dto.SubredditDto;
import com.example.springredditclone.model.Post;
import com.example.springredditclone.model.Subreddit;
import com.example.springredditclone.model.User;



@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdDate", expression ="java(java.time.Instant.now())")
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto, User user);
}