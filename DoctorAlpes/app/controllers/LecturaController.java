package controllers;

import akka.dispatch.MessageDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import dispatchers.AkkaDispatcher;
import models.*;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import static play.libs.Json.toJson;

/**
 * Created by aj.paredes10 on 15/02/2017.
 */
public class LecturaController extends Controller{

    public CompletionStage<Result> getLecturas()
    {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(() -> { return LecturaEntity.FINDER.all(); } ,jdbcDispatcher)
                .thenApply(lecturas -> {return ok(toJson(lecturas));}
                );
    }

    public CompletionStage<Result> getLectura(Long idE)
    {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.
                supplyAsync(() -> { return LecturaEntity.FINDER.byId(idE); } ,jdbcDispatcher)
                .thenApply(lecturas -> {return ok(toJson(lecturas));}
                );
    }

    public CompletionStage<Result> createLectura(Long idHist)
    {
        JsonNode n = request().body().asJson();

        LecturaEntity lectura = Json.fromJson( n , LecturaEntity.class ) ;
        return CompletableFuture.supplyAsync(
                ()->{
                    HistorialDeMedicionEntity historial = HistorialDeMedicionEntity.FINDER.byId(idHist);
                    lectura.setFecha(new Date());
                    lectura.setHistorialMedicion(historial);
                    historial.addLectura(lectura);
                    lectura.save();
                    historial.update();
                    return lectura;
                }
        ).thenApply(
                lecturas -> {
                    return ok(Json.toJson(lecturas));
                }
        );
    }

    public CompletionStage<Result> deleteLectura(Long idE)
    {
        MessageDispatcher jdbcDispatcher = AkkaDispatcher.jdbcDispatcher;

        return CompletableFuture.supplyAsync(
                ()->{
                    LecturaEntity lec = LecturaEntity.FINDER.byId(idE);
                    lec.delete();
                    return lec;
                }
        ).thenApply(
                lecturas -> {
                    return ok(Json.toJson(lecturas));
                }
        );
    }


    public CompletionStage<Result> updateLectura( Long idE)
    {
        JsonNode n = request().body().asJson();
        LecturaEntity m = Json.fromJson( n , LecturaEntity.class ) ;
        LecturaEntity antiguo = LecturaEntity.FINDER.byId(idE);

        return CompletableFuture.supplyAsync(
                ()->{
                    antiguo.setId(m.getId());
                    antiguo.setFrecuenciaCardiaca(m.getFrecuenciaCardiaca());
                    antiguo.setNivelEstres(m.getNivelEstres());
                    antiguo.setPresionDiastolica(m.getPresionDiastolica());
                    antiguo.setPresionSistolica(m.getPresionSistolica());
                    //antiguo.setFecha(m.getFecha());

                    antiguo.update();
                    return antiguo;
                }
        ).thenApply(
                lecturas -> {
                    return ok(Json.toJson(lecturas));
                }
        );
    }


}
