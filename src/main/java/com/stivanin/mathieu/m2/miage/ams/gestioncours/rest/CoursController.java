package com.stivanin.mathieu.m2.miage.ams.gestioncours.rest;

import com.stivanin.mathieu.m2.miage.ams.gestioncours.DTO.StatistiquesCours;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.entities.Cours;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.exceptions.BadDateException;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.exceptions.CoursNotFoundException;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.exceptions.InscriptionException;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.services.GestionCoursMetier;
import com.stivanin.mathieu.m2.miage.ams.gestioncours.services.GestionStatistiqueMetier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/cours")
@CrossOrigin
public class CoursController {

    Logger logger = LoggerFactory.getLogger(CoursController.class);

    @Autowired
    GestionCoursMetier gestionCoursMetier;

    @Autowired
    GestionStatistiqueMetier gestionStatistiqueMetier;

    @GetMapping("/{id}")
    public Cours getCours(@PathVariable("id") Long idCours) {
        logger.info("Obtenir les informations d'un cours");
        try {
            return this.gestionCoursMetier.getCours(idCours);
        } catch (CoursNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Cours Not Found", e);
        }
    }

    @GetMapping("")
    public List<Cours> getListeCours(){
        logger.info("Obtenir la liste de tous les cours");

        return this.gestionCoursMetier.getListeCours();
    }

    @GetMapping("/enseignant")
    public Iterable<Cours> getListeCoursEnseignant(@RequestParam("enseignant") String id) {
        Long idl = Long.parseLong(id);
        logger.info("Obtenir la liste des cours");
        logger.info("Option reçue {}", idl);

        return this.gestionCoursMetier.getCoursEnseignant(idl);
    }

    @GetMapping("/participant")
    public Iterable<Cours> getListeCoursParticipant(@RequestParam("participant") String id) {
        long idl = Long.parseLong(id);
        logger.info("Obtenir la liste des cours");
        logger.info("Option reçue {}", idl);

        return this.gestionCoursMetier.getCoursParticipant(idl);
    }

    @GetMapping("/{id}/inscriptions")
    public Boolean getInscriptionIsPossible(@PathVariable("id") Long idCours) {
        logger.info("Savoir s'il est possible de s'inscrire à un cours");
        try {
            return this.gestionCoursMetier.inscriptionCoursIsPossible(idCours);
        } catch (CoursNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PreAuthorize("#oauth2.hasScope('write') and hasRole('ROLE_ADHERENT') or hasRole('ROLE_ENSEIGNANT')")
    @PutMapping("/{id}/inscriptions")
    public Cours postInscriptionParticipant(@PathVariable("id") Long idCours, @RequestParam("participant") String idPar) {
        long idParticipant = Long.parseLong(idPar);
        logger.info("Ajouter un participant à un nouveau cours");
        logger.info("Option reçue idCours :{} idParticipant {}", idCours, idParticipant);
        try {
            return this.gestionCoursMetier.inscriptionCours(idCours, idParticipant);
        } catch (CoursNotFoundException | InscriptionException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PreAuthorize("#oauth2.hasScope('write') and hasRole('ROLE_ENSEIGNANT')")
    @PostMapping("")
    public Cours postCours(@RequestBody Cours cours) {

        logger.info("Création d'un nouveau cours " + cours);
        try {
            return this.gestionCoursMetier.creerCours(cours);
        } catch (BadDateException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PreAuthorize("#oauth2.hasScope('write') and hasRole('ROLE_PRESIDENT')")
    @GetMapping(path = "/statistiques")
    public StatistiquesCours getStatistiqueAssociation() {
        logger.info("Connaitre les  statistiques de miage sous l'eau ");
        return this.gestionStatistiqueMetier.obtenirStatistiqueCours();
    }
}
