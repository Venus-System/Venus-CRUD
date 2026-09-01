package com.venus.crud.service.jpa.fullstage;

import com.venus.crud.dto.jpa.response.fullstage.ReviewFullResponse;
import com.venus.crud.entity.enums.VoteType;
import com.venus.crud.entity.review.Review;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.review.ReviewMapper;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import com.venus.crud.repository.jpa.review.ReviewVoteRepository;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewFullService {

    private static final Logger log = LoggerFactory.getLogger(ReviewFullService.class);

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository reviewVoteRepository;
    private final ReviewMapper reviewMapper;

    public ReviewFullService(ReviewRepository reviewRepository, ReviewVoteRepository reviewVoteRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewVoteRepository = reviewVoteRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional(readOnly = true)
    public ReviewFullResponse findById(Long id) {
        Review review = executeOrFail(() -> reviewRepository.findById(id), "Falha ao consultar avaliacao no banco de dados")
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada com id " + id));

        long usefulVotes = executeOrFail(() -> reviewVoteRepository.countByReviewIdAndVoteType(id, VoteType.USEFUL),
                "Falha ao contar votos uteis da avaliacao");
        long notUsefulVotes = executeOrFail(() -> reviewVoteRepository.countByReviewIdAndVoteType(id, VoteType.NOT_USEFUL),
                "Falha ao contar votos nao uteis da avaliacao");

        return new ReviewFullResponse(reviewMapper.toResponse(review), usefulVotes, notUsefulVotes);
    }

    private <T> T executeOrFail(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException ex) {
            log.warn("Violacao de integridade de dados: {}", ex.getMessage());
            throw new DuplicateResourceException("Os dados informados conflitam com um registro existente.");
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ServiceUnavailableException(errorMessage + ". Tente novamente mais tarde.", ex);
        }
    }
}