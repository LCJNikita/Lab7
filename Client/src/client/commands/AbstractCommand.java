package client.commands;

import server.Request;
import server.Response;
import server.database.User;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Abstract command class that implements Command interface and can sentd requests to server
 */
public abstract class AbstractCommand implements Command {
    private User user;

    /**
     * Create bytes array from Request object
     *
     * @param request an Request object
     * @return byte array
     * @throws IOException if stream exception caused
     */
    public byte[] createBytesRequest(Request request) throws IOException {
        byte[] bytesRequest;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(request);
            oos.flush();
            bytesRequest = baos.toByteArray();

        } catch (IOException e) {
            throw e;
        }
        return bytesRequest;
    }

    /**
     * send a request to socket and get a response
     *
     * @param request a Request object to sent to server
     * @param socket  a socket connection to server
     * @return a Response obj from server
     * @throws IOException            when working with streams
     * @throws ClassNotFoundException when desialization
     */
    public Response sendRequest(Request request, Socket socket) throws IOException, ClassNotFoundException {
        Response response = null;
        request.setUser(this.getUser());
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);

            response = (Response) in.readObject();

        } catch (IOException e) {
            System.err.println("Got and error when sending request");
            throw e;
        }
        return response;

    }

    public Response sendRequest(Request request, SocketChannel channel) {
        request.setUser(this.getUser());
        Response response = null;
        try (ObjectOutputStream out = new ObjectOutputStream(channel.socket().getOutputStream())) {
            ByteBuffer buffRequest = ByteBuffer.wrap(createBytesRequest(request));
            while (buffRequest.hasRemaining()) {
                channel.write(buffRequest);
            }
            ByteBuffer respBuf = ByteBuffer.allocate(4096);

            while (channel.read(respBuf) < 0) {
                continue;
            }

            byte[] byteResponse = respBuf.array();

            try (ByteArrayInputStream bais = new ByteArrayInputStream(byteResponse);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                response = (Response) ois.readObject();

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Get an error when handling response");
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Got and error when sending request");
            e.printStackTrace();
        }
        return response;
    }

    /**
     * user getter
     *
     * @return user
     */
    @Override
    public User getUser() {
        return user;
    }

    /**
     * user setter
     *
     * @param user user
     */
    @Override
    public void setUser(User user) {
        this.user = user;
    }
}
