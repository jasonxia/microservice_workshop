package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.List;

/**
 * Created by xiay on 30/3/17.
 */
public class Membership implements River.PacketListener{
    public static void main(String[] args) {
        String host = "localhost";
        String port = "5672";

        final RapidsConnection rapidsConnection = new RabbitMqRapids("membership_in_java", host, port);
        final River river = new River(rapidsConnection);
        river.requireValue("need", "car_rental_offer");  // Reject packet unless it has key:value pair
        river.forbid("solutions");
        river.forbid("membership_id"); // Reject packet if the user is already a member
        river.register(new Membership());         // Hook up to the river to start receiving traffic
    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
        try {
            List<Object> solutions = packet.getList("solutions");
            solutions.add("Please join the membership");
            connection.publish(packet.toJson());
        } catch (Exception e) {
            throw new RuntimeException("Could not publish message:", e);
        }
    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
        System.out.println(String.format(" [x] %s", errors));
    }

}
