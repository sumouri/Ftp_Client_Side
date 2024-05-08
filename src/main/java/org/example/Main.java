package org.example;
import java.io.*;
import org.apache.commons.net.ftp.*;




























public class Main {

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        boolean running = true;
        while (running) {
            System.out.println("Enter FTP server address:");
            String server;
            try {
                server = consoleInput.readLine();
            } catch (IOException e) {
                System.out.println("Error reading server address: " + e.getMessage());
                return;
            }

            System.out.println("Enter FTP port:");
            int port;
            try {
                port = Integer.parseInt(consoleInput.readLine());
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading port number: " + e.getMessage());
                return;
            }

            System.out.println("Enter username:");
            String username;
            try {
                username = consoleInput.readLine();
            } catch (IOException e) {
                System.out.println("Error reading username: " + e.getMessage());
                return;
            }

            System.out.println("Enter password:");
            String password;
            try {
                password = consoleInput.readLine();
            } catch (IOException e) {
                System.out.println("Error reading password: " + e.getMessage());
                return;
            }

            System.out.println("Enter 'list' to list files :");
            System.out.println("Enter 'upload' to upload a file :");
            System.out.println("Enter 'download' to download a file :");
            System.out.println("Enter 'Delete'to delete file :");
            System.out.println("Enter 'exit' to quit :");
            String operation;
            try {
                operation = consoleInput.readLine().trim().toLowerCase();
            } catch (IOException e) {
                System.out.println("Error reading operation: " + e.getMessage());
                return;
            }

            switch (operation) {
                case "list":
                    listFiles(server, port, username, password);
                    break;
                case "upload":
                    uploadFile(consoleInput, server, port, username, password);
                    break;
                case "download":
                    downloadFile(consoleInput, server, port, username, password);
                    break;
                case "delete":
                    deleteFile(consoleInput, server, port, username, password);
                    break;
                case "exit":
                    System.out.println("Exiting program...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid operation. Please enter 'list', 'upload', 'download','delete' or 'exit'.");
            }
            if (!running) {
                try {
                    FTPClient ftp = new FTPClient();
                    ftp.connect(server, port);
                    ftp.logout();
                    ftp.disconnect();
                    System.out.println("221 Goodbye..");
                } catch (IOException ex) {
                    System.out.println("Error disconnecting from FTP server: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }



    private static void listFiles(String server, int port, String username, String password) {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            boolean successLogin = ftp.login(username, password);
            int replyCode = ftp.getReplyCode(); // Get the reply code after login attempt
            String replyString = ftp.getReplyString(); // Get the reply string after login attempt

            if (successLogin) {
                if (replyCode == 230) { // If login is successful
                    System.out.println("230 Login successful.");
                    System.out.println(replyString); // Display the reply string

                    ftp.enterLocalPassiveMode();

                    FTPFile[] files = ftp.listFiles();
                    System.out.println("Files on the server:");
                    for (int i = 0; i < files.length; i++) {
                        System.out.println((i + 1) + ". " + files[i].getName());
                    }
                } else if (replyCode == 331) { // If password is incorrect
                    System.out.println("Incorrect password. Please try again.");
                } else if (replyCode == 227) { // Entering Passive Mode
                    System.out.println("Entering Passive Mode. " + replyString);
                } else if (replyCode == 257) { // Current directory
                    System.out.println("Current directory: " + replyString);
                } else if (replyCode == 550) { // Unknown error or Couldn't open the file
                    System.out.println("Error: " + replyString);
                } else {
                    System.out.println("Unexpected response: " + replyString);
                }
            } else {
                System.out.println("Login failed. Please check your username and password.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private static void deleteFile(BufferedReader consoleInput, String server, int port, String username, String password) {
        System.out.println("Enter the name of the file to delete:");
        String fileName;
        try {
            fileName = consoleInput.readLine();
        } catch (IOException e) {
            System.out.println("Error reading file name: " + e.getMessage());
            return;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            ftp.login(username, password);
            ftp.enterLocalPassiveMode();

            boolean deleted = ftp.deleteFile(fileName);
            if (deleted) {
                System.out.println("File " + fileName + " deleted successfully.");
            } else {
                System.out.println("Failed to delete file " + fileName + ".");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private static void uploadFile(BufferedReader consoleInput, String server, int port, String username, String password) {
        System.out.println("Enter local file path:");
        String filePath;
        try {
            filePath = consoleInput.readLine();
        } catch (IOException e) {
            System.out.println("Error reading file path: " + e.getMessage());
            return;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            ftp.login(username, password);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);

            boolean success = ftp.storeFile(file.getName(), inputStream);
            inputStream.close();

            if (success) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("550 Couldn't open the file");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void downloadFile(BufferedReader consoleInput, String server, int port, String username, String password) {
        System.out.println("Enter remote file path:");
        String remoteFilePath;
        try {
            remoteFilePath = consoleInput.readLine();
        } catch (IOException e) {
            System.out.println("Error reading remote file path: " + e.getMessage());
            return;
        }

        System.out.println("Enter local destination path:");
        String localDestinationPath;
        try {
            localDestinationPath = consoleInput.readLine();
        } catch (IOException e) {
            System.out.println("Error reading local destination path: " + e.getMessage());
            return;
        }

        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(server, port);
            ftp.login(username, password);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            File localFile = new File(localDestinationPath);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));

            boolean success = ftp.retrieveFile(remoteFilePath, outputStream);
            outputStream.close();

            if (success) {
                System.out.println("File downloaded failed.");
            } else {
                System.out.println("File download successfully.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
