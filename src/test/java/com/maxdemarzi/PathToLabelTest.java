package com.maxdemarzi;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.test.server.HTTP;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PathToLabelTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withFixture(MODEL_STATEMENT)
            .withExtension("/v1", Service.class);

    @Test
    public void shouldFindFirstLabel() {
        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/service/path_to/One/from/1").toString());
        HashMap actual = response.content();
        assertEquals(ONE_MAP, actual);
    }

    @Test
    public void shouldFindSecondLabel() {
        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/service/path_to/Two/from/1").toString());
        HashMap actual = response.content();
        assertEquals(TWO_MAP, actual);
    }

    @Test
    public void shouldFindThreeLabel() {
        HTTP.Response response = HTTP.GET(neo4j.httpURI().resolve("/v1/service/path_to/Three/from/1").toString());
        HashMap actual = response.content();
        assertEquals(THREE_MAP, actual);
    }

    public static final String MODEL_STATEMENT =
            new StringBuilder()
                    .append("CREATE (start:Start)")
                    .append("CREATE (one:One {name:'1'})")
                    .append("CREATE (two:Two {name:'2.0'})")
                    .append("CREATE (twotoo:Two {name:'2.1'})")
                    .append("CREATE (three:Three {name:'3.0'})")
                    .append("CREATE (start)-[:CONNECTS]->(one)")
                    .append("CREATE (one)-[:CONNECTS]->(two)")
                    .append("CREATE (start)-[:CONNECTS]->(twotoo)")
                    .append("CREATE (one)-[:CONNECTS]->(three)")
                    .append("CREATE (two)-[:CONNECTS]->(three)")
                    .append("CREATE (twotoo)-[:CONNECTS]->(three)")
                    .toString();

    static HashMap<String, Object> ONE_MAP = new HashMap<String, Object>(){{
        put("name", "1");
    }};

    static HashMap<String, Object> TWO_MAP = new HashMap<String, Object>(){{
        put("name", "2.0");
    }};

    static HashMap<String, Object> THREE_MAP = new HashMap<String, Object>(){{
        put("name", "3.0");
    }};

}
