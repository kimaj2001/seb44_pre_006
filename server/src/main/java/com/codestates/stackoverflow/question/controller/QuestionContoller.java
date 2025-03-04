package com.codestates.stackoverflow.question.controller;

import com.codestates.stackoverflow.dto.MultiResponseDto;
import com.codestates.stackoverflow.dto.SingleResponseDto;
import com.codestates.stackoverflow.question.dto.QuestionDto;
import com.codestates.stackoverflow.question.entity.Question;
import com.codestates.stackoverflow.question.mapper.QuestionMapper;
import com.codestates.stackoverflow.question.service.QuestionService;
import com.codestates.stackoverflow.utils.UriCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/questions")
public class QuestionContoller {
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionMapper questionMapper;

    @PostMapping("/ask")
    public ResponseEntity postQuestion(@RequestBody @Valid QuestionDto.PostRequest request) {
        // Location 헤더에 생성된 질문의 정보를 조회할 수 있는 URI를 포함하여 전달한다.
        Question question = questionService.createQuestion(questionMapper.requestToQuestion(request));
        URI location = UriCreator.createUri("/questions", question.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/posts/{question-id}/edit")
    public ResponseEntity updateQuestion(@RequestBody @Valid QuestionDto.PostRequest request,
                                         @PathVariable("question-id") Long questionId) {
        Question question = questionService.updateQuestion(questionMapper.requestToQuestion(request), questionId);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create("/questions/" + question.getId()))
                .build();
    }

    @GetMapping("{question-id}")
    public ResponseEntity getQuestion(@PathVariable("question-id") @Positive Long id) {
         return new ResponseEntity<>(
                 new SingleResponseDto<>(questionMapper.questionToDetail(questionService.getQuestion(id))), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<MultiResponseDto> getQuestions(@RequestParam @Positive int page,
                                                         @RequestParam @Positive int size) {
        Page<Question> pageQuestion = questionService.getQuestions(page - 1, size);
        List<Question> questions = pageQuestion.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        questionMapper.questionsToResponses(questions), pageQuestion),
                HttpStatus.OK);
    }

    //제목으로 질문 검색하는 기능
    @GetMapping("/search")
    public ResponseEntity<MultiResponseDto> searchTitle(@RequestParam @Positive int page,
                                                        @RequestParam @Positive int size,
                                                        @RequestParam String title) {
        Page<Question> pageQuestion = questionService.searchTitle(page - 1, size, title);
        List<Question> questions = pageQuestion.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(questionMapper.questionsToResponses(questions), pageQuestion), HttpStatus.OK);
    }

    @DeleteMapping("{question-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable("question-id") Long id) {
        questionService.deleteQuestion(id);
    }


}
