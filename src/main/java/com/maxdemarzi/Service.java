package com.maxdemarzi;

import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/service")
public class Service {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/helloworld")
    public Response helloWorld() throws IOException {
        Map<String, String> results = new HashMap<String,String>(){{
            put("hello","world");
        }};
        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }


    @GET
    @Path("/path_to/{label}/from/{id}")
    public Response pathToLabel(@PathParam("label") String label,
                                @PathParam("id") Long id,
                                @Context GraphDatabaseService db) throws IOException {
        HashMap<String, Object> results = new HashMap<String,Object>();

        LabelEvaluator labelEvaluator = new LabelEvaluator(DynamicLabel.label(label));
        PathExpander pathExpander = PathExpanderBuilder.allTypesAndDirections().build();

        TraversalDescription td = db.traversalDescription()
                .breadthFirst()
                .evaluator(labelEvaluator)
                .expand(pathExpander)
                .uniqueness(Uniqueness.RELATIONSHIP_PATH);

        try (Transaction tx = db.beginTx()) {
            Node start = db.getNodeById(id);

            for (org.neo4j.graphdb.Path position : td.traverse(start)) {
                Node found = position.endNode();
                for (String property : found.getPropertyKeys()) {
                    results.put(property, found.getProperty(property));
                }
            }

        }

        return Response.ok().entity(objectMapper.writeValueAsString(results)).build();
    }

}
