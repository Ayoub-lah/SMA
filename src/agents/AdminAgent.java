package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminAgent extends Agent {

    @Override
    protected void setup() {


        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String patientData = msg.getContent();

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
                        else if (part.startsWith("Priorité:")
                                || part.startsWith("Priorite:"))
                            prio = part.substring(9).trim();
                        else if (part.startsWith("Diagnostic:"))
                            diag = part.substring(11).trim();
                    }

                    // ── Ignorer si diagnostic N/A et déjà en BD
                    if (diag.equals("N/A") && dejaExiste(nom, consult)) {
                        System.out.println(
                                "⚠ Doublon ignoré pour : " + nom);
                        return;
                    }

                    // ── Ignorer si diagnostic N/A et priorité CRITIQUE
                    // (EmergencyAgent envoie sans diagnostic)
                    // On attend le message complet de DoctorAgent/Pharmacy
                    if (diag.equals("N/A") && prio.equals("CRITIQUE")) {
                        System.out.println(
                                "⚠ Message Emergency sans diagnostic — ignoré");
                        return;
                    }

                    sauvegarder(nom, consult, prio, diag);

                } else {
                    block();
                }
            }
        });
    }

    // ── Vérifier si patient existe déjà dans les 10 dernières secondes
    private boolean dejaExiste(String nom, String consult) {
        Connection conn = DBConnection.connect();
        if (conn == null) return false;
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM patients " +
                            "WHERE nom = ? AND consultation = ? " +
                            "AND id = (SELECT MAX(id) FROM patients)"
            );
            ps.setString(1, nom);
            ps.setString(2, consult);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeQuietly(conn);
        }
        return false;
    }

    // ── Sauvegarder dans MySQL
    private void sauvegarder(String nom, String consult,
                             String prio, String diag) {
        Connection conn = DBConnection.connect();
        if (conn == null) {
            System.out.println("ERREUR: Connexion MySQL échouée");
            return;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO patients" +
                            "(nom, consultation, priorite, diagnostic) " +
                            "VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, nom);
            ps.setString(2, consult);
            ps.setString(3, prio);
            ps.setString(4, diag);
            ps.executeUpdate();
            ps.close();
            // Dans sauvegarder(), remplace les println par :
            System.out.println();
            System.out.println(
                    "┌─────────────────────────────────────────────────────┐");
            System.out.println(
                    "│  AdminAgent — Sauvegarde Dossier Patient            │");
            System.out.println(
                    "├─────────────────────────────────────────────────────┤");
            System.out.printf(
                    "│  Nom         : %-37s│%n", nom);
            System.out.printf(
                    "│  Consultation: %-37s│%n", consult);
            System.out.printf(
                    "│  Priorité    : %-37s│%n", prio);
            System.out.printf(
                    "│  Diagnostic  : %-37s│%n", diag);
            System.out.println(
                    "│  ✓ Dossier sauvegardé dans MySQL                    │");
            System.out.println(
                    "└─────────────────────────────────────────────────────┘");
            System.out.println();
        } catch (Exception e) {
            System.out.println("ERREUR MySQL : " + e.getMessage());
        } finally {
            DBConnection.closeQuietly(conn);
        }
    }
}