package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {

	@InjectMocks
	private ScoreService service;

	@Mock
	private MovieRepository movieRepository;

	@Mock
	private ScoreRepository scoreRepository;

	@Mock
	private UserService userService;

	private ScoreDTO scoreDTO;
	private MovieEntity movie;
	private UserEntity user;

	@BeforeEach
	void setUp() throws Exception {

		scoreDTO = ScoreFactory.createScoreDTO();
		movie = MovieFactory.createMovieEntity();
		user = UserFactory.createUserEntity();

		Mockito.when(userService.authenticated()).thenReturn(user);

		Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		Mockito.when(scoreRepository.saveAndFlush(any())).thenAnswer(invocation -> {
			ScoreEntity score = invocation.getArgument(0);
			movie.getScores().add(score);
			return score;
		});
		Mockito.when(movieRepository.save(any())).thenReturn(movie);

		MovieDTO result = service.saveScore(scoreDTO);

		assertEquals(movie.getId(), result.getId());
		assertEquals(movie.getTitle(), result.getTitle());
		assertEquals(4.5, result.getScore());
		assertEquals(1, result.getCount());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});

		Mockito.verify(movieRepository, Mockito.times(1)).findById(scoreDTO.getMovieId());


		Mockito.when(movieRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDTO);
		});
	}
}
