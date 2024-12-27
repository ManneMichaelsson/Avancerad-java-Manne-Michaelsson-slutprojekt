package com.example.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.scene.control.TextArea;

public class HelloController implements Initializable {

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldAuthor;

    @FXML
    private TextArea textArea_allBooks;

    @FXML
    private TextField textFieldChangeBookTitle;

    @FXML
    private TextField textFieldChangeBookAuthor;

    @FXML
    private ChoiceBox<String> btnChoiceBoxChangeBook;

    //Sätter värde i dropdown för changeBook
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashMap<Integer, Book> booksHashMap = getAllBooksList();

        List<String> bookIdsAsStrings = booksHashMap.keySet().stream()
                .map(String::valueOf) // Konvertera varje ID (int) till String
                .collect(Collectors.toList());

        btnChoiceBoxChangeBook.setItems(FXCollections.observableArrayList(bookIdsAsStrings));

        btnChoiceBoxChangeBook.setOnAction(this::setChangeBookTextFields);
    }

    //Sätter värde i textfield för changeBookfälten
    private void setChangeBookTextFields(javafx.event.ActionEvent actionEvent) {
        HashMap<Integer, Book> booksHashMap = getAllBooksList();

        //Hämtar valt ID från ChoiceBox
        String selectedBookIdString = btnChoiceBoxChangeBook.getValue();
        if (selectedBookIdString != null) {
            try {
                int selectedBookId = Integer.parseInt(selectedBookIdString);

                Book selectedBook = booksHashMap.get(selectedBookId);

                if (selectedBook != null) {
                    textFieldChangeBookTitle.setText(selectedBook.getTitle());
                    textFieldChangeBookAuthor.setText(selectedBook.getAuthor());
                } else {
                    textArea_allBooks.setText("Boken med ID " + selectedBookId + " finns inte.");
                }
            } catch (NumberFormatException e) {
                textArea_allBooks.setText("Ogiltigt ID: " + selectedBookIdString);
            }
        } else {
            textArea_allBooks.setText("Ingen bok vald.");
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader;
        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }

    public void onSaveChangeBookButtonClick(javafx.event.ActionEvent actionEvent) {
        int bookId = Integer.parseInt(btnChoiceBoxChangeBook.getValue());
        String bookName = textFieldChangeBookTitle.getText();
        String author = textFieldChangeBookAuthor.getText();

        Book updatedBook = new Book(bookId, bookName, author);

        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String updatedBookJson = objectMapper.writeValueAsString(updatedBook);

            //Skicka URL länken som förfrågan, tar länken plus det bookId som användaren har skrivit in.
            URL url = new URL("http://localhost:8080/books/" + bookId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //Sätter in put förfrågan
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = updatedBookJson.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }
            //Kolla server status på förfrågan
            String jsonResponse = readResponse(connection);
            textArea_allBooks.setText("You've updated the book! New details: " + jsonResponse);

            //error hantering
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onAddBookButtonClick(javafx.event.ActionEvent actionEvent) {
        try {
            //Läs värden från textfield Fälten
            int bookId = Integer.parseInt(textFieldId.getText());
            String bookName = textFieldName.getText();
            String author = textFieldAuthor.getText();

            //Skapa nytt book objekt med de värden från textfield
            Book myBook = new Book(bookId, bookName, author);

            //Lägger om det till en Json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(myBook);

            //Skickar post förfrågan
            URL url = new URL("http://localhost:8080/books");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input);
            }

            String jsonResponse = readResponse(connection);
            textArea_allBooks.setText("You've added a book to the library! Details: " + jsonResponse);
            String myBookIdString = String.valueOf(myBook.getId());

            //gör textfields tomma efter lyckad använding
            textFieldId.setText("");
            textFieldName.setText("");
            textFieldAuthor.setText("");

            //lägger till nya boken i dropdown i change book
            btnChoiceBoxChangeBook.getItems().add(myBookIdString);
        } catch (IOException e) {
            textArea_allBooks.setText("Error: Could not connect to server. Is it running?");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            textArea_allBooks.setText("Error: Invalid input. Please enter a valid number for ID.");
            e.printStackTrace();
        } catch (Exception e) {
            textArea_allBooks.setText("Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onReadAllBookButtonClick(javafx.event.ActionEvent actionEvent) {
        try {
            URL url = new URL("http://localhost:8080/books");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String jsonResponse = readResponse(connection);

                ObjectMapper mapper = new ObjectMapper();
                List<Book> bookList = Arrays.asList(mapper.readValue(jsonResponse, Book[].class));

                // Bygg formaterad sträng
                StringBuilder formattedBooks = new StringBuilder();
                for (Book book : bookList) {
                    formattedBooks.append("Id: ").append(book.getId())
                            .append(", Title: ").append(book.getTitle())
                            .append(", Author: ").append(book.getAuthor())
                            .append("\n");
                }
                textArea_allBooks.setText(formattedBooks.toString());
            }

            else{
                textArea_allBooks.setText("Error: Could not connect to server.");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDeleteBookButtonClick(javafx.event.ActionEvent actionEvent) {
        try {
            //Läs av användarens val av book
            String bookIdString = btnChoiceBoxChangeBook.getValue();

            //kollar så inte det är tomt
            if (bookIdString == null || bookIdString.isEmpty()) {
                textArea_allBooks.setText("Error: Please select a book.");
                return;
            }

        int bookId = Integer.parseInt(bookIdString);

        //Skickar post förfrågan
            URL url = new URL("http://localhost:8080/books/" + bookId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //om server svarar ok
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
            textArea_allBooks.setText("You've deleted the book");
            btnChoiceBoxChangeBook.getItems().remove(bookIdString);

            //Tar bort text i fälten
            textFieldChangeBookTitle.setText("");
            textFieldChangeBookAuthor.setText("");
            }
            else
            {
                String errorResponse = readResponse(connection);
                textArea_allBooks.setText("Error: Could not connect to server. Server responded: " + errorResponse);
            }

        } catch (ProtocolException ex) {
            throw new RuntimeException(ex);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

        public HashMap<Integer, Book> getAllBooksList() {
        try {
            URL url = new URL("http://localhost:8080/books");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String jsonResponse = readResponse(connection);

                ObjectMapper mapper = new ObjectMapper();
                List<Book> bookList = Arrays.asList(mapper.readValue(jsonResponse, Book[].class));

                HashMap<Integer, Book> bookHashMap = new HashMap<>();
                for (Book book : bookList) {
                    bookHashMap.put(book.getId(), book);
                }
                return bookHashMap;
            }
            else{
                textArea_allBooks.setText("Error: Could not connect to server.");
                return null;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}