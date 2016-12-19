/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pis.hu2.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author Stephan Wolfgang Kusch
 */
public class Teilnehmer implements Runnable {
    
    private final Socket m_Socket;
    private String m_Name;
    private final TeilnehmerListe m_ClientList;
    
    public Teilnehmer(Socket socket, TeilnehmerListe clientList){
        
        this.m_Socket = socket;
        this.m_ClientList = clientList;
    }

    @Override
    public void run() {
        
        try{
            
            BufferedReader socket_in = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
            PrintWriter socket_out = new PrintWriter(new OutputStreamWriter(m_Socket.getOutputStream()));
            String read_line = null;
            
            while (!m_Socket.isClosed()){
                
                try{
                    
                    read_line = socket_in.readLine();
                    processMessage(socket_out, read_line);
                }
                catch (SocketTimeoutException ste){
                    
                    ste.printStackTrace();
                }
            }
            
            socket_out.close();
            socket_in.close();
        }
        catch (IOException e){
            
            e.printStackTrace();
        }
        finally{
            
            try{
                
                m_Socket.close();
            }
            catch (IOException e){
                
                e.printStackTrace();
            }
        } 
    }
    
    public synchronized Socket getSocket(){
        
        return m_Socket;
    }

    public synchronized String getName(){
        
        return m_Name;
    }
    
    private synchronized boolean isNameUsed(String name){
        
        for (int i = 0; i < m_ClientList.getSize(); i++)
        {
            if (m_ClientList.get(i).getName().equals(name))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private synchronized boolean isNameCorrect(String name){
        
        return !(name.length() > 30 || name.length() == 0 || name.contains(":"));
    }
    
    public synchronized String getNamelist(){
        
        StringBuilder sb = new StringBuilder("namelist");
        for (int i = 0; i < m_ClientList.getSize(); i++){
            
            sb.append(":").append(m_ClientList.get(i).getName());
        }
        
        return new String(sb);
        
    }
      
    private void commandConnect(PrintWriter socket_out, String message) throws IOException{
        
        if (m_ClientList.findClient(this) <= -1){
        
            m_Name = message;
        }

        if (!isNameUsed(this.getName()) && isNameCorrect(this.getName()) && m_ClientList.findClient(this) <= -1){
            
            m_ClientList.addTeilnehmer(this);
            socket_out.write("connect:ok" + "\n");
            m_ClientList.sendMessage(this.getNamelist(), "namelist");
            Server.getLog().append("Teilnehmer: ").append(this.getName()).append(" hat sich verbunden!\n");
            Server.setChanged(true);
            socket_out.flush();
        }else{
            
            if (m_ClientList.findClient(this) > -1){
                
                Server.getLog().append("Doppelter Anmeldeversuch des Nutzers ").append(this.getName()).append("\n");
                Server.setChanged(true);
            }else if (!isNameCorrect(this.getName())){
                
                Server.getLog().append("Teilnehmer: ").append(this.getName()).append(" verwendet einen ungültigen Namen!\n");
                Server.setChanged(true);
                socket_out.write("refused:invalid_name\n");
                socket_out.flush();
                m_Socket.close();
            }else{
                
                socket_out.write("refused:name_in_use\n");
                socket_out.flush();
                m_Socket.close();
            }
        }     
    }
    
    private boolean commandDisconnect(PrintWriter socket_out, String message) throws IOException{
        
        if(message.equals("")){
            
            m_ClientList.remove(m_ClientList.findClient(this));
            socket_out.write("disconnect:ok" + "\n");
            m_ClientList.sendMessage(this.getNamelist(), "namelist");
            Server.getLog().append(" hat die Verbindung unterbrochen!Teilnehmer: ").append(this.getName()).append("\n");
            Server.setChanged(true);
            socket_out.flush();
            m_Socket.close();
            return true;
        }
        
        return false;        
    }
    
    private void commandDefault(PrintWriter socket_out) throws IOException{
        
        m_ClientList.remove(m_ClientList.findClient(this));
        socket_out.write("disconnect:invalid_command\n");
        Server.getLog().append("Teilnehmer: ").append(this.getName()).append(" hat einen ungültigen Befehl verwendet!\n");
        Server.setChanged(true);
        socket_out.flush();
        m_Socket.close();        
    }
    
    private void processMessage(PrintWriter socket_out, String read_line) throws IOException{
        
            if (read_line != null){
                
                String command = read_line.substring(0, read_line.indexOf(':') + 1);
                String message = read_line.substring(read_line.indexOf(':') + 1, read_line.length());

                switch (command){
                    
                    case "connect:":
                        commandConnect(socket_out, message);
                        break;

                    case "message:":
                        m_ClientList.sendMessage(read_line, this.getName());
                        break;

                    case "disconnect:":
                        // Wenn disconnect:TEXT ankommt, geht es bei default: weiter.
                        if(commandDisconnect(socket_out, message)){
                            
                            break;
                        }

                    default:
                        commandDefault(socket_out);
                        break;
                }

                read_line = null;
            }        
        }
    }
