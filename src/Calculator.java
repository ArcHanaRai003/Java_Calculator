import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Stack;

public class Calculator extends Application {

    private Stack<Double> operandStack = new Stack<>();
    private Stack<String> operatorStack = new Stack<>();
    private TextField display;
    private boolean operatorPressed = false;  // Flag to check if the operator was pressed

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        // Creating the display for the calculator
        display = new TextField();
        display.setEditable(false);
        display.setStyle("-fx-font-size: 30px; -fx-alignment: center-right;");
        display.setPrefHeight(100);

        // Creating buttons for digits and operations
        Button[] numberButtons = new Button[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new Button(String.valueOf(i));
            numberButtons[i].setStyle("-fx-font-size: 20px;");
            numberButtons[i].setPrefSize(80, 80); // this is for the uniform size for all buttons
        }

        Button addButton = new Button("+");
        Button subtractButton = new Button("-");
        Button multiplyButton = new Button("x");
        Button divideButton = new Button("/");
        Button clearButton = new Button("C");
        Button equalsButton = new Button("=");

        addButton.setStyle("-fx-font-size: 20px;");//css rule used in javaFX to set the fontsize
        addButton.setPrefSize(80, 80);
        subtractButton.setStyle("-fx-font-size: 20px;");
        subtractButton.setPrefSize(80, 80);
        multiplyButton.setStyle("-fx-font-size: 20px;");
        multiplyButton.setPrefSize(80, 80);
        divideButton.setStyle("-fx-font-size: 20px;");
        divideButton.setPrefSize(80, 80);
        clearButton.setStyle("-fx-font-size: 20px;");
        clearButton.setPrefSize(80, 80);
        equalsButton.setStyle("-fx-font-size: 20px;");
        equalsButton.setPrefSize(80, 80);

        // Set event handlers for the buttons
        for (int i = 0; i < 10; i++) {
            final int num = i;
            numberButtons[i].setOnAction(e -> handleNumberInput(num));
        }

        addButton.setOnAction(e -> handleOperatorInput("+"));
        subtractButton.setOnAction(e -> handleOperatorInput("-"));
        multiplyButton.setOnAction(e -> handleOperatorInput("x"));
        divideButton.setOnAction(e -> handleOperatorInput("/"));
        clearButton.setOnAction(e -> handleClear());
        equalsButton.setOnAction(e -> handleEquals());

        // Layout for buttons and display
        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setStyle("-fx-padding: 20px;");

        // Arrange buttons to match the desired layout
        buttonGrid.add(numberButtons[7], 0, 0); // 7
        buttonGrid.add(numberButtons[8], 1, 0); // 8
        buttonGrid.add(numberButtons[9], 2, 0); // 9
        buttonGrid.add(addButton, 3, 0);       // "+"

        buttonGrid.add(numberButtons[4], 0, 1); // 4
        buttonGrid.add(numberButtons[5], 1, 1); // 5
        buttonGrid.add(numberButtons[6], 2, 1); // 6
        buttonGrid.add(subtractButton, 3, 1);   // "-"

        buttonGrid.add(numberButtons[1], 0, 2); // 1
        buttonGrid.add(numberButtons[2], 1, 2); // 2
        buttonGrid.add(numberButtons[3], 2, 2); // 3
        buttonGrid.add(multiplyButton, 3, 2);   // "x"

        buttonGrid.add(clearButton, 0, 3);      // "C"
        buttonGrid.add(numberButtons[0], 1, 3); // 0
        buttonGrid.add(equalsButton, 2, 3);     // "="
        buttonGrid.add(divideButton, 3, 3);     // "/"

        VBox root = new VBox(10);
        root.getChildren().addAll(display, buttonGrid);

        Scene scene = new Scene(root, 400, 500); 
        scene.setOnKeyPressed(this::handleKeyPress); 
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Handle numeric input
    private void handleNumberInput(int num) {
        if (operatorPressed) {
            operatorPressed = false;
        }
        display.appendText(String.valueOf(num));  // Append the number to the display
    }

    // Handle operator input
    private void handleOperatorInput(String operator) {
        if (!display.getText().isEmpty() && !operatorPressed) {
            // Push current number to the operand stack
            operandStack.push(Double.parseDouble(display.getText().split(" ")[display.getText().split(" ").length - 1]));
            while (!operatorStack.isEmpty() && precedence(operator) <= precedence(operatorStack.peek())) {
                performOperation();
            }
            operatorStack.push(operator);  // Add the operator to the stack
            display.appendText(" " + operator + " ");  // Append the operator to the display
            operatorPressed = true;  // Set the flag indicating operator was pressed
        }
    }

    // Handle equals button
    private void handleEquals() {
        if (!display.getText().isEmpty()) {
            // Push the last number to the operand stack
            operandStack.push(Double.parseDouble(display.getText().split(" ")[display.getText().split(" ").length - 1]));

            while (!operatorStack.isEmpty()) {
                performOperation();
            }

            double result = operandStack.pop();

            // Check if the result is an integer, and format it accordingly
            if (result == (int) result) {
                display.setText(String.valueOf((int) result));  // Display as an integer
            } else {
                display.setText(String.valueOf(result));  // Display as a decimal number
            }
        }
    }

    // Handle clear button
    private void handleClear() {
        display.clear();
        operandStack.clear();
        operatorStack.clear();
        operatorPressed = false;  // Reset the operator flag
    }

    // Handle key press events for supporting keyboard input
    private void handleKeyPress(KeyEvent event) {
        String input = event.getText();
        if (input.matches("[0-9]")) {
            handleNumberInput(Integer.parseInt(input));
        } else if (input.equals("+")) {
            handleOperatorInput("+");
        } else if (input.equals("-")) {
            handleOperatorInput("-");
        } else if (input.equals("x")) {
            handleOperatorInput("x");
        } else if (input.equals("/")) {
            handleOperatorInput("/");
        } else if (input.equals("=")) {
            handleEquals();
        } else if (input.equals("C")) {
            handleClear();
        }
    }

    // Perform the operation at the top of the operator stack
    private void performOperation() {
        String operator = operatorStack.pop();
        double b = operandStack.pop();
        double a = operandStack.pop();
        double result = 0;

        switch (operator) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "x":
                result = a * b;
                break;
            case "/":
                result = a / b;
                break;
        }

        operandStack.push(result);
    }

    // Determine the precedence of the operators
    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "x":
            case "/":
                return 2;
            default:
                return -1;
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}
