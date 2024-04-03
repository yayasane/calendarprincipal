package sn.veli.service.mapper;

import org.mapstruct.*;
import sn.veli.domain.Day;
import sn.veli.service.dto.DayDTO;

/**
 * Mapper for the entity {@link Day} and its DTO {@link DayDTO}.
 */
@Mapper(componentModel = "spring")
public interface DayMapper extends EntityMapper<DayDTO, Day> {}
