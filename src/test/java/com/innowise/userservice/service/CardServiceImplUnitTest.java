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
import com.innowise.userservice.service.interfaces.mappers.CardMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.innowise.userservice.utils.TestsConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplUnitTest {

    @Mock
    private PaymentCardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper mapper;

    @Mock
    private CardServiceImpl self;

    @InjectMocks
    private CardServiceImpl cardService;

    private PaymentCard card;
    private User user;
    private CardResponseDto responseDto;
    private CardRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(4L);
        user.setName(NAME_JON);
        user.setSurname(SNOW);
        user.setBirthDate(LocalDate.now().minusYears(30));
        user.setEmail(EMAIL_JON);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        card = new PaymentCard(
                2L, CARD_NUMBER1, HOLDER_NAME_JON, LocalDate.now().plusYears(1),
                true, LocalDateTime.now(), LocalDateTime.now(), user
        );

        user.addCard(card);

        responseDto = new CardResponseDto(2L, 4L, CARD_NUMBER1,
                HOLDER_NAME_JON, card.getExpirationDate(), true,
                card.getCreatedAt(), card.getUpdatedAt());

        requestDto = new CardRequestDto(null, 2L, CARD_NUMBER1, HOLDER_NAME_JON, LocalDate.now(), true);
    }


    @Test
    void readAll() {
        List<PaymentCard> cardList = List.of(card);

        when(cardRepository.findAll()).thenReturn(cardList);
        when(mapper.cardsListToDtoList(cardList)).thenReturn(List.of(responseDto));

        List<CardResponseDto> response = cardService.readAll();
        assertEquals(1, response.size());
        assertEquals(CARD_NUMBER1, response.getFirst().number());

        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void readByIdTest() {
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        when(mapper.cardToCardDto(card)).thenReturn(responseDto);

        CardResponseDto responseDto1 = cardService.readById(2L);

        assertNotNull(responseDto1);
        verify(cardRepository, times(1)).findById(2L);
        verify(mapper).cardToCardDto(card);
    }

    @Test
    void findCardByIdTestThrowsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> cardService.readById(1L));
        verify(cardRepository).findById(any());
    }

    @Test
    void create() {


        when(userRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());
        when(userRepository.findByIdWithLock(2L)).thenReturn(Optional.of(user));
        when(mapper.cardDtoToCard(requestDto)).thenReturn(card);
        when(mapper.cardToCardDto(any())).thenReturn(responseDto);

        CardResponseDto responseDto1 = cardService.create(requestDto);

        assertNotNull(responseDto1);
        verify(cardRepository).saveAndFlush(any());

        user.addCard(card);
        user.addCard(card);
        user.addCard(card);
        user.addCard(card);

        assertThrows(CardLimitViolationException.class, () -> cardService.create(requestDto));


        CardRequestDto requestDtoForUserWithId1 = new CardRequestDto(null,
                1L, null, null, null, null);
        assertThrows(NoSuchElementException.class, () ->
                cardService.create(requestDtoForUserWithId1));
    }

    @Test
    void update() {
        CardRequestDto updateRequest = new CardRequestDto(2L, 4L,
                null, null, null, true);

        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        when(cardRepository.saveAndFlush(card)).thenReturn(card);
        when(mapper.cardToCardDto(card)).thenReturn(responseDto);

        cardService.update(2L, updateRequest);

        user.setId(1L);
        assertThrows(UnsupportedOperationException.class, () ->
                cardService.update(2L, updateRequest));

        assertThrows(IllegalArgumentException.class, () ->
                cardService.update(1L, requestDto));
    }

    @Test
    void deleteById() {
        cardService.deleteById(1L);
        verify(self).deactivateCard(1L);
    }

    @Test
    void readAllCardsByUserId() {
        List<PaymentCard> cardList = List.of(card);
        when(cardRepository.findPaymentCardsByUserId(2L)).thenReturn(cardList);
        when(mapper.cardsListToDtoList(cardList)).thenReturn(List.of(responseDto));
        List<CardResponseDto> cardResponseDtos = cardService.readAllCardsByUserId(2L);
        assertNotNull(cardResponseDtos);
        verify(cardRepository).findPaymentCardsByUserId(2L);
    }

    @Test
    void activateCard() {
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        when(cardRepository.saveAndFlush(card)).thenReturn(card);
        when(mapper.cardToCardDto(card)).thenReturn(responseDto);
        assertThrows(ActivationException.class, () -> cardService.activateCard(2L));

        card.setActive(false);
        CardResponseDto responseDto1 = cardService.activateCard(2L);
        assertNotNull(responseDto1);
    }

    @Test
    void deactivateCard() {
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        card.setActive(false);
        assertThrows(DeactivationException.class, () -> cardService.deactivateCard(2L));
    }
}