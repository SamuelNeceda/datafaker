package net.datafaker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FakeValuesGroupingTest {

    private FakeValuesGrouping fakeValuesGrouping;
    private FakeValues addressValues;

    @BeforeEach
    void before() {
        fakeValuesGrouping = new FakeValuesGrouping();
        addressValues = FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "address.yml", "address"));
        fakeValuesGrouping.add(addressValues);
    }

    @Test
    void handlesOneFakeValue() {
        assertThat(fakeValuesGrouping.get("address")).isEqualTo(addressValues.get("address"))
            .isNotNull();
    }

    @Test
    void handlesMultipleFakeValues() {
        FakeValues catValues = FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "cat.yml", "creature"));
        fakeValuesGrouping.add(catValues);

        assertThat(fakeValuesGrouping.get("address")).isEqualTo(addressValues.get("address"))
            .isNotNull();

        assertThat(fakeValuesGrouping.get("creature")).isEqualTo(catValues.get("creature"))
            .isNotNull();
    }

    @Test
    void throwsExceptionForUnsupportedFakeValuesInterface() {
        FakeValuesInterface unsupportedFakeValue = new FakeValuesInterface() {
            @Override
            public Map<String, Object> get(String key) {
                return Collections.emptyMap();
            }
        };

        assertThatThrownBy(() -> fakeValuesGrouping.add(unsupportedFakeValue))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not supported (please raise an issue)");
    }

    @Test
    void mergesFakeValuesGrouping() {
        FakeValuesGrouping anotherGrouping = new FakeValuesGrouping();
        FakeValues addressValues = FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "address.yml", "address"));
        anotherGrouping.add(addressValues);

        fakeValuesGrouping.add(anotherGrouping);
        
        assertThat(fakeValuesGrouping.get("address")).isEqualTo(addressValues.get("address"))
            .isNotNull();
    }

}
