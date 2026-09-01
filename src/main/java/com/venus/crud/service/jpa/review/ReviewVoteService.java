package com.venus.crud.service.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReviewVotePatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewVoteRequest;
import com.venus.crud.dto.jpa.response.review.ReviewVoteResponse;
import com.venus.crud.entity.enums.VoteType;
import com.venus.crud.entity.review.ReviewVote;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.review.ReviewVoteMapper;
import com.venus.crud.repository.jpa.review.ReviewVoteRepository;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewVoteService {

    private static final Logger log = LoggerFactory.getLogger(ReviewVoteService.class);

    private final ReviewVoteRepository reviewVoteRepository;
    private final ReviewVoteMapper reviewVoteMapper;

    public ReviewVoteService(ReviewVoteRepository reviewVoteRepository, ReviewVoteMapper reviewVoteMapper) {
        this.reviewVoteRepository = reviewVoteRepository;
        this.reviewVoteMapper = reviewVoteMapper;
    }

    @Transactional(readOnly = true)
    public List<ReviewVoteResponse> findAll() {
        return executeOrFail(reviewVoteRepository::findAll, "Falha ao consultar votos no banco de dados").stream()
                .map(reviewVoteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewVoteResponse findByReviewIdAndUserId(Long reviewId, Long userId) {
        return reviewVoteMapper.toResponse(getOrThrow(reviewId, userId));
    }

    @Transactional(readOnly = true)
    public Slice<ReviewVoteResponse> findByReviewId(Long reviewId, Pageable pageable) {
        return executeOrFail(() -> reviewVoteRepository.findByReviewId(reviewId, pageable), "Falha ao consultar votos da avaliacao")
                .map(reviewVoteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long countByReviewIdAndVoteType(Long reviewId, VoteType voteType) {
        return executeOrFail(() -> reviewVoteRepository.countByReviewIdAndVoteType(reviewId, voteType),
                "Falha ao contar votos da avaliacao");
    }

    @Transactional(readOnly = true)
    public Slice<ReviewVoteResponse> search(VoteType voteType, Pageable pageable) {
        Slice<ReviewVote> result = voteType != null
                ? executeOrFail(() -> reviewVoteRepository.findByVoteType(voteType, pageable), "Falha ao consultar votos por tipo")
                : executeOrFail(() -> reviewVoteRepository.findAllBy(pageable), "Falha ao consultar votos");

        return result.map(reviewVoteMapper::toResponse);
    }

    @Transactional
    public ReviewVoteResponse create(ReviewVoteRequest request) {
        ensureUserHasNotVoted(request.reviewId(), request.userId());

        ReviewVote reviewVote = reviewVoteMapper.toEntity(request);
        ReviewVote saved = executeOrFail(() -> reviewVoteRepository.save(reviewVote), "Falha ao registrar voto no banco de dados");
        return reviewVoteMapper.toResponse(saved);
    }

    @Transactional
    public ReviewVoteResponse patch(Long reviewId, Long userId, ReviewVotePatchRequest request) {
        ReviewVote reviewVote = getOrThrow(reviewId, userId);
        reviewVoteMapper.patchEntity(request, reviewVote);

        ReviewVote saved = executeOrFail(() -> reviewVoteRepository.save(reviewVote), "Falha ao atualizar voto no banco de dados");
        return reviewVoteMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long reviewId, Long userId) {
        getOrThrow(reviewId, userId);
        executeOrFail(() -> {
            reviewVoteRepository.deleteByReviewIdAndUserId(reviewId, userId);
            return null;
        }, "Falha ao remover voto no banco de dados");
    }

    private ReviewVote getOrThrow(Long reviewId, Long userId) {
        var reviewVote = executeOrFail(() -> reviewVoteRepository.findByReviewIdAndUserId(reviewId, userId),
                "Falha ao consultar voto no banco de dados");
        return reviewVote.orElseThrow(
                () -> new ResourceNotFoundException("O usuario " + userId + " nao votou na avaliacao " + reviewId));
    }

    private void ensureUserHasNotVoted(Long reviewId, Long userId) {
        boolean exists = executeOrFail(() -> reviewVoteRepository.existsByReviewIdAndUserId(reviewId, userId),
                "Falha ao verificar voto existente");
        if (exists) {
            throw new DuplicateResourceException("O usuario " + userId + " ja votou na avaliacao " + reviewId);
        }
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