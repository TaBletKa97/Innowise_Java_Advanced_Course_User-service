package com.innowise.userservice.service.interfaces.mappers;

import com.innowise.userservice.repository.entity.PaymentCard;
import com.innowise.userservice.service.dto.CardRequestDto;
import com.innowise.userservice.service.dto.CardResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    List<CardResponseDto> cardsListToDtoList(List<PaymentCard> cards);

    @Mapping(target = "userId", source = "user.id")
    CardResponseDto cardToCardDto(PaymentCard card);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard cardDtoToCard(CardRequestDto cardRequest);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateFromDto(CardRequestDto request, @MappingTarget PaymentCard target);

}
