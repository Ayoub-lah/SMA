package agents;

import jade.core.Agent;
import jade.core.AID;

import jade.lang.acl.ACLMessage;

import jade.core.behaviours.OneShotBehaviour;

public class PatientAgent extends Agent {

    @Override
    protected void setup() {

        System.out.println(
                "Patient Agent Started"
        );

        addBehaviour(
                new OneShotBehaviour() {

                    @Override
                    public void action() {

                        ACLMessage msg =
                                new ACLMessage(
                                        ACLMessage.REQUEST
                                );

                        msg.addReceiver(
                                new AID(
                                        "RDV",
                                        AID.ISLOCALNAME
                                )
                        );

                        msg.setContent(
                                "Nom:Ayoub | Consultation:Cardiologie | Priorité:CRITIQUE"
                        );

                        send(msg);

                        System.out.println(
                                "Demande médicale envoyée"
                        );
                    }
                }
        );
    }
}