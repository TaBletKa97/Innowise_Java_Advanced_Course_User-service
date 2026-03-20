package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.PaymentCard;
import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.interfaces.PaymentCardRepository;
import com.innowise.userservice.repository.interfaces.UserRepository;
import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import com.innowise.userservice.service.exceptions.ActivationException;
import com.innowise.userservice.service.exceptions.CardLimitViolationException;
import com.innowise.userservice.service.exceptions.DeactivationException;
import com.innowise.userservice.service.interfaces.CardService;
import com.innowise.userservice.service.interfaces.mappers.CardMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.innowise.userservice.service.utils.ServiceConstants.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService<CardResponseDto, CardRequestDto, Long> {

    private final CardServiceImpl self;
    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper mapper;

    public CardServiceImpl(@Lazy CardServiceImpl self, PaymentCardRepository cardRepository, UserRepository userRepository, CardMapper mapper) {
        this.self = self;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CardResponseDto> readAll() {
        return mapper.cardsListToDtoList(cardRepository.findAll());
    }

    @Override
    public CardResponseDto readById(Long id) {
        return mapper.cardToCardDto(findCardById(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#createRequest.userId()"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public CardResponseDto create(CardRequestDto createRequest) {
        User user = userRepository.findByIdWithLock(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException(NO_USER_ERROR_MESSAGE
                        + createRequest.userId()));
        if (user.getCards().stream().filter(PaymentCard::isActive).count() >= 5) {
            throw new CardLimitViolationException();
        }

        PaymentCard card = mapper.cardDtoToCard(createRequest);
        user.addCard(card);

        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#updateRequest.userId()"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public CardResponseDto update(Long id, CardRequestDto updateRequest) {
        if (!Objects.equals(id, updateRequest.id())) {
            throw new IllegalArgumentException(
                    String.format("Path ID (%d) and Request ID (%d) must match",
                            id, updateRequest.id()));
        }

        PaymentCard card = findCardById(id);
        if (!Objects.equals(card.getUser().getId(), updateRequest.userId())) {
            throw new UnsupportedOperationException(CARD_OWNER_CHANGE_MESSAGE);
        }
        mapper.updateFromDto(updateRequest, card);
        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public void deleteById(Long id) {
        self.deactivateCard(id);
    }

    @Override
    @Transactional
    public List<CardResponseDto> readAllCardsByUserId(Long userId) {
        return mapper.cardsListToDtoList(
                cardRepository.findPaymentCardsByUserId(userId));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public CardResponseDto activateCard(Long id) {
        PaymentCard card = findCardById(id);
        if (card.isActive()) {
            throw new  ActivationException();
        }
        card.setActive(true);
        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user_list", allEntries = true)
    })
    public CardResponseDto deactivateCard(Long id) {
        PaymentCard card = findCardById(id);
        if (!card.isActive()) {
            throw new DeactivationException();
        }
        card.setActive(false);
        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    private PaymentCard findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(NO_CARD_ERROR_MESSAGE + id));
    }
}
