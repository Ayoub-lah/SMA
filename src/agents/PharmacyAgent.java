package agents;

import jade.core.Agent;

import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.core.behaviours.CyclicBehaviour;

public class PharmacyAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(
                "Pharmacy Agent Started"
        );

        addBehaviour(
                new CyclicBehaviour() {

                    @Override
                    public void action() {

                        ACLMessage msg =
                                receive();

                        if (msg != null) {

                            System.out.println(
                                    "Ordonnance reçue : "
                                            + msg.getContent()
                            );

                            System.out.println(
                                    "Préparation médicaments..."
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
                                    "Dossier patient mis à jour"
                            );

                            send(adminMsg);

                        } else {

                            block();
                        }
                    }
                }
        );
    }
}