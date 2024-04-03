package sn.veli.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.veli.domain.Day;
import sn.veli.repository.DayRepository;
import sn.veli.service.DayService;
import sn.veli.service.dto.DayDTO;
import sn.veli.service.mapper.DayMapper;

/**
 * Service Implementation for managing {@link sn.veli.domain.Day}.
 */
@Service
@Transactional
public class DayServiceImpl implements DayService {

    private final Logger log = LoggerFactory.getLogger(DayServiceImpl.class);

    private final DayRepository dayRepository;

    private final DayMapper dayMapper;

    public DayServiceImpl(DayRepository dayRepository, DayMapper dayMapper) {
        this.dayRepository = dayRepository;
        this.dayMapper = dayMapper;
    }

    @Override
    public DayDTO save(DayDTO dayDTO) {
        log.debug("Request to save Day : {}", dayDTO);
        Day day = dayMapper.toEntity(dayDTO);
        day = dayRepository.save(day);
        return dayMapper.toDto(day);
    }

    @Override
    public DayDTO update(DayDTO dayDTO) {
        log.debug("Request to update Day : {}", dayDTO);
        Day day = dayMapper.toEntity(dayDTO);
        day = dayRepository.save(day);
        return dayMapper.toDto(day);
    }

    @Override
    public Optional<DayDTO> partialUpdate(DayDTO dayDTO) {
        log.debug("Request to partially update Day : {}", dayDTO);

        return dayRepository
            .findById(dayDTO.getId())
            .map(existingDay -> {
                dayMapper.partialUpdate(existingDay, dayDTO);

                return existingDay;
            })
            .map(dayRepository::save)
            .map(dayMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayDTO> findAll() {
        log.debug("Request to get all Days");
        return dayRepository.findAll().stream().map(dayMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DayDTO> findOne(Long id) {
        log.debug("Request to get Day : {}", id);
        return dayRepository.findById(id).map(dayMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Day : {}", id);
        dayRepository.deleteById(id);
    }

    public Day findDayByDateString(String dateString) {
        // Convertir la date en format LocalDate
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Calculer le jour de la semaine
        String dayOfWeek = DayOfWeek.from(date).name();

        // Créer un objet Day
        Day day = new Day();
        day.setDate(dateString);
        day.setDayOfWeek(dayOfWeek);

        // Enregistrer l'objet Day (optionnel)
        // dayRepository.save(day);

        return day;
    }
}
