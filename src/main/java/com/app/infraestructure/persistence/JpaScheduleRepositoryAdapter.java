package com.app.infraestructure.persistence;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;

import com.app.application.port.out.ScheduleRepositoryOutPort;
import com.app.domain.model.Schedule;
import com.app.infraestructure.persistence.entity.ProfessionalEntity;
import com.app.infraestructure.persistence.entity.ScheduleEntity;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class JpaScheduleRepositoryAdapter implements ScheduleRepositoryOutPort {
	
	private final ModelMapper modelMapper;

	@Override
	public Uni<Long> save(Schedule schedule, Long professionalId) {
		ScheduleEntity entity = modelMapper.map(schedule, ScheduleEntity.class);
		
		return ProfessionalEntity.findById(professionalId)
				.flatMap(professional -> {
					ProfessionalEntity professionalEntity = (ProfessionalEntity) professional;
					
					entity.setProfessionalEntity(professionalEntity);
					entity.setStatus(true);

					return ScheduleEntity.persist(entity).replaceWith(entity.getId());
				});
	}

	@Override
	public Uni<List<Schedule>> findByProfessionalIdAndDate(long professionalId, LocalDate date) {
		return ScheduleEntity.find("professionalEntity.id = ?1 and date = ?2 and status = ?3", professionalId, date, true)
				.list()
				.map(entities ->
	            entities.stream()
	                .map(entity -> modelMapper.map(entity, Schedule.class))
	                .toList()
	        );
    }

}
