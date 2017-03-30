package com.nrkei.microservices.car_rental_offer;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.*;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

// Understands the requirement for advertising on a site
public class SolutionCollector implements River.PacketListener {

    public static void main(String[] args) {
        String host = "localhost";
        String port = "5672";

        final RapidsConnection rapidsConnection = new RabbitMqRapids("solution_collector_java", host, port);
        final River river = new River(rapidsConnection);
        river.requireValue("need", "car_rental_offer");  // Reject packet unless it has key:value pair
        river.interestedIn("need_id");
        river.require("solutions");
        river.register(new SolutionCollector());         // Hook up to the river to start receiving traffic

    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
        System.out.println("----------------------");
        System.out.print("Got solution for " + packet.get("need_id") + " : ");
        System.out.println(packet.getList("solutions").toString());
    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
        System.out.println(String.format(" [x] %s", errors));
    }
}
