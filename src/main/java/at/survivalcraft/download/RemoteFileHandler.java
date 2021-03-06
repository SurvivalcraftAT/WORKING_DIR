package at.survivalcraft.download;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoteFileHandler {

    private static int portNumber = 22222;
    private static String host = "survivalcraft.at";

    public static String getSong(int id, Path p) {
        File f = p.toFile();
        if (!f.exists()) {
            f.mkdirs();
        }
        Socket clientSocket = null;
        PrintWriter os = null;
        BufferedReader is = null;
        try {
            clientSocket = new Socket(host, portNumber);
            os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host " + host);
        }
        if (clientSocket != null && os != null && is != null) {
            String responseLine;
            try {
                os.println("testName;passwd;getSong;" + id + ";");
                os.flush();
                System.out.println("Sending request");
                responseLine = is.readLine();
                if (responseLine.endsWith(";;")) {
                    responseLine = responseLine.replaceAll(";;", "");
                    String[] erg = responseLine.split(";");
                    p = Paths.get(p.toString(), erg[0]);
                    new Downloader(p.toString(), erg[1]);
                }
                os.close();
                is.close();
                clientSocket.close();
                return p.toFile().getName();
            } catch (Exception e) {
                System.err.println("IOException:  " + e);
            }
        }
        return "";
    }

}
