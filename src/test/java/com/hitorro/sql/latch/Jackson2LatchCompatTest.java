package com.hitorro.sql.latch;

import com.hitorro.sql.latch.latches.*;
import com.hitorro.util.core.iterator.JsonValueSource;
import com.hitorro.util.typesystem.FieldBaseIntf;
import org.junit.BeforeClass;
import org.junit.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Pins what JSONLatchSource reads into each latch type (string, long, int, short, float, double,
 * date, object) from every kind of JSON value (strings, numeric strings, ints, longs, BigIntegers,
 * decimals, booleans, null, objects, arrays, missing) to what hitorro-jsonsql 3.0.2 read on
 * Jackson 2.18 (2.x wrapped big numbers, truncated 3.7 to 3 and read non-numbers as 0; Jackson 3's
 * own accessors throw). The golden file was produced by running the 3.0.2 classes with the same
 * inputs (generator: hitorro-j3 tools/Jackson2JsonsqlGolden.java.txt). LatchSource needs a coercion
 * map; coercion.csv is a copy of hitorro-all data/sql/coercion.csv.
 */
public class Jackson2LatchCompatTest {

    @BeforeClass
    public static void coercionMap() throws Exception {
        File f = Files.createTempFile("coercion", ".csv").toFile();
        f.deleteOnExit();
        try (InputStream in = Jackson2LatchCompatTest.class.getResourceAsStream("coercion.csv")) {
            Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        System.setProperty("jsg.coercion", f.getAbsolutePath());
    }

  static final Map<String, String> out = new LinkedHashMap<>();
  static void p(String k, Object v){ out.put(k, String.valueOf(v).replace("\\","\\\\").replace("\n","\\n").replace("\r","\\r")); }
  static String err(Throwable t){ String n = t.getClass().getName(); return "!" + (n.startsWith("com.fasterxml.jackson") || n.startsWith("tools.jackson") ? "jackson" : t.getClass().getSimpleName()); }
  static FieldBaseIntf field(final String name) {
    return new FieldBaseIntf() {
      public String getName() { return name; }
      public Class getImplementingClass() { return String.class; }
    };
  }
  static Map<String, String> compute() throws Exception {
    out.clear();
    TimeZone prev = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    try {
      ObjectMapper om = com.hitorro.util.json.JsonMappers.json();
      // LatchSource loads ${ht_data}/sql/coercion.csv in its static initializer: point it at a copy
      if (System.getProperty("jsg.coercion") != null) {
        ObjectNode props = (ObjectNode) om.readTree("{\"latchsource\":{\"coercion\":{\"file\":\"file://" + System.getProperty("jsg.coercion") + "\"}}}");
        com.hitorro.util.core.params.GlobalProperties.setDefaultProperties(props);
      }
      String doc = "{\"s\":\"abc\",\"sn\":\"42\",\"sd\":\"3.5\",\"i\":7,\"neg\":-3,\"l\":12345678901,\"big\":123456789012345678901234567890,"
          + "\"d\":3.7,\"dn\":-2.5,\"e\":1e300,\"t\":true,\"f\":false,\"z\":null,\"o\":{\"x\":1},\"a\":[1,2],\"ea\":[],"
          + "\"date\":\"2024-02-29\",\"ts\":1700000000000,\"iso\":\"2024-02-29T10:11:12Z\",\"u\":\"\\u00e9\\uD83D\\uDE00\",\"ws\":\" 5 \"}";
      final JsonNode node = om.readTree(doc);
      JSONLatchSource src = new JSONLatchSource(new com.hitorro.jsontypesystem.Type());
      src.row = new JsonValueSource() {
        @Override public Object getValue(String f) { return node.get(f); }
      };
      src.currentRow = 3;
      List<String> fields = new ArrayList<>();
      node.propertyNames().forEach(fields::add);
      fields.add("missing");
      for (String f : fields) {
        String k = "latch." + f + ".";
        try { StringLatch l = new StringLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "string", ok + " " + l.value + " " + l.validRow + " " + l.currentRow); } catch (Throwable t) { p(k + "string", err(t)); }
        try { LongLatch l = new LongLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "long", ok + " " + l.value + " " + l.validRow); } catch (Throwable t) { p(k + "long", err(t)); }
        try { IntLatch l = new IntLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "int", ok + " " + l.value + " " + l.validRow); } catch (Throwable t) { p(k + "int", err(t)); }
        try { ShortLatch l = new ShortLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "short", ok + " " + l.value + " " + l.validRow); } catch (Throwable t) { p(k + "short", err(t)); }
        try { FloatLatch l = new FloatLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "float", ok + " " + l.value + " " + l.validRow); } catch (Throwable t) { p(k + "float", err(t)); }
        try { DoubleLatch l = new DoubleLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "double", ok + " " + l.value + " " + l.validRow); } catch (Throwable t) { p(k + "double", err(t)); }
        try { DateLatch l = new DateLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "date", ok + " " + (l.value == null ? null : l.value.getTime()) + " " + l.validRow); } catch (Throwable t) { p(k + "date", err(t)); }
        try { ObjectLatch l = new ObjectLatch(src, field(f)); boolean ok = src.get(f, l); p(k + "object", ok + " " + l.value + " " + (l.value == null ? "" : ((JsonNode) l.value).getNodeType()) + " " + l.validRow); } catch (Throwable t) { p(k + "object", err(t)); }
      }
    } finally {
      TimeZone.setDefault(prev);
    }
    return out;
  }

    @Test
    public void latchesReadAsOnJackson2() throws Exception {
        Map<String, String> golden = new LinkedHashMap<>();
        try (InputStream in = getClass().getResourceAsStream("jackson2-latches.txt")) {
            for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (line.isEmpty()) continue;
                int eq = line.indexOf('=');
                golden.put(line.substring(0, eq), line.substring(eq + 1));
            }
        }
        Map<String, String> got = compute();
        for (Map.Entry<String, String> e : golden.entrySet()) {
            assertEquals(e.getKey(), e.getValue(), got.get(e.getKey()));
        }
        assertEquals(golden.keySet(), got.keySet());
    }
}
