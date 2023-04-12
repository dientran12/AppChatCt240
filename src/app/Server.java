package app;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.mysql.cj.jdbc.Blob;

import dao.DAO;

public class Server {

	public Server() {

	}

	public Server(int imageid, int length, byte[] byteimage) {
		this.imageid = imageid;
		this.length = length;
		this.byteimage = byteimage;
	}

	public static ServerThreadBus serverThreadBus;
	public int imageid;
	public static int length;
	public static byte[] byteimage;
	public static int numberThread = 0;
	private static Socket socket;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		serverThreadBus = new ServerThreadBus();
		System.out.println("Server started");
		int clientNumber = 1;
		try {
			serverSocket = new ServerSocket(12345);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}

		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 10, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(8));

		try {
			while (true) {
				socket = serverSocket.accept();
				numberThread++;
				ServerThread serverThread = new ServerThread(socket, clientNumber++);
				serverThreadBus.add(serverThread);
				executor.execute(serverThread);
			}
		} catch (IOException ex) {
			System.out.println("Có lỗi");
		} finally {
			try {
				serverSocket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public void addData(byte[] byteimage) {
		DAO.addData(byteimage);
	}

	public static void getData(int imageId) {
		DAO.getData(imageId);
		byteimage = DAO.byteimage;
		length = byteimage.length;
	}

}
