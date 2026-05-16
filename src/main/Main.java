package main;

import database.DBConnection;
import gui.HospitalDashboard;
import gui.PatientFormDialog;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

import javax.swing.*;

public class Main {

    public static HospitalDashboard dashboard;
    public static AgentContainer    container;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            dashboard = new HospitalDashboard();
        });

        try {
            Runtime rt = Runtime.instance();
            Profile p  = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");
            container = rt.createMainContainer(p);

            AgentController doctor    = container.createNewAgent(
                    "Doctor",    "agents.DoctorAgent",    null);
            AgentController rdv       = container.createNewAgent(
                    "RDV",       "agents.RDVAgent",       null);
            AgentController pharmacy  = container.createNewAgent(
                    "Pharmacy",  "agents.PharmacyAgent",  null);
            AgentController admin     = container.createNewAgent(
                    "Admin",     "agents.AdminAgent",     null);
            AgentController emergency = container.createNewAgent(
                    "Emergency", "agents.EmergencyAgent", null);

            emergency.start();
            doctor.start();
            rdv.start();
            pharmacy.start();
            admin.start();

            // Dans main(), après le démarrage des agents, ajoute :
            System.out.println("\n");
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║          HospitalSMA — JADE Multi-Agent System       ║");
            System.out.println("║          FST Tanger — SIT & Big Data 2025/2026       ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  ✓ EmergencyAgent   démarré                          ║");
            System.out.println("║  ✓ DoctorAgent      démarré                          ║");
            System.out.println("║  ✓ RDVAgent         démarré                          ║");
            System.out.println("║  ✓ PharmacyAgent    démarré                          ║");
            System.out.println("║  ✓ AdminAgent       démarré                          ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  STATUS : Système prêt — En attente de patients...   ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.println();

            DBConnection.connect();

            // Patient par défaut au démarrage
            sendPatient("Ayoub", "Cardiologie", "CRITIQUE");

            Thread.sleep(2000);
            SwingUtilities.invokeLater(() -> {
                if (dashboard != null) dashboard.refreshStats();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Méthode publique pour envoyer un patient depuis le GUI
    public static void sendPatient(String nom, String consult, String prio) {
        try {
            // Numéro unique pour éviter conflits de nom d'agent
            String agentName = "Patient_" + System.currentTimeMillis();
            AgentController patient = container.createNewAgent(
                    agentName,
                    "agents.PatientAgent",
                    new Object[]{nom, consult, prio}
            );
            patient.start();

            // Refresh dashboard après 1.5s
            javax.swing.Timer t = new javax.swing.Timer(1500, e -> {
                SwingUtilities.invokeLater(() -> {
                    if (dashboard != null) dashboard.refreshStats();
                });
            });
            t.setRepeats(false);
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}