package agents;

import jade.core.Agent;

import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.core.behaviours.CyclicBehaviour;

public class EmergencyAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(
                "Emergency Agent Started"
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
                                    "URGENCE DETECTEE : "
                                            + patientData
                            );

                            System.out.println(
                                    "Ambulance envoyée"
                            );

                            System.out.println(
                                    "Médecin urgent notifié"
                            );

                            ACLMessage adminMsg =
                                    new ACLMessage(
                                            ACLMessage.INFORM
                                    );

                            adminMsg.addReceiver(
                                    new AID(
                                            "Admin",
                                            AID.ISLOCALNAME
                                    )
                            );

                            adminMsg.setContent(
                                    patientData
                            );

                            send(adminMsg);

                            System.out.println(
                                    "Informations envoyées à Admin"
                            );

                        } else {

                            block();
                        }
                    }
                }
        );
    }
}