package com.example.NewSchool.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Constructor a lamen pou fòse NetBeans rekonèt JavaMailSender a
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerCodeProfesseur(String email, String nom, String code) {
        envoyer(email,
            "Bienvenue NewSchool - Votre code d'accès",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte professeur a été créé.\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "L'Administration - College Mixte Le Progres");
    }

    public void envoyerCodeSecretaire(String email, String nom, String code) {
        envoyer(email,
            "Bienvenue NewSchool - Accès Secrétariat",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte secrétaire a été créé.\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "L'Administration - NewSchool");
    }

    public void envoyerCodeEleve(String email, String nom, String code) {
        envoyer(email,
            "NewSchool - Votre accès Élève",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte élève a été créé.\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "La Secrétaire - NewSchool");
    }

    private void envoyer(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[EMAIL] Echec envoi vers " + to + " : " + e.getMessage());
        }
    }
}