package sp_2026.airbnb.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests for URL query-string parsing: basic pairs, flag keys, repeated keys,
 * percent-decoding, fragment stripping, and malformed/edge inputs.
 */
class URLParserTest {

    private static Map<String, List<String>> expected(String[]... entries) {
        Map<String, List<String>> map = new HashMap<>();
        for (String[] entry : entries) {
            String key = entry[0];
            List<String> values = Arrays.asList(Arrays.copyOfRange(entry, 1, entry.length));
            map.put(key, values);
        }
        return map;
    }

    @Test
    void problemExample_simpleKeyValuePairs() {
        assertEquals(
                expected(new String[] {"a", "b"}, new String[] {"c", "d"}),
                URLParser.parseURL("?a=b&c=d"));
    }

    @Test
    void problemExample_noValueKey_treatsAsTrue() {
        assertEquals(
                expected(new String[] {"sd", "True"}),
                URLParser.parseURL("?sd"));
    }

    @Test
    void problemExample_repeatedKey_collectsIntoList() {
        assertEquals(
                expected(new String[] {"a", "b", "c", "d"}),
                URLParser.parseURL("?a=b&a=c&a=d"));
    }

    @Test
    void problemExample_encodedSpaceInValue() {
        assertEquals(
                expected(new String[] {"key", "hello world"}),
                URLParser.parseURL("?key=hello%20world"));
    }

    @Test
    void inputNotStartingWithQuestionMark_returnsEmpty() {
        assertEquals(Map.of(), URLParser.parseURL("a=b&c=d"));
    }

    @Test
    void nullOrEmptyInput_returnsEmpty() {
        assertEquals(Map.of(), URLParser.parseURL(null));
        assertEquals(Map.of(), URLParser.parseURL(""));
    }

    @Test
    void emptyQueryAfterQuestionMark_returnsEmpty() {
        assertEquals(Map.of(), URLParser.parseURL("?"));
    }

    @Test
    void fragment_strippedBeforeParsing() {
        assertEquals(
                expected(new String[] {"a", "b"}),
                URLParser.parseURL("?a=b#section"));
    }

    @Test
    void encodedHashInValue_decodedAfterFragmentStripped() {
        assertEquals(
                expected(new String[] {"key", "foo#bar"}),
                URLParser.parseURL("?key=foo%23bar#section"));
    }

    @Test
    void emptySegments_fromLeadingTrailingOrDoubleAmpersand_areIgnored() {
        assertEquals(
                expected(new String[] {"a", "b"}),
                URLParser.parseURL("?&a=b&"));
        assertEquals(
                expected(new String[] {"a", "b"}, new String[] {"c", "d"}),
                URLParser.parseURL("?&&a=b&&c=d&"));
    }

    @Test
    void emptyValue_isPreserved() {
        assertEquals(
                expected(new String[] {"key", ""}),
                URLParser.parseURL("?key="));
    }

    @Test
    void valueWithMultipleEquals_onlyFirstEqualsSplits() {
        assertEquals(
                expected(new String[] {"a", "b=c"}),
                URLParser.parseURL("?a=b=c"));
    }

    @Test
    void encodedDelimiterInValue_decodedWithoutBreakingStructure() {
        assertEquals(
                expected(new String[] {"formula", "a=b"}),
                URLParser.parseURL("?formula=a%3Db"));
        assertEquals(
                expected(new String[] {"q", "x&y"}),
                URLParser.parseURL("?q=x%26y"));
    }

    @Test
    void encodedDelimiterInKey_decodedAfterSplitting() {
        assertEquals(
                expected(new String[] {"a=b", "1"}),
                URLParser.parseURL("?a%3Db=1"));
    }

    @Test
    void plusSignInValue_decodedAsSpace() {
        assertEquals(
                expected(new String[] {"k", "a b"}),
                URLParser.parseURL("?k=a+b"));
    }

    @Test
    void invalidPercentEncoding_fallsBackToOriginalString() {
        assertEquals(
                expected(new String[] {"k", "bad%ZZ"}),
                URLParser.parseURL("?k=bad%ZZ"));
    }
}
