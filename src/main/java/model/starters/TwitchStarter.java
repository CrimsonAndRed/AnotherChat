package model.starters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import model.Origin;
import model.clients.Client;
import model.clients.TwitchConsoleClient;
import model.emotes.EmotesContainer;
import model.emotes.twitch.*;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;
import model.pools.SecretsPool;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonLoader;
import util.JsonPropertiesReader;
import web.CustomUrlBuilder;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.InputMismatchException;

import static web.StringSender.sendString;

/**
 * THIS CLASS IS TOO COMPLICATED.
 */
public class TwitchStarter implements Starter {

    private final static Logger logger = LogManager.getLogger();
    private final String ADDRESS = "irc.chat.twitch.tv";
    private final int PORT = 6667;
    private final int LOCAL_PORT = 8080;
    private String name;
    private String oauth;
    // TODO SecretsPool
    private String clientId;
    private String clientSecret;


    public Socket getSocket() {
        return socket;
    }

    private Socket socket;
    private BufferedWriter bwriter;
    private BufferedReader breader;

    @Override
    public Client prepareConnection(String channelName) throws InternetException, FileSystemException {

        tryLoadApplicationSecrets();
        loadEmotes();
        // TODO make this authenticate just once
        Pair<String, String> loger = SecretsPool.getSecret(Origin.TWITCHTV);
        if (loger == null) {
            tryAuthenticateWeb();
        } else {
            name = loger.getLeft();
            oauth = loger.getRight();
        }

        try {
            socket = new Socket(ADDRESS, PORT);
            bwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            breader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternetException();
        }
        tryAuthenticateIrc();
        sendString(bwriter, "JOIN #" + channelName);
        logger.debug("Sent this to twitch server: " + "JOIN #" + channelName);
        //TODO maybe sort of resolve, this is stupid
        Client client = new TwitchConsoleClient(socket, bwriter, breader);
        return client;
    }

    private void loadEmotes() throws FileSystemException {
        logger.info("Getting twitch emotes");
        //TODO create EmotesContainer
        if (TwitchEmotesContainer.getInstance() == null) {

            final String pathToEmotes = "temp/TwitchEmotes.json";
            JsonObject obj = new TwitchEmotesLoader().getEmotesAndSave(pathToEmotes);
            logger.info("Got twitch emotes");
            logger.info("Trying to parse twitch emotions");
            JsonArray arr = obj.getAsJsonArray("emoticons");
            logger.info("Got " + arr.size() + " twitch emotions. Is it possible?");

            Gson gson = new Gson();
            //TODO put this in method or class
            EmotesContainer emotes = TwitchEmotesContainer.setInstance(arr.size());
            for (int i = 0; i < arr.size(); i++) {
                TwitchJsonEmotion emotion = gson.fromJson(arr.get(i), TwitchJsonEmotion.class);
                //TODO get(0) ...
                emotes.getMap().put(emotion.getRegex(), emotion.getImages().get(0).getUrl());
            }
            logger.info("Parsed " + emotes.getMap().size() + " twitch emotions in base. Must be equal to " + arr.size());
            logger.info("Getting betterTv emotes");

            final String pathToBetterTv = "temp/BetterTvEmotes.json";
            obj = new BetterTvEmotesLoader().getEmotesAndSave(pathToBetterTv);
            logger.info("Got betterTv emotes");
            logger.info("Trying to parse betterTv emotions");
            JsonArray array = obj.getAsJsonArray("emotes");
            logger.info("Got " + array.size() + " betterTv emotions. Is it possible?");
            //TODO RageFace seems to be in Twitch.tv and BetterTv emotes
            //TODO Replace Twitch emote with Better emote forever
            for (int i = 0; i < array.size(); i++) {
                BetterTvJsonEmotion emotion = gson.fromJson(array.get(i), BetterTvJsonEmotion.class);
                emotes.getMap().put(emotion.getRegex(), emotion.getUrl());
            }
            // -1 because or RageFace
            logger.info("Now twitch has " + emotes.getMap().size() + " emotes in base. Must be equal to " + (array.size() + arr.size() - 1));
        } else {
            logger.info("Twitch emotes are already loaded");
        }
        logger.info("Starting IRC connection...");
        //TODO this is questionable
        System.gc();
    }

    private void tryLoadApplicationSecrets() throws FileSystemException {
        JsonPropertiesReader settingsReader = null;
        try {
            settingsReader = new JsonPropertiesReader("settings.json");
        } catch (InputMismatchException e) {
            logger.error(e);
            throw new FileSystemException();
        }
        clientId = settingsReader.readProperty("clientId");
        clientSecret = settingsReader.readProperty("clientSecret");
    }

