package agents;

import jade.core.Agent;

import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.core.behaviours.CyclicBehaviour;

public class DoctorAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(
                "Doctor Agent Started"
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
                                    "Consultation patient : "
                                            + patientData
                            );

                            String diagnostic =
                                    "Diagnostic: Hypertension";

                            System.out.println(
                                    diagnostic
                            );

                            ACLMessage ordonnance =
                                    new ACLMessage(
                                            ACLMessage.INFORM
                                    );

                            ordonnance.addReceiver(
                                    new AID(
                                            "Pharmacy",
                                            AID.ISLOCALNAME
                                    )
                            );

                            ordonnance.setContent(
                                    "Ordonnance pour patient | Médicament: Paracétamol"
                            );

                            send(ordonnance);

                            System.out.println(
                                    "Ordonnance envoyée pharmacie"
                            );

                        } else {

                            block();
                        }
                    }
                }
        );
    }
}