package com.venus.crud.service.jpa.review;

import com.venus.crud.dto.jpa.patch.review.ReviewPatchRequest;
import com.venus.crud.dto.jpa.request.review.ReviewRequest;
import com.venus.crud.dto.jpa.response.review.ReviewResponse;
import com.venus.crud.entity.review.Review;
import com.venus.crud.exception.DuplicateResourceException;
import com.venus.crud.exception.ResourceNotFoundException;
import com.venus.crud.exception.ServiceUnavailableException;
import com.venus.crud.mapper.jpa.review.ReviewMapper;
import com.venus.crud.repository.jpa.review.ReviewRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> findAll() {
        return executeOrFail(reviewRepository::findAll, "Falha ao consultar avaliacoes no banco de dados").stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponse findById(Long id) {
        return reviewMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> findByProductVersionId(Long productVersionId, Boolean verifiedUse, BigDecimal minRating, Pageable pageable) {
        Slice<Review> result;
        if (Boolean.TRUE.equals(verifiedUse)) {
            result = executeOrFail(() -> reviewRepository.findByProductVersionIdAndVerifiedUseTrue(productVersionId, pageable),
                    "Falha ao consultar avaliacoes verificadas do produto");
        } else if (minRating != null) {
            result = executeOrFail(() -> reviewRepository.findByProductVersionIdAndRatingGreaterThanEqual(productVersionId, minRating, pageable),
                    "Falha ao consultar avaliacoes do produto por nota minima");
        } else {
            result = executeOrFail(() -> reviewRepository.findByProductVersionId(productVersionId, pageable),
                    "Falha ao consultar avaliacoes do produto");
        }

        return result.map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> findByUserId(Long userId, Pageable pageable) {
        return executeOrFail(() -> reviewRepository.findByUserId(userId, pageable), "Falha ao consultar avaliacoes do usuario")
                .map(reviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Slice<ReviewResponse> search(Pageable pageable) {
        return executeOrFail(() -> reviewRepository.findAllBy(pageable), "Falha ao consultar avaliacoes")
                .map(reviewMapper::toResponse);
    }

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        ensureUserHasNotReviewed(request.userId(), request.productVersionId(), null);

        Review review = reviewMapper.toEntity(request);
        Review saved = executeOrFail(() -> reviewRepository.save(review), "Falha ao criar avaliacao no banco de dados");
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewRequest request) {
        Review review = getOrThrow(id);
        ensureUserHasNotReviewed(request.userId(), request.productVersionId(), id);

        reviewMapper.updateEntity(request, review);
        Review saved = executeOrFail(() -> reviewRepository.save(review), "Falha ao atualizar avaliacao no banco de dados");
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public ReviewResponse patch(Long id, ReviewPatchRequest request) {
        Review review = getOrThrow(id);
        reviewMapper.patchEntity(request, review);

        Review saved = executeOrFail(() -> reviewRepository.save(review), "Falha ao atualizar avaliacao no banco de dados");
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Review review = getOrThrow(id);
        executeOrFail(() -> {
            reviewRepository.delete(review);
            return null;
        }, "Falha ao remover avaliacao no banco de dados");
    }

    private Review getOrThrow(Long id) {
        Optional<Review> review = executeOrFail(() -> reviewRepository.findById(id), "Falha ao consultar avaliacao no banco de dados");
        return review.orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada com id " + id));
    }

    private void ensureUserHasNotReviewed(Long userId, Long productVersionId, Long excludeId) {
        boolean alreadyReviewed = executeOrFail(() -> reviewRepository.findByUserIdAndProductVersionId(userId, productVersionId),
                "Falha ao verificar avaliacao existente do usuario")
                .filter(existing -> !existing.getId().equals(excludeId))
                .isPresent();
        if (alreadyReviewed) {
            throw new DuplicateResourceException("O usuario " + userId + " ja avaliou a versao de produto " + productVersionId);
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