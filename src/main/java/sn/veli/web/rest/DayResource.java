package sn.veli.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.veli.repository.DayRepository;
import sn.veli.service.DayService;
import sn.veli.service.dto.DayDTO;
import sn.veli.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.veli.domain.Day}.
 */
@RestController
@RequestMapping("/api/days")
public class DayResource {

    private final Logger log = LoggerFactory.getLogger(DayResource.class);

    private static final String ENTITY_NAME = "calendarprincipalDay";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DayService dayService;

    private final DayRepository dayRepository;

    public DayResource(DayService dayService, DayRepository dayRepository) {
        this.dayService = dayService;
        this.dayRepository = dayRepository;
    }

    /**
     * {@code POST  /days} : Create a new day.
     *
     * @param dayDTO the dayDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dayDTO, or with status {@code 400 (Bad Request)} if the day has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DayDTO> createDay(@Valid @RequestBody DayDTO dayDTO) throws URISyntaxException {
        log.debug("REST request to save Day : {}", dayDTO);
        if (dayDTO.getId() != null) {
            throw new BadRequestAlertException("A new day cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DayDTO result = dayService.save(dayDTO);
        return ResponseEntity
            .created(new URI("/api/days/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /days/:id} : Updates an existing day.
     *
     * @param id the id of the dayDTO to save.
     * @param dayDTO the dayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dayDTO,
     * or with status {@code 400 (Bad Request)} if the dayDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DayDTO> updateDay(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody DayDTO dayDTO)
        throws URISyntaxException {
        log.debug("REST request to update Day : {}, {}", id, dayDTO);
        if (dayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DayDTO result = dayService.update(dayDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dayDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /days/:id} : Partial updates given fields of an existing day, field will ignore if it is null
     *
     * @param id the id of the dayDTO to save.
     * @param dayDTO the dayDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dayDTO,
     * or with status {@code 400 (Bad Request)} if the dayDTO is not valid,
     * or with status {@code 404 (Not Found)} if the dayDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the dayDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DayDTO> partialUpdateDay(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DayDTO dayDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Day partially : {}, {}", id, dayDTO);
        if (dayDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dayDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dayRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DayDTO> result = dayService.partialUpdate(dayDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dayDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /days} : get all the days.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of days in body.
     */
    @GetMapping("")
    public List<DayDTO> getAllDays() {
        log.debug("REST request to get all Days");
        return dayService.findAll();
    }

    /**
     * {@code GET  /days/:id} : get the "id" day.
     *
     * @param id the id of the dayDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dayDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DayDTO> getDay(@PathVariable("id") Long id) {
        log.debug("REST request to get Day : {}", id);
        Optional<DayDTO> dayDTO = dayService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dayDTO);
    }

    /**
     * {@code DELETE  /days/:id} : delete the "id" day.
     *
     * @param id the id of the dayDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable("id") Long id) {
        log.debug("REST request to delete Day : {}", id);
        dayService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
