package sn.veli.service;

import java.util.List;
import java.util.Optional;

import sn.veli.domain.Day;
import sn.veli.service.dto.DayDTO;

/**
 * Service Interface for managing {@link sn.veli.domain.Day}.
 */
public interface DayService {
    /**
     * Save a day.
     *
     * @param dayDTO the entity to save.
     * @return the persisted entity.
     */
    DayDTO save(DayDTO dayDTO);

    /**
     * Updates a day.
     *
     * @param dayDTO the entity to update.
     * @return the persisted entity.
     */
    DayDTO update(DayDTO dayDTO);

    /**
     * Partially updates a day.
     *
     * @param dayDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DayDTO> partialUpdate(DayDTO dayDTO);

    /**
     * Get all the days.
     *
     * @return the list of entities.
     */
    List<DayDTO> findAll();

    /**
     * Get the "id" day.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DayDTO> findOne(Long id);

    /**
     * Delete the "id" day.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Day findDayByDateString(String date);
}
