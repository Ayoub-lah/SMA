package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class EmergencyAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String patientData = msg.getContent();

                    // Extraire nom
                    String nom = "Patient";
                    for (String part : patientData.split("\\|")) {
                        part = part.trim();
                        if (part.startsWith("Nom:"))
                            nom = part.substring(4).trim();
                    }

                    System.out.println();
                    System.out.println(
                            "╔═════════════════════════════════════════════════════╗");
                    System.out.println(
                            "║  🚨  URGENCE — CAS CRITIQUE DÉTECTÉ                ║");
                    System.out.println(
                            "╠═════════════════════════════════════════════════════╣");
                    System.out.printf(
                            "║  Patient     : %-37s║%n", nom);
                    System.out.println(
                            "║  ✓ Ambulance envoyée immédiatement                  ║");
                    System.out.println(
                            "║  ✓ Médecin urgentiste notifié                       ║");
                    System.out.println(
                            "║  ✓ Dossier transmis à l'administration              ║");
                    System.out.println(
                            "╚═════════════════════════════════════════════════════╝");

                    String enriched = patientData
                            + " | Diagnostic:Urgence critique — intervention immédiate";

                    ACLMessage adminMsg =
                            new ACLMessage(ACLMessage.INFORM);
                    adminMsg.addReceiver(
                            new AID("Admin", AID.ISLOCALNAME));
                    adminMsg.setContent(enriched);
                    send(adminMsg);

                } else {
                    block();
                }
            }
        });
    }
}