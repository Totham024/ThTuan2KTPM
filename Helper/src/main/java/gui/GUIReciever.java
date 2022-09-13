package gui;

import java.awt.EventQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.Font;
import java.util.Properties;
import java.awt.Color;

public class GUIReciever extends JFrame {

	private JPanel contentPane;
	private JTextPane textReceiver;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIReciever frame = new GUIReciever();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public GUIReciever() throws NamingException, JMSException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblReciever = new JLabel("Reciever");
		lblReciever.setBackground(new Color(255, 128, 64));
		lblReciever.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblReciever.setBounds(159, 14, 139, 38);
		contentPane.add(lblReciever);
		
		textReceiver = new JTextPane();
		textReceiver.setBackground(new Color(255, 255, 255));
		textReceiver.setBounds(10, 63, 416, 171);
		Receiver();
		contentPane.add(textReceiver);
	}

	private void Receiver() throws NamingException, JMSException {
		BasicConfigurator.configure();
		Properties settings=new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
				"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		Context ctx=new InitialContext(settings);
		Object obj=ctx.lookup("ConnectionFactory");
		ConnectionFactory factory=(ConnectionFactory)obj;
		Destination destination
		=(Destination) ctx.lookup("dynamicQueues/hongtham");
		Connection con=factory.createConnection("admin","admin");
		con.start();
		Session session=con.createSession(
				false,
				Session.CLIENT_ACKNOWLEDGE
				);
		MessageConsumer receiver = session.createConsumer(destination);
	
		System.out.println(" was listened on queue...");
		receiver.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message msg) {
				try {
					if(msg instanceof TextMessage){
						TextMessage tm=(TextMessage)msg;
						String txt=tm.getText();
						System.out.println("Receiver"+txt);
						textReceiver.setText(txt);
						msg.acknowledge();
					}
					else if(msg instanceof ObjectMessage){
						ObjectMessage om=(ObjectMessage)msg;
						System.out.println(om);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

}
