package main;

import database.DBConnection;
import gui.HospitalGUI;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {

    public static void main(String[] args) {

        try {

            Runtime rt = Runtime.instance();

            Profile p = new ProfileImpl();

            p.setParameter(
                    Profile.GUI,
                    "true"
            );

            AgentContainer mc =
                    rt.createMainContainer(p);

            AgentController patient =
                    mc.createNewAgent(
                            "Patient",
                            "agents.PatientAgent",
                            null
                    );

            AgentController doctor =
                    mc.createNewAgent(
                            "Doctor",
                            "agents.DoctorAgent",
                            null
                    );

            AgentController rdv =
                    mc.createNewAgent(
                            "RDV",
                            "agents.RDVAgent",
                            null
                    );

            AgentController pharmacy =
                    mc.createNewAgent(
                            "Pharmacy",
                            "agents.PharmacyAgent",
                            null
                    );

            AgentController admin =
                    mc.createNewAgent(
                            "Admin",
                            "agents.AdminAgent",
                            null
                    );

            AgentController emergency =
                    mc.createNewAgent(
                            "Emergency",
                            "agents.EmergencyAgent",
                            null
                    );

            HospitalGUI gui =
                    new HospitalGUI();

            gui.addLog(
                    "System Started"
            );

            emergency.start();
            doctor.start();
            rdv.start();
            pharmacy.start();
            admin.start();
            patient.start();
            DBConnection.connect();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}