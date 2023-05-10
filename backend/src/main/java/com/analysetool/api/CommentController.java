package com.analysetool.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.analysetool.repositories.CommentsRepository;
import com.analysetool.modells.Comments;
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentsRepository commentRepository;

    @GetMapping("")
    public Iterable<Comments> getAllComments() {
        return commentRepository.findAll();
    }
    /*
    @GetMapping("/{id}")
    public ResponseEntity<Comments> getCommentById(@PathVariable("id") Long id) {
        Comments comment = (Comments) commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(comment);
        }
    }

    @PostMapping("")
    public ResponseEntity<Comments> createComment(@RequestBody Comments comment) {
        Comments savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComment(@PathVariable("id") Long id, @RequestBody Comment comment) {
        Comments existingComment = commentRepository.findById(id).orElse(null);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        } else {
            comment.setCommenstId(id);
            Comments updatedComment = commentRepository.save(comment);
            return ResponseEntity.ok(updatedComment);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        Comments existingComment = commentRepository.findById(id).orElse(null);
        if (existingComment == null) {
            return ResponseEntity.notFound().build();
        } else {
            commentRepository.delete(existingComment);
            return ResponseEntity.noContent().build();
        }
    }*/

}
