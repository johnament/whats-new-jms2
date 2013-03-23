package com.tad.jms.whatsnew;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
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
import javax.ws.rs.Produces;

@Path("/jms")
@Stateless
public class WhatsNewReader {

	@Resource(mappedName = "jms/__defaultConnectionFactory")
	private ConnectionFactory connFactory;

	@Resource(mappedName = "jms/SomeQueue")
	private Queue queue;

	private Logger logger = Logger.getLogger(WhatsNewReader.class
			.getCanonicalName());

	public List<String> readMessageContent() {
		List<String> messageBodies = new ArrayList<>();
		logger.info("Reading.");
		try (Connection conn = connFactory.createConnection();
				Session sess = conn.createSession();
				MessageConsumer cons = sess.createConsumer(queue)) {
			logger.info("In the try.");
			Message m = null;
			while ((m = cons.receiveNoWait()) != null) {
				logger.info("In the while.");
				if (m instanceof TextMessage) {
					TextMessage tm = (TextMessage) m;
					messageBodies.add(tm.getText());
					m.acknowledge();
				}
				logger.info("leaving iteration.");
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
