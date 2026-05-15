package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;

public class PatientAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Patient Agent Started");

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {

                // ── Récupérer les arguments dynamiques
                Object[] args  = getArguments();
                String nom     = (args != null && args.length > 0) ? (String) args[0] : "Patient";
                String consult = (args != null && args.length > 1) ? (String) args[1] : "Générale";
                String prio    = (args != null && args.length > 2) ? (String) args[2] : "NORMAL";

                String content = "Nom:" + nom +
                        " | Consultation:" + consult +
                        " | Priorité:" + prio;

                System.out.println("Patient envoie : " + content);

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID("RDV", AID.ISLOCALNAME));
                msg.setContent(content);
                send(msg);

                System.out.println("Demande médicale envoyée au RDV Agent");
            }
        });
    }
}