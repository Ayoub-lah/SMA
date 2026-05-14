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

        System.out.println(
                "Admin Agent Started"
        );

        addBehaviour(
                new CyclicBehaviour() {

                    @Override
                    public void action() {

                        ACLMessage msg =
                                receive();

                        if (msg != null) {

                            String patientData =
                                    msg.getContent();

                            System.out.println(
                                    "ADMIN reçoit : "
                                            + patientData
                            );

                            try {

                                Connection conn =
                                        DBConnection.connect();

                                PreparedStatement ps =
                                        conn.prepareStatement(
                                                "INSERT INTO patients(nom, consultation, priorite, diagnostic) VALUES (?, ?, ?, ?)"
                                        );

                                ps.setString(
                                        1,
                                        "Ayoub"
                                );

                                ps.setString(
                                        2,
                                        "Cardiologie"
                                );

                                ps.setString(
                                        3,
                                        "CRITIQUE"
                                );

                                ps.setString(
                                        4,
                                        "Urgence Cardiaque"
                                );

                                ps.executeUpdate();

                                System.out.println(
                                        "Patient sauvegardé dans MySQL"
                                );

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        } else {

                            block();
                        }
                    }
                }
        );
    }
}