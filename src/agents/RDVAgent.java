package agents;

import jade.core.Agent;

import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.core.behaviours.CyclicBehaviour;

public class RDVAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(
                "RDV Agent Started"
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
                                    "Patient reçu : "
                                            + patientData
                            );

                            if (
                                    patientData.contains("CRITIQUE")
                            ) {

                                System.out.println(
                                        "CAS CRITIQUE !!!"
                                );

                                ACLMessage emergency =
                                        new ACLMessage(
                                                ACLMessage.INFORM
                                        );

                                emergency.addReceiver(
                                        new AID(
                                                "Emergency",
                                                AID.ISLOCALNAME
                                        )
                                );

                                emergency.setContent(
                                        patientData
                                );

                                send(emergency);

                            } else {

                                ACLMessage forward =
                                        new ACLMessage(
                                                ACLMessage.INFORM
                                        );

                                forward.addReceiver(
                                        new AID(
                                                "Doctor",
                                                AID.ISLOCALNAME
                                        )
                                );

                                forward.setContent(
                                        patientData
                                );

                                send(forward);
                            }

                        } else {

                            block();
                        }


                    }
                }
        );
    }
}