package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Admin Agent Started");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String patientData = msg.getContent();
                    System.out.println("ADMIN reçoit : " + patientData);

                    // ── Parser le message dynamiquement
                    String nom     = "Inconnu";
                    String consult = "N/A";
                    String prio    = "NORMAL";
                    String diag    = "N/A";

                    String[] parts = patientData.split("\\|");
                    for (String part : parts) {
                        part = part.trim();
                        if (part.startsWith("Nom:"))
                            nom = part.substring(4).trim();
                        else if (part.startsWith("Consultation:"))
                            consult = part.substring(13).trim();
                        else if (part.startsWith("Priorité:") || part.startsWith("Priorite:"))
                            prio = part.substring(9).trim();
                        else if (part.startsWith("Diagnostic:"))
                            diag = part.substring(11).trim();
                    }

                    System.out.println("→ Nom      : " + nom);
                    System.out.println("→ Consult  : " + consult);
                    System.out.println("→ Priorité : " + prio);
                    System.out.println("→ Diag     : " + diag);

                    try {
                        Connection conn = DBConnection.connect();
                        if (conn == null) {
                            System.out.println("ERREUR: Connexion MySQL échouée");
                            return;
                        }

                        PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO patients(nom, consultation, priorite, diagnostic) " +
                                        "VALUES (?, ?, ?, ?)"
                        );
                        ps.setString(1, nom);
                        ps.setString(2, consult);
                        ps.setString(3, prio);
                        ps.setString(4, diag);
                        ps.executeUpdate();
                        ps.close();
                        conn.close();

                        System.out.println("Patient sauvegardé dans MySQL");

                    } catch (Exception e) {
                        System.out.println("ERREUR MySQL : " + e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    block();
                }
            }
        });
    }
}