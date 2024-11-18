package net.datafaker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FakeValuesTest {

    private static final String PATH = "address";
    private FakeValues fakeValues;
    private FakeValuesContext fakeValuesContext;

    @BeforeEach
    void before() {
        fakeValuesContext = mock(FakeValuesContext.class);
        fakeValues = FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "address.yml", PATH));
    }

/*
    Test case for for https://github.com/datafaker-net/datafaker/issues/574
    To test it need to change net.datafaker.service.FakeValues.loadValues to something from private
    Powermock can not test it because it requires JUnit4
    @Test
    void testLoadValues() {
        FakeValues fv = Mockito.spy(new FakeValues(Locale.ENGLISH));
        ExecutorService service = new ForkJoinPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        service.submit(() -> {
            latch.countDown();
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            fv.get("key");
        });
        service.submit(() -> {
            latch.countDown();
            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            fv.get("key");
        });
        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(fv, times(1)).loadValues();
    }
*/

    @Test
    void getAValueReturnsAValue() {
        assertThat(fakeValues.get(PATH)).isNotNull();
    }

    @Test
    void getAValueDoesNotReturnAValue() {
        assertThat(fakeValues.get("dog")).isNull();
    }

    @Test
    void getAValueWithANonEnglishFile() {
        FakeValues frenchFakeValues = FakeValues.of(FakeValuesContext.of(Locale.FRENCH));
        assertThat(frenchFakeValues.get(PATH)).isNotNull();
    }

    @Test
    void getAValueForHebrewLocale() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.get(PATH)).isNotNull();
    }

    @Test
    void correctPathForHebrewLanguage() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.getPaths()).containsExactly("he");
    }

    @Test
    void incorrectPathForHebrewLanguage() {
        FakeValues hebrew = FakeValues.of(FakeValuesContext.of(new Locale("iw")));
        assertThat(hebrew.getPaths()).doesNotContain("iw");
    }

    @Test
    void correctLocale() {
        FakeValues fv = FakeValues.of(FakeValuesContext.of(new Locale("uk")));
        assertThat(fv.getLocale()).isEqualTo(new Locale("uk"));
    }

    @Test
    void getAValueFromALocaleThatCantBeLoaded() {
        FakeValues fakeValues = FakeValues.of(FakeValuesContext.of(new Locale("nothing")));
        assertThat(fakeValues.get(PATH)).isNull();
    }

    @ParameterizedTest
    @MethodSource("fakeValuesProvider")
    void checkEquals(FakeValues fv1, FakeValues fv2, boolean equals) {
        if (equals) {
            assertThat(fv1).isEqualTo(fv2);
        } else {
            assertThat(fv1).isNotEqualTo(fv2);
        }
    }

    @Test
    void testLoadFromUrlIOException() throws IOException {
        fakeValues = FakeValues.of(fakeValuesContext);
        URL mockUrl = mock(URL.class);

        when(fakeValuesContext.getUrl()).thenReturn(mockUrl);
        when(mockUrl.openStream()).thenThrow(new IOException("Stream error"));

        assertThatThrownBy(() -> fakeValues.loadFromUrl())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to read fake values from");
    }

    @Test
    void testKeysOfWithNullMap() {
        // Testing null map should return null
        Set<String> result = FakeValues.keysOf(null);
        assertThat(result).isNull();
    }

    @Test
    void testKeysOfWithEmptyMap() {
        // Testing empty map should return null
        Set<String> result = FakeValues.keysOf(Collections.emptyMap());
        assertThat(result).isNull();
    }

    @Test
    void testKeysOfWithNonEmptyMap() {
        // Testing non-empty map should return the set of keys
        Map<String, Object> map = Map.of("key1", "value1", "key2", "value2");
        Set<String> result = FakeValues.keysOf(map);
        assertThat(result).containsExactlyInAnyOrder("key1", "key2");
    }

    static Stream<Arguments> fakeValuesProvider() throws MalformedURLException {
        Path tmp = Paths.get("tmp");
        return Stream.of(
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), FakeValues.of(FakeValuesContext.of(Locale.CANADA)), true),
            of(null, FakeValues.of(FakeValuesContext.of(Locale.CANADA)), false),
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), null, false),
            of(FakeValues.of(FakeValuesContext.of(Locale.CANADA)), null, false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH)), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", null)), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")),
                false),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")), FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path")),
                true),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())),
                FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())), true),
            of(FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", Paths.get("tmp2").toUri().toURL())),
                FakeValues.of(FakeValuesContext.of(Locale.ENGLISH, "filepath", "path", tmp.toUri().toURL())), false)
        );
    }
}
