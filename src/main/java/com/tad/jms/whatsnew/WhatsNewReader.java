package com.tad.jms.whatsnew;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/jms")
@RequestScoped
public class WhatsNewReader {

	@Resource //(name = "jms/__defaultConnectionFactory")
	private ConnectionFactory connFactory;

	@Resource //(name = "jms/SomeQueue")
	private Queue queue;

	private Logger logger = Logger.getLogger(WhatsNewReader.class
			.getCanonicalName());

	@PostConstruct
	public void init() {
		// List<String> msgs = readMessageContent();
		String msgs = "foo";
		logger.info("Messages: " + msgs);
	}

	public List<String> readMessageContent() {
		List<String> messageBodies = new ArrayList<>();
		try (Connection conn = connFactory.createConnection();
				Session sess = conn.createSession();
				MessageConsumer cons = sess.createConsumer(queue)) {
			Message m = null;
			while ((m = cons.receive()) != null) {
				if (m instanceof TextMessage) {
					TextMessage tm = (TextMessage) m;
					messageBodies.add(tm.getText());
					m.acknowledge();
				}
			}
		} catch (JMSException | JMSRuntimeException e) {

		}
		return messageBodies;
	}

	@GET
	@Produces("text/plain")
	public String getMessages() {
		List<String> msgs = readMessageContent();
		StringBuilder sb = new StringBuilder("Hello,");
		for (String m : msgs) {
			sb.append(m).append("\n");
		}
		return sb.toString();
	}
}
