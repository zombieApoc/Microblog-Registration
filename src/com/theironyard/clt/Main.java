package com.theironyard.clt;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.HashMap;

public class Main {



    public static void main(String[] args) throws SQLException {
        Spark.init();

        Spark.post("/destroy-user", (request, response) -> {
            Session session = request.session();

            session.invalidate();

            response.redirect("/");
            return "";
        });

        Spark.post("/delete-message", (request, response) -> {
            Session session = request.session();

            if (session.attribute("userName") == null) {
                response.redirect("/");
                return "";
            }

            int messageNumber = Integer.valueOf(request.queryParams("messageNum"));
            User currentUser = DataLog.getUser(session.attribute("userName"));

            DataLog.getMessages(currentUser.name);

            DataLog.deleteMessage(messageNumber);

            response.redirect("/");



            return "";
        });


        Spark.post("/edit-message", (request, response) -> {
            Session session = request.session();

            if (session.attribute("userName") == null) {
                response.redirect("/");
                return "";
            }

            String newMessage = request.queryParams("newMessage");
            int messageId = Integer.valueOf(request.queryParams("messageNumber"));


            DataLog.editMessage(newMessage, messageId);

            response.redirect("/");


            return "";
        });

        Spark.get(
                "/",
                (request, response) -> {
                    Session context = request.session();

                    // if we can't get a user from session, show login
                    if (context.attribute("userName") == null) {
                        return new ModelAndView(null, "login.html");
                    } else {
                        User current = DataLog.getUser(context.attribute("userName"));

                        HashMap<String, Object> model = new HashMap<>();

                        model.put("user", current);
                        model.put("messages", DataLog.getMessages(current.name));

                        return new ModelAndView(model, "messages.html");
                    }
                },
                new MustacheTemplateEngine()
        );

        Spark.post("/login",
                (request, response) -> {
                    Session session = request.session();

                    // get name from query string
                    String name = request.queryParams("loginName");
                    String password = request.queryParams("loginPassword");

                    // if user exists at username
                    User tempUser = new User(name);


                    if (password.equals(tempUser.password)) {
                        // save name to session
                        session.attribute("userName", name);


                        // make sure that the users hashmap has an entry with that name
                        DataLog.getUser(name);
                    }


                    response.redirect("/");
                    return "";
                }
        );

        Spark.post("/create-message",
                (request, response) -> {
                    Session session = request.session();
                    String userName = session.attribute("userName");

                    if (userName == null) {
                        response.redirect("/");
                        return "";
                    }

                    // get message from query string
                    String message = request.queryParams("message");

                    User currentUser = DataLog.getUser(session.attribute("userName"));

                    DataLog.addMessage(currentUser.name, message);
                    response.redirect("/");


                    return "";
                }
        );

        Spark.post("/register",
                (request, response) -> {
                    Session session = request.session();


                    String userName = request.queryParams("userName");
                    String firstName = request.queryParams("firstName");
                    String lastName = request.queryParams("lastName");

                    DataLog.addUser(userName,firstName,lastName);
                    response.redirect("/");
                    return "";
                }
        );
    }

}
