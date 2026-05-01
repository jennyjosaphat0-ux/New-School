package com.example.NewSchool.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Voye kòd ak enfòmasyon paiement (pou elèv ak paran)
    public void envoyerCodeEleveAvecPaiement(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Inscription confirmée & Paiement requis",
            "Bonjour " + nom + ",\n\n"
            + "Votre inscription à NewScool est confirmée!\n\n"
            + "═══════════════════════════════\n"
            + "📧 Email : " + email + "\n"
            + "🔑 Code  : " + code + "\n"
            + "═══════════════════════════════\n\n"
            + "⚠️  IMPORTANT — PAIEMENT REQUIS :\n"
            + "Des frais d'inscription de 1,000 Gdes sont dus.\n"
            + "Connectez-vous et allez dans 'Mes Paiements' pour payer.\n\n"
            + "Sans paiement, l'accès aux bulletins sera bloqué.\n\n"
            + "🌐 Connexion : http://localhost:9090\n\n"
            + "L'Administration — NewScool");
    }

    // Voye kòd simple (ancienne version — backwards compat)
    public void envoyerCodeEleve(String emailDestinataire, String nomDestinataire,
                                  String nomEleve, String code) {
        envoyer(emailDestinataire,
            "NewScool - Code d'accès de " + nomEleve,
            "Bonjour " + nomDestinataire + ",\n\n"
            + "Le compte de l'élève " + nomEleve + " a été créé.\n\n"
            + "📧 Email : " + emailDestinataire + "\n"
            + "🔑 Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "L'Administration — NewScool");
    }

    public void envoyerCodeProfesseur(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Votre code d'accès Professeur",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte professeur a été créé.\n\n"
            + "📧 Email : " + email + "\n"
            + "🔑 Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "L'Administration — NewScool");
    }

    public void envoyerCodeSecretaire(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Votre code d'accès Secrétaire",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte secrétaire a été créé.\n\n"
            + "📧 Email : " + email + "\n"
            + "🔑 Code  : " + code + "\n\n"
            + "Connectez-vous sur : http://localhost:9090\n\n"
            + "L'Administration — NewScool");
    }

    private void envoyer(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("[EMAIL] Echec vers " + to + " : " + e.getMessage());
        }
    }
}
