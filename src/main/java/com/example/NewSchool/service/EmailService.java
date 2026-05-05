package com.example.NewSchool.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    private static final String SENDER_EMAIL = "jennyjosaphat0@gmail.com";
    private static final String SENDER_NAME  = "NewScool";

    private void envoyer(String to, String subject, String body) {
        try {
            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("api-key", apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{"
                + "\"sender\":{\"name\":\"" + SENDER_NAME + "\",\"email\":\"" + SENDER_EMAIL + "\"},"
                + "\"to\":[{\"email\":\"" + to + "\"}],"
                + "\"subject\":\"" + subject + "\","
                + "\"textContent\":\"" + body.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\""
                + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 201) {
                System.out.println("[EMAIL] Envoye OK vers " + to);
            } else {
                System.err.println("[EMAIL] Echec HTTP " + code + " vers " + to);
            }
        } catch (Exception e) {
            System.err.println("[EMAIL] Echec vers " + to + " : " + e.getMessage());
        }
    }

    public void envoyerCodeEleveAvecPaiement(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Inscription confirmee & Paiement requis",
            "Bonjour " + nom + ",\n\n"
            + "Votre inscription a NewScool est confirmee!\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "IMPORTANT : Des frais d inscription de 1,000 Gdes sont dus.\n"
            + "Connectez-vous et allez dans Mes Paiements pour payer.\n\n"
            + "Sans paiement, l acces aux bulletins sera bloque.\n\n"
            + "Connexion : https://new-school-7h76.onrender.com\n\n"
            + "L Administration - College Mixte Le Progres");
    }

    public void envoyerCodeEleve(String emailDestinataire, String nomDestinataire,
                                  String nomEleve, String code) {
        envoyer(emailDestinataire,
            "NewScool - Code d acces de " + nomEleve,
            "Bonjour " + nomDestinataire + ",\n\n"
            + "Le compte de l eleve " + nomEleve + " a ete cree.\n\n"
            + "Email : " + emailDestinataire + "\n"
            + "Code  : " + code + "\n\n"
            + "Connexion : https://new-school-7h76.onrender.com\n\n"
            + "L Administration - College Mixte Le Progres");
    }

    public void envoyerCodeProfesseur(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Votre code d acces Professeur",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte professeur a ete cree.\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "Connexion : https://new-school-7h76.onrender.com\n\n"
            + "L Administration - College Mixte Le Progres");
    }

    public void envoyerCodeSecretaire(String email, String nom, String code) {
        envoyer(email,
            "NewScool - Votre code d acces Secretaire",
            "Bonjour " + nom + ",\n\n"
            + "Votre compte secretaire a ete cree.\n\n"
            + "Email : " + email + "\n"
            + "Code  : " + code + "\n\n"
            + "Connexion : https://new-school-7h76.onrender.com\n\n"
            + "L Administration - College Mixte Le Progres");
    }
}