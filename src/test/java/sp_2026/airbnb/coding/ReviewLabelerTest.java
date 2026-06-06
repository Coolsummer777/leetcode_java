package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ReviewLabelerTest {

    private static Map<String, String> tokens(String... pairs) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }

    @Test
    void problemSample_caseInsensitiveWithOriginalCasing() {
        String review = "I booked a house on Airbnb for my trip to San Francisco. It was a lovely experience.";
        Map<String, String> tokens = tokens("Airbnb", "business", "san francisco", "city");

        assertEquals(
                "I booked a house on [business]{Airbnb} for my trip to [city]{San Francisco}. It was a lovely experience.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void longestMatchWins_whenOneTokenExtendsAnother() {
        String review = "We visited San Francisco Bay yesterday.";
        Map<String, String> tokens = tokens("san francisco", "city", "san francisco bay", "region");

        assertEquals(
                "We visited [region]{San Francisco Bay} yesterday.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void longestMatchWins_whenShorterTokenIsPrefix() {
        String review = "Airbnb Inc is hiring.";
        Map<String, String> tokens = tokens("airbnb", "business", "airbnb inc", "company");

        assertEquals(
                "[company]{Airbnb Inc} is hiring.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void repeatedTokens_eachOccurrenceWrapped() {
        String review = "Airbnb hosts love Airbnb.";
        Map<String, String> tokens = tokens("airbnb", "business");

        assertEquals(
                "[business]{Airbnb} hosts love [business]{Airbnb}.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void tokenAtStartAndEnd() {
        String review = "Airbnb was great. Book Airbnb";
        Map<String, String> tokens = tokens("airbnb", "business");

        assertEquals(
                "[business]{Airbnb} was great. Book [business]{Airbnb}",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void punctuationAfterToken_doesNotBreakMatch() {
        String review = "I used Airbnb.";
        Map<String, String> tokens = tokens("airbnb", "business");

        assertEquals(
                "I used [business]{Airbnb}.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void multiWordToken_matchesFlexibleWhitespace() {
        String review = "Trip to San  Francisco was fun.";
        Map<String, String> tokens = tokens("san francisco", "city");

        assertEquals(
                "Trip to [city]{San  Francisco} was fun.",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void noMatches_returnsOriginalReview() {
        String review = "Nothing to tag here.";
        Map<String, String> tokens = tokens("airbnb", "business");

        assertEquals(review, ReviewLabeler.label(review, tokens));
    }

    @Test
    void emptyReview_returnsEmpty() {
        assertEquals("", ReviewLabeler.label("", tokens("airbnb", "business")));
    }

    @Test
    void emptyTokenMap_returnsOriginalReview() {
        String review = "Airbnb in San Francisco.";
        assertEquals(review, ReviewLabeler.label(review, Map.of()));
    }

    @Test
    void mixedCaseTokenInMap_stillMatchesCaseInsensitively() {
        String review = "welcome to SAN FRANCISCO";
        Map<String, String> tokens = tokens("San Francisco", "city");

        assertEquals(
                "welcome to [city]{SAN FRANCISCO}",
                ReviewLabeler.label(review, tokens));
    }

    @Test
    void nonOverlappingMatches_leftToRight() {
        String review = "Airbnb in San Francisco";
        Map<String, String> tokens = tokens("airbnb", "business", "san francisco", "city");

        assertEquals(
                "[business]{Airbnb} in [city]{San Francisco}",
                ReviewLabeler.label(review, tokens));
    }
}
