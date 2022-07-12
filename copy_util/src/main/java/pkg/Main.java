package pkg;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main extends Application {
    enum TYPE {FILE, DIRECTORY}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        final VBox vBox = new VBox(20);
        vBox.setPadding(new Insets(25, 25, 25, 25));
        VBox.setVgrow(vBox, Priority.ALWAYS);

        final Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Copy util");

        final ComboBox<TYPE> comboBox = new ComboBox<>(FXCollections.observableArrayList(TYPE.FILE, TYPE.DIRECTORY));
        comboBox.setValue(TYPE.values()[0]);
        vBox.getChildren().add(comboBox);

        vBox.getChildren().add(new Label("Press INSERT button to call dialog"));

        final TextField from = new TextField();
        from.setOnKeyReleased(event -> set(event.getCode().getName(), comboBox.getValue(), primaryStage, from, true));
        vBox.getChildren().add(new VBox(new Label("From"), from));

        final TextField to = new TextField();
        to.setOnKeyReleased(event -> set(event.getCode().getName(), comboBox.getValue(), primaryStage, to, false));
        vBox.getChildren().add(new VBox(new Label("To"), to));

        final Button button = new Button("Copy");
        button.setOnMouseClicked(event -> {
            final Path source = Path.of(from.getText());
            final Path destination = Path.of(to.getText());
            try {
                if (Files.isRegularFile(source)) {
                    System.out.println("Copy : " + source + " To: " + destination);
                    Copy.copyFile(source, destination);
                } else if (Files.isDirectory(source)) {
                    final List<Path> paths = FileTree.get(source);
                    int i = 1;
                    for (final Path path : paths) {
                        final Path relative = source.relativize(path);
                        final Path destinationPath = destination.resolve(relative);
                        System.out.println(i++ + " / " + paths.size() + " Copy : " + path + " To : " + destinationPath);
                        Copy.copyFile(path, destinationPath);
                    }
                } else {
                    throw new IllegalStateException("Unknown type");
                }
                System.out.println("Copy COMPLETE");
                new Alert(Alert.AlertType.INFORMATION, "Copy complete").show();
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "ERROR").show();
            }
        });
        vBox.getChildren().add(button);

        primaryStage.show();
    }

    private static void set(final String key,
                            final TYPE type,
                            final Stage primaryStage,
                            final TextField textField,
                            final boolean isOpen) {
        if (key.equals("Insert")) {
            final File file = switch (type) {
                case FILE -> {
                    final FileChooser chooser = new FileChooser();
                    yield isOpen ? chooser.showOpenDialog(primaryStage) : chooser.showSaveDialog(primaryStage);
                }
                case DIRECTORY -> new DirectoryChooser().showDialog(primaryStage);
            };
            if (file != null && file.exists()) {
                textField.setText(file.getAbsolutePath());
            }
        }
    }
}
