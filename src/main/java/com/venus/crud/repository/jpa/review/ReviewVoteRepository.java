package com.venus.crud.repository.jpa.review;

import com.venus.crud.entity.enums.VoteType;
import com.venus.crud.entity.review.ReviewVote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {

    Optional<ReviewVote> findByReviewIdAndUserId(Long reviewId, Long userId);
    long countByReviewIdAndVoteType(Long reviewId, VoteType voteType);
    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
    Page<ReviewVote> findByVoteType(VoteType voteType, Pageable pageable);
}
