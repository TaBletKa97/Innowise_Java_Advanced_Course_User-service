package com.innowise.userservice.service;

import com.innowise.userservice.repository.entity.PaymentCard;
import com.innowise.userservice.repository.entity.User;
import com.innowise.userservice.repository.interfaces.PaymentCardRepository;
import com.innowise.userservice.repository.interfaces.UserRepository;
import com.innowise.userservice.service.DTO.CardRequestDTO;
import com.innowise.userservice.service.DTO.CardResponseDTO;
import com.innowise.userservice.service.exceptions.CardLimitViolationException;
import com.innowise.userservice.service.interfaces.CardService;
import com.innowise.userservice.service.interfaces.mappers.CardMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static com.innowise.userservice.service.utils.ServiceConstants.*;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService<CardResponseDTO, CardRequestDTO, Long> {

    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper mapper;

    @Override
    public List<CardResponseDTO> readAll() {
        return mapper.cardsListToDtoList(cardRepository.findAll());
    }

    @Override
    public CardResponseDTO readById(Long id) {
        return mapper.cardToCardDto(cardRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(NO_CARD_ERROR_MESSAGE + id)));
    }

    @Override
    @Transactional
    public CardResponseDTO create(@Validated CardRequestDTO createRequest) {
        User user = userRepository.findById(createRequest.userId())
                .orElseThrow(() -> new NoSuchElementException(NO_USER_ERROR_MESSAGE
                        + createRequest.userId()));

        user.getCards().forEach(PaymentCard::getId);

        if (user.getCards().size() >= 5) {
            throw new CardLimitViolationException();
        }

        PaymentCard card = mapper.cardDtoToCard(createRequest);
        user.addCard(card);

        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    public CardResponseDTO update(Long id, @Validated CardRequestDTO updateRequest) {
        if (!Objects.equals(id, updateRequest.id())) {
            throw new IllegalArgumentException(
                    String.format("Path ID (%d) and Request ID (%d) must match",
                            id, updateRequest.id()));
        }

        PaymentCard card = cardRepository.findById(updateRequest.id()).orElseThrow(() ->
                new NoSuchElementException(NO_CARD_ERROR_MESSAGE + updateRequest.id()));
        if (!Objects.equals(card.getUser().getId(), updateRequest.userId())) {
            throw new UnsupportedOperationException(CARD_OWNER_CHANGE_MESSAGE);
        }
        mapper.updateFromDto(updateRequest, card);
        return mapper.cardToCardDto(cardRepository.saveAndFlush(card));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new NoSuchElementException(NO_CARD_ERROR_MESSAGE + id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<CardResponseDTO> readAllCardsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchElementException(NO_USER_ERROR_MESSAGE + userId));
        user.getCards().forEach(PaymentCard::getId);
        return mapper.cardsListToDtoList(user.getCards());
    }

    @Override
    @Transactional
    public CardResponseDTO activateCard(Long id) {
        return mapper.cardToCardDto(cardRepository.activateCard(id));
    }

    @Override
    @Transactional
    public CardResponseDTO deactivateCard(Long id) {
        return mapper.cardToCardDto(cardRepository.deactivateCard(id));
    }
}
