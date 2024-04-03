package sn.veli.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.veli.domain.DayTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.veli.web.rest.TestUtil;

class DayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Day.class);
        Day day1 = getDaySample1();
        Day day2 = new Day();
        assertThat(day1).isNotEqualTo(day2);

        day2.setId(day1.getId());
        assertThat(day1).isEqualTo(day2);

        day2 = getDaySample2();
        assertThat(day1).isNotEqualTo(day2);
    }
}
