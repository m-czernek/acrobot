package com.redhat.resources;

import com.redhat.entities.Acronym;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class RestResource {

    @GET
    @Path("{acronym}")
    public List<Acronym> getByName(@PathParam("acronym") String acronym) {
        return Acronym.find("lower(acronym)", acronym.toLowerCase()).list();
    }

    @PUT
    @Transactional
    public Response save(Acronym acronym) {
        for (var expl : acronym.explanations) {
            if (expl.acronym == null) {
                expl.acronym = acronym;
            }
        }
        Acronym.persist(acronym);
        return Response.status(201).build();
    }

    @POST
    @Transactional
    public Acronym update(Acronym acronym) {
        var oldAcronym = (Acronym) Acronym.find("acronym", acronym.acronym)
                .firstResultOptional()
                .orElseThrow(NotFoundException::new);

        oldAcronym.delete();
        save(acronym);

        return acronym;
    }

    @Path("/{acronym}")
    @DELETE
    @Transactional
    public void delete(@PathParam("acronym") String acronym) {
        Acronym
          .find("lower(acronym)", acronym.toLowerCase())
          .firstResultOptional().orElseThrow(NotFoundException::new)
          .delete();
    }
}
