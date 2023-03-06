package com.redhat.client;

import com.redhat.entities.Acronym;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient
public interface AcrobotBE {

    @GET
    @Path("{acronym}")
    public List<Acronym> getByName(@PathParam("acronym") String acronym);

    @PUT
    public Response save(Acronym acronym);

    @POST
    public Acronym update(Acronym acronym);

    @DELETE
    @Path("{acronym}")
    public void delete(@PathParam("acronym") String acronym);
}
