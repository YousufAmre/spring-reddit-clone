package com.example.springredditclone.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springredditclone.dto.CommentsDto;
import com.example.springredditclone.exceptions.PostNotFoundException;
import com.example.springredditclone.mapper.CommentMapper;
import com.example.springredditclone.model.Comment;
import com.example.springredditclone.model.NotificationEmail;
import com.example.springredditclone.model.Post;
import com.example.springredditclone.model.User;
import com.example.springredditclone.repository.CommentRepository;
import com.example.springredditclone.repository.PostRepository;
import com.example.springredditclone.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

	private static final String POST_URL = "";
	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final CommentMapper commentMapper;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;
	
	public void save(CommentsDto commentsDto) {
		Post post = postRepository.findById(commentsDto.getPostId())
				.orElseThrow(()-> new PostNotFoundException(commentsDto.getPostId().toString()));
		
		Comment comment= commentMapper.map(commentsDto, post, authService.getCurrentUser());
		
		commentRepository.save(comment);
		
		String message = mailContentBuilder.build(authService.getCurrentUser() + " posted a comment on your post." + POST_URL);
        sendCommentNotification(message, post.getUser());
	}
	
	private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

	public List<CommentsDto> getAllCommentsForPost(Long postId) {
		
		Post post = postRepository.findById(postId).orElseThrow(()-> new PostNotFoundException(postId.toString()));
		return commentRepository.findByPost(post)
				.stream()
				.map(commentMapper::maptoDto)
				.collect(Collectors.toList());
		
	}

	public List<CommentsDto> getAllCommentsForUser(String username) {
		User user = userRepository.findByUsername(username)
						.orElseThrow(()-> new UsernameNotFoundException(username));
		
		return commentRepository.findAllByUser(user)
				.stream()
				.map(commentMapper::maptoDto)
				.collect(Collectors.toList());
		
	}
	
}