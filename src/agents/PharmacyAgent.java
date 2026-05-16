package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class PharmacyAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    String nom        = "Patient";
                    String medicament = "Paracétamol 1000mg";

                    for (String part : content.split("\\|")) {
                        part = part.trim();
                        if (part.startsWith("Nom:"))
                            nom = part.substring(4).trim();
                        else if (part.startsWith("Médicament:"))
                            medicament = part.substring(11).trim();
                    }

                    System.out.println();
                    System.out.println(
                            "┌─────────────────────────────────────────────────────┐");
                    System.out.println(
                            "│  PharmacyAgent — Préparation Médicaments            │");
                    System.out.println(
                            "├─────────────────────────────────────────────────────┤");
                    System.out.printf(
                            "│  Patient     : %-37s│%n", nom);
                    System.out.printf(
                            "│  Médicament  : %-37s│%n", medicament);
                    System.out.println(
                            "│  ✓ Médicament préparé et prêt                       │");
                    System.out.println(
                            "└─────────────────────────────────────────────────────┘");

                } else {
                    block();
                }
            }
        });
    }
}