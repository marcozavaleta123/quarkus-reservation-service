package com.app.infraestructure.persistence.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.domain.model.Booking;
import com.app.domain.model.Client;
import com.app.domain.model.Professional;
import com.app.infraestructure.persistence.entity.BookingEntity;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class BookingMapper {

    private final ModelMapper modelMapper;

    public Booking toDomain(BookingEntity entity) {
        Booking booking = modelMapper.map(entity, Booking.class);
        booking.setClient(
            modelMapper.map(entity.getClientEntity(), Client.class)
        );
        booking.setProfessional(
            modelMapper.map(entity.getProfessionalEntity(), Professional.class)
        );
        return booking;
    }

    public List<Booking> toDomainList(List<BookingEntity> entities) {
        return entities.stream()
            .map(this::toDomain)
            .toList();
    }
}
