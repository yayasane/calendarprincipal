package sn.veli.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.veli.web.rest.TestUtil;

class DayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DayDTO.class);
        DayDTO dayDTO1 = new DayDTO();
        dayDTO1.setId(1L);
        DayDTO dayDTO2 = new DayDTO();
        assertThat(dayDTO1).isNotEqualTo(dayDTO2);
        dayDTO2.setId(dayDTO1.getId());
        assertThat(dayDTO1).isEqualTo(dayDTO2);
        dayDTO2.setId(2L);
        assertThat(dayDTO1).isNotEqualTo(dayDTO2);
        dayDTO1.setId(null);
        assertThat(dayDTO1).isNotEqualTo(dayDTO2);
    }
}
