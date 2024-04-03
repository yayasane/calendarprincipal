package sn.veli.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.veli.IntegrationTest;
import sn.veli.domain.Day;
import sn.veli.repository.DayRepository;
import sn.veli.service.dto.DayDTO;
import sn.veli.service.mapper.DayMapper;

/**
 * Integration tests for the {@link DayResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DayResourceIT {

    private static final String DEFAULT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_DAY_OF_WEEK = "AAAAAAAAAA";
    private static final String UPDATED_DAY_OF_WEEK = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/days";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private DayMapper dayMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDayMockMvc;

    private Day day;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Day createEntity(EntityManager em) {
        Day day = new Day().date(DEFAULT_DATE).dayOfWeek(DEFAULT_DAY_OF_WEEK);
        return day;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Day createUpdatedEntity(EntityManager em) {
        Day day = new Day().date(UPDATED_DATE).dayOfWeek(UPDATED_DAY_OF_WEEK);
        return day;
    }

    @BeforeEach
    public void initTest() {
        day = createEntity(em);
    }

    @Test
    @Transactional
    void createDay() throws Exception {
        int databaseSizeBeforeCreate = dayRepository.findAll().size();
        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);
        restDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isCreated());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeCreate + 1);
        Day testDay = dayList.get(dayList.size() - 1);
        assertThat(testDay.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDay.getDayOfWeek()).isEqualTo(DEFAULT_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void createDayWithExistingId() throws Exception {
        // Create the Day with an existing ID
        day.setId(1L);
        DayDTO dayDTO = dayMapper.toDto(day);

        int databaseSizeBeforeCreate = dayRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = dayRepository.findAll().size();
        // set the field null
        day.setDate(null);

        // Create the Day, which fails.
        DayDTO dayDTO = dayMapper.toDto(day);

        restDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isBadRequest());

        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDayOfWeekIsRequired() throws Exception {
        int databaseSizeBeforeTest = dayRepository.findAll().size();
        // set the field null
        day.setDayOfWeek(null);

        // Create the Day, which fails.
        DayDTO dayDTO = dayMapper.toDto(day);

        restDayMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isBadRequest());

        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDays() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        // Get all the dayList
        restDayMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(day.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE)))
            .andExpect(jsonPath("$.[*].dayOfWeek").value(hasItem(DEFAULT_DAY_OF_WEEK)));
    }

    @Test
    @Transactional
    void getDay() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        // Get the day
        restDayMockMvc
            .perform(get(ENTITY_API_URL_ID, day.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(day.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE))
            .andExpect(jsonPath("$.dayOfWeek").value(DEFAULT_DAY_OF_WEEK));
    }

    @Test
    @Transactional
    void getNonExistingDay() throws Exception {
        // Get the day
        restDayMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDay() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        int databaseSizeBeforeUpdate = dayRepository.findAll().size();

        // Update the day
        Day updatedDay = dayRepository.findById(day.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDay are not directly saved in db
        em.detach(updatedDay);
        updatedDay.date(UPDATED_DATE).dayOfWeek(UPDATED_DAY_OF_WEEK);
        DayDTO dayDTO = dayMapper.toDto(updatedDay);

        restDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dayDTO))
            )
            .andExpect(status().isOk());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
        Day testDay = dayList.get(dayList.size() - 1);
        assertThat(testDay.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDay.getDayOfWeek()).isEqualTo(UPDATED_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void putNonExistingDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dayDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(dayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDayWithPatch() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        int databaseSizeBeforeUpdate = dayRepository.findAll().size();

        // Update the day using partial update
        Day partialUpdatedDay = new Day();
        partialUpdatedDay.setId(day.getId());

        restDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDay))
            )
            .andExpect(status().isOk());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
        Day testDay = dayList.get(dayList.size() - 1);
        assertThat(testDay.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testDay.getDayOfWeek()).isEqualTo(DEFAULT_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void fullUpdateDayWithPatch() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        int databaseSizeBeforeUpdate = dayRepository.findAll().size();

        // Update the day using partial update
        Day partialUpdatedDay = new Day();
        partialUpdatedDay.setId(day.getId());

        partialUpdatedDay.date(UPDATED_DATE).dayOfWeek(UPDATED_DAY_OF_WEEK);

        restDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDay.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDay))
            )
            .andExpect(status().isOk());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
        Day testDay = dayList.get(dayList.size() - 1);
        assertThat(testDay.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testDay.getDayOfWeek()).isEqualTo(UPDATED_DAY_OF_WEEK);
    }

    @Test
    @Transactional
    void patchNonExistingDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dayDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(dayDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDay() throws Exception {
        int databaseSizeBeforeUpdate = dayRepository.findAll().size();
        day.setId(longCount.incrementAndGet());

        // Create the Day
        DayDTO dayDTO = dayMapper.toDto(day);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDayMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(dayDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Day in the database
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDay() throws Exception {
        // Initialize the database
        dayRepository.saveAndFlush(day);

        int databaseSizeBeforeDelete = dayRepository.findAll().size();

        // Delete the day
        restDayMockMvc.perform(delete(ENTITY_API_URL_ID, day.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Day> dayList = dayRepository.findAll();
        assertThat(dayList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
