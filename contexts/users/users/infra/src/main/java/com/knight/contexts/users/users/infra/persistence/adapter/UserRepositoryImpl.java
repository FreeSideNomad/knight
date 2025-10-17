package com.knight.contexts.users.users.infra.persistence.adapter;

import com.knight.contexts.users.users.app.service.UserApplicationService;
import com.knight.contexts.users.users.domain.aggregate.User;
import com.knight.contexts.users.users.infra.persistence.mapper.UserMapper;
import com.knight.contexts.users.users.infra.persistence.repository.UserJpaRepository;
import com.knight.platform.sharedkernel.UserId;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * JPA-based implementation of UserRepository.
 * Adapter between domain repository interface and JPA repository.
 */
@Singleton
public class UserRepositoryImpl implements UserApplicationService.UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(User user) {
        LOG.debug("Saving user: {}", user.getUserId().id());

        var entity = mapper.toEntity(user);
        jpaRepository.save(entity);

        LOG.info("User saved: {}", user.getUserId().id());
    }

    @Override
    public Optional<User> findById(UserId userId) {
        LOG.debug("Finding user by ID: {}", userId.id());

        return jpaRepository.findById(userId.id())
            .map(mapper::toDomain);
    }
}
