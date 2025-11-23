package com.capstone.vsl.repository;

import com.capstone.vsl.entity.Dictionary;
import com.capstone.vsl.entity.User;
import com.capstone.vsl.entity.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    Optional<UserFavorite> findByUserAndDictionary(User user, Dictionary dictionary);
    
    boolean existsByUserAndDictionary(User user, Dictionary dictionary);
    
    List<UserFavorite> findByUserOrderByCreatedAtDesc(User user);
}

