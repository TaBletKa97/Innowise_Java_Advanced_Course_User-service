package com.innowise.userservice.controller.interfaces.mappers;

import com.innowise.userservice.controller.DTO.CardRequestDTO;
import com.innowise.userservice.controller.DTO.CardResponseDTO;
import com.innowise.userservice.model.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    List<CardResponseDTO> cardsListToDtoList(List<PaymentCard> cards);

    @Mapping(target = "userId", source = "user.id")
    CardResponseDTO cardToCardDto(PaymentCard card);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard cardDtoToCard(CardRequestDTO cardRequest);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateFromDto(CardRequestDTO request, @MappingTarget PaymentCard target);

}