    private void tryAuthenticateIrc() throws InternetException {
        sendString(bwriter, "PASS oauth:" + oauth);
        sendString(bwriter, "NICK " + name);
        logger.debug("Sent this to authenticate: <<< " + "PASS " + oauth);
        logger.debug("Sent this to authenticate: <<< " + "NICK " + name);
        String string = null;
        try {
            string = breader.readLine();
            if (string.equals(":tmi.twitch.tv NOTICE * :Improperly formatted auth") ||
                    string.equals(":tmi.twitch.tv NOTICE * :Login authentication failed") ||
                    string.endsWith("Unknown command")) {
                logger.error("Authentication error");
                logger.info("System responded:\n" + string);
                throw new InternetException();
            }
            // 7 messages as twitch irc response
            logger.debug(string);
            logger.debug(breader.readLine());
            logger.debug(breader.readLine());
            logger.debug(breader.readLine());
            logger.debug(breader.readLine());
            logger.debug(breader.readLine());
            logger.debug(breader.readLine());
            logger.info("Authentication completed");

        } catch (IOException e) {
            logger.error("Authentication failed");
            throw new InternetException();
        }
    }

    private void tryAuthenticateWeb() throws InternetException {
        logger.info("Trying to authenticate via web browser");
        String code = null;
        try {
            code = getCodeFromWeb();
            logger.debug("Passed 1st stage of twitch authentication (code)");
        } catch (IOException e) {
            logger.error("Could not pass 1st stage of twitch authentication (code)");
            logger.error(e);
            throw new InternetException();
        }
        // code is not null now
        try {
            getOauthFromWeb(code);
            logger.debug("Passed 2nd stage of twitch authentication (oauth)");
        } catch (IOException e) {
            logger.error("Could not pass 2nd stage of Twitch authentication (oauth)");
            logger.error(e);
            throw new InternetException();
        }

        try {
            getNameByOauth();
            logger.debug("Passed 3nd stage of twitch authentication (name)");
        } catch (IOException e) {
            logger.error("Could not pass 3rd stage of Twitch authentication (name)");
            logger.error(e);
            throw new InternetException();
        }
    }

    private String getCodeFromWeb() throws IOException {
        // Start server on this port
        ServerSocket socket = new ServerSocket(LOCAL_PORT);

        String url = "https://api.twitch.tv/kraken/oauth2/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=http://localhost:8080" +
                "&response_type=code" +
                "&scope=chat_login+user_read" +
                "&force_verify=true";

        Desktop.getDesktop().browse(URI.create(url));
        Socket connectionSocket = socket.accept();

        BufferedReader br = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        String line;
        String header = br.readLine();
        logger.debug("Browser responded:\n--------------------------------------------------------------------");
        logger.debug(header);
        while ((line = br.readLine()).length() != 0) {
            // Logic goes here
            logger.debug(line);
        }
        logger.debug("\n--------------------------------------------------------------------");
        int codeAttr = header.indexOf("code=");
        int ampAttr = header.indexOf('&');
        if (codeAttr == -1 || ampAttr == -1) {
            logger.error("Failed to get Oauth2");
            throw new IOException();
        }
        String code = header.substring(codeAttr + 5, ampAttr);
        logger.debug("code is " + code);
        DataOutputStream os = new DataOutputStream(connectionSocket.getOutputStream());
        //TODO do something better
        os.writeUTF("HTTP/1.1 200 OK\r\n");
        os.flush();
        os.close();
        connectionSocket.close();
        socket.close();
        return code;
    }

    private void getOauthFromWeb(String code) throws IOException {
        BufferedReader br = null;
        //TODO TODO TODO TODO TODO TODO TODO TODO TODO
        //TODO no secrets, change auth2 get flow  TODO
        //TODO TODO TODO TODO TODO TODO TODO TODO TODO

        URL u = new URL(
                "https://api.twitch.tv/kraken/oauth2/token" +
                        "?client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&code=" + code +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=http://localhost:8080"// +
                //               "&state=c3ab8aa609ea11e793ae92361f002671"
        );

        br = new BufferedReader(new CustomUrlBuilder(u).setMethod("POST").getStream());
        Gson gson = new Gson();
        JsonReader reader = gson.newJsonReader(br);
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("access_token")) {
                oauth = reader.nextString();
                logger.debug(oauth + " <<< this is token");
                break;
            }
        }
        reader.close();
        br.close();

    }

    private void getNameByOauth() throws IOException {
        BufferedReader br = null;

        URL u = new URL(
                "https://api.twitch.tv/kraken/user"
        );

//        br = new BufferedReader(new CustomUrlBuilder(u).setMethod("GET")
//                                                        .setAttribute("Authorization", "OAuth " + oauth)
//                                                        .setAttribute("Client-ID", clientId)
//                                                        .setAttribute("Accept", "application/vnd.twitchtv.v5+json")
//                                                        .getStream());
        /*Gson gson = new Gson();
        JsonReader reader = gson.newJsonReader(br);
        reader.beginObject();
        while (reader.hasNext()) {
            if (reader.nextName().equals("name")) {
                name = reader.nextString();
                logger.debug(name + " <<< this is name");
                break;
            }
        }
        reader.close();*/
        InputStreamReader s = new CustomUrlBuilder(u).setMethod("GET")
                .setAttribute("Authorization", "OAuth " + oauth)
                .setAttribute("Client-ID", clientId)
                .setAttribute("Accept", "application/vnd.twitchtv.v5+json")
                .getStream();
        JsonLoader load = new JsonLoader();
        JsonObject obj = load.getJsonFromStream(s);
        name = obj.get("name").getAsString();
        s.close();
//        br.close();

    }
}
