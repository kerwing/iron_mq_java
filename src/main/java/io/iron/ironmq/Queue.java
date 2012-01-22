package io.iron.ironmq;

import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * The Queue class represents a specific IronMQ queue bound to a client.
 */
public class Queue {
    final private Client client;
    final private String name;

    Queue(Client client, String name) {
        this.client = client;
        this.name = name;
    }

    /**
    * Retrieves a Message from the queue. If there are no items on the queue, an
    * HTTPException is thrown.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public Message get() throws IOException {
        JSONObject jsonObj = client.get("queues/" + name + "/messages");
        Message msg = new Message();
        msg.setId((String)jsonObj.get("id"));
        msg.setBody(jsonObj.getString("body"));
        if (jsonObj.has("timeout")) {
            msg.setTimeout(jsonObj.getLong("timeout"));
        }
        return msg;
    }

    /**
    * Deletes a Message from the queue.
    *
    * @param id The ID of the message to delete.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void deleteMessage(String id) throws IOException {
        client.delete("queues/" + name + "/messages/" + id);
    }

    /**
    * Deletes a Message from the queue.
    *
    * @param msg The message to delete.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void deleteMessage(Message msg) throws IOException {
        deleteMessage(msg.getId());
    }

    /**
    * Pushes a message onto the queue.
    *
    * @param msg The body of the message to push.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void push(String msg) throws IOException {
        push(msg, 0);
    }

    /**
    * Pushes a message onto the queue.
    *
    * @param msg The body of the message to push.
    * @param timeout The timeout of the message to push.
    *
    * @throws HTTPException If the IronMQ service returns a status other than 200 OK.
    * @throws IOException If there is an error accessing the IronMQ server.
    */
    public void push(String msg, long timeout) throws IOException {
        Message message = new Message();
        message.setBody(msg);
        message.setTimeout(timeout);
        String jsonStr = JSONSerializer.toJSON(message).toString();
        client.post("queues/" + name + "/messages", jsonStr);
    }
}