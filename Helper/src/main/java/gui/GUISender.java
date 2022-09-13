package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.awt.event.ActionEvent;

public class GUISender extends JFrame {

	private JPanel contentPane;
	private JTextField txtMssv;
	private JTextField txtTen;
	private JTextField txtNgaySinh;
	
	Person p = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUISender frame = new GUISender();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUISender() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 531, 199);
		contentPane = new JPanel();
		contentPane.setBorder(null);

		setContentPane(contentPane);
		
		JLabel lblMssv = new JLabel("Mssv");
		contentPane.add(lblMssv);
		
		txtMssv = new JTextField();
		contentPane.add(txtMssv);
		txtMssv.setColumns(10);
		
		JLabel lblTen = new JLabel("Ten");
		contentPane.add(lblTen);
		
		txtTen = new JTextField();
		txtTen.setColumns(10);
		contentPane.add(txtTen);
		
		JLabel lblNgaySinh = new JLabel("Ngay Sinh");
		contentPane.add(lblNgaySinh);
		
		txtNgaySinh = new JTextField();
		txtNgaySinh.setColumns(10);
		contentPane.add(txtNgaySinh);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sender();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			private void sender() throws Exception {
				String temp = txtMssv.getText().trim();
				long mssv = Long.parseLong(temp);
				String ten = txtTen.getText().trim();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

				String dateInString = txtNgaySinh.getText();
				Date ngaysinh = formatter.parse(dateInString);
				BasicConfigurator.configure();
				Properties settings=new Properties();
				settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
						"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
				settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
				Context ctx=new InitialContext(settings);
				ConnectionFactory factory=
						(ConnectionFactory)ctx.lookup("ConnectionFactory");
				Destination destination=
						(Destination) ctx.lookup("dynamicQueues/hongtham");
			
				Connection con=factory.createConnection("admin","admin");
			
				con.start();
				
				Session session=con.createSession(
						false,
						Session.AUTO_ACKNOWLEDGE
						);
				MessageProducer producer = session.createProducer(destination);
				Message msg=session.createTextMessage("hello mesage from ActiveMQ");
				producer.send(msg);
				p=new Person(mssv, ten, ngaysinh);
				String xml=new XMLConvert<Person>(p).object2XML(p);
				msg=session.createTextMessage(xml);
				producer.send(msg);
				
				session.close();con.close();
				System.out.println("Finished...");
				
			}
		});
		contentPane.add(btnSend);
	}

}
