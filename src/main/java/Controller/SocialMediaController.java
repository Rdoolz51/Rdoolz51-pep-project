package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your
 * controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a
 * controller may be built.
 */
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        accountService = new AccountService();
        messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in
     * the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * 
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessageHandler);
        app.get("/messages/{message_id}", this::getMessageByMessageIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::patchMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        return app;
    }

    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.addAccount(account);
        if (addedAccount == null) {
            ctx.status(400);
        } else {
            ctx.json(mapper.writeValueAsString(addedAccount));
        }
    }

    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account loginAccount = accountService.loginAccount(account);

        if (loginAccount == null) {
            ctx.status(401);
        } else {
            ctx.json(mapper.writeValueAsString(loginAccount));
        }
    }

    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Account account = accountService.getAccountById(message.getPosted_by());

        if (message.getMessage_text().length() >= 255 || message.getMessage_text() == "" ||
                account == null) {
            ctx.status(400);
        }

        Message addedMessage = messageService.addMessage(message);

        ctx.json(mapper.writeValueAsString(addedMessage));

    }

    private void getAllMessageHandler(Context ctx) throws JsonProcessingException {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void getMessageByMessageIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.getMessageByMessageId(id);
        if (message != null) {
            ctx.json(mapper.writeValueAsString(message));
        } else {
            ctx.status(401);
        }
    }

    private void deleteMessageByIdHandler(Context ctx) throws JsonProcessingException {
        int id = Integer.parseInt(ctx.pathParam("message_id"));
        Message message = messageService.deleteMessageById(id);

        if (message != null) {
            ctx.json(message);
        } else {
            ctx.status(200);
        }
    }

    private void patchMessageByIdHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message body = mapper.readValue(ctx.body(), Message.class);
        int id = Integer.parseInt(ctx.pathParam("message_id"));

        if (body.getMessage_text().length() >= 255 ||
                body.getMessage_text() == "" ||
                messageService.getMessageByMessageId(id) == null) {
            ctx.status(400);
        }

        Message message = messageService.patchMessageById(id, body.getMessage_text());

        ctx.json(mapper.writeValueAsString(message));
    }

    private void getMessagesByAccountIdHandler(Context ctx) throws JsonProcessingException {
        int id = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> messages = messageService.getMessagesByAccountId(id);

        ctx.json(messages);
    }

    /**
     * This is an example handler for an example endpoint.
     * 
     * @param context The Javalin Context object manages information about both the
     *                HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }

}