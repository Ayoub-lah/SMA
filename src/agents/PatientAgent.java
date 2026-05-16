package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;

public class PatientAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                Object[] args  = getArguments();
                String nom     = (args != null && args.length > 0)
                        ? (String) args[0] : "Patient";
                String consult = (args != null && args.length > 1)
                        ? (String) args[1] : "Générale";
                String prio    = (args != null && args.length > 2)
                        ? (String) args[2] : "NORMAL";

                String content = "Nom:" + nom
                        + " | Consultation:" + consult
                        + " | Priorité:" + prio;

                System.out.println(
                        "┌─────────────────────────────────────────────────────┐");
                System.out.println(
                        "│  NOUVELLE DEMANDE PATIENT                           │");
                System.out.println(
                        "├─────────────────────────────────────────────────────┤");
                System.out.printf(
                        "│  %-10s : %-40s│%n", "Nom",     nom);
                System.out.printf(
                        "│  %-10s : %-40s│%n", "Service", consult);
                System.out.printf(
                        "│  %-10s : %-40s│%n", "Priorité", prio);
                System.out.println(
                        "│  → Envoi vers RDVAgent...                           │");
                System.out.println(
                        "└─────────────────────────────────────────────────────┘");

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(new AID("RDV", AID.ISLOCALNAME));
                msg.setContent(content);
                send(msg);
            }
        });
    }
}