package bullscows;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static Scanner scanner = new Scanner(System.in);

    // Create char[] with all possible characters available to the game
    private static final char[] POSSIBLE_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    public static void main(String[] args) {
        // initializing the variables needed
        String secretCode = "";
        int turnCounter = 0, length = 0, numberOfSymbols = 0;
        int win = 0;

        /* Getting the input for length and amount of characters used in the game
            added try catch to stop the programming running into NumberFormatExceptions and added return to stop the
            program running
         */
        System.out.println("Please, enter the secret code's length");
        try {
            length = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Please only enter numbers here");
            return;
        }

        System.out.println("Input the number of possible symbols in the code:");
        try {
            numberOfSymbols = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Please only enter numbers here");
            return;
        }

        // when the Input length and numberOfSymbols are valid this creates the secretCode String needed with the
        // given parameters
        if (validateInput(length, numberOfSymbols)) {
            secretCode = getSecretCode(length, numberOfSymbols);
            System.out.println("Okay, let's start a game!");
            /* This loops through the game as long as a valid input guess is detected. Else the game will exit after one
             invalid input
             -1 == invalid input and game will exit
             0 == valid input and game continues
             1 == game won and will exit after
             */
            while (win == 0) {
                turnCounter++;
                System.out.printf("Turn %d:%n", turnCounter);
                win = inputGrader(secretCode, length);
                if (win == -1 || win == 1) {
                    break;
                }
            }
        }
    }

//    public static boolean validateInput(String input, int length) {
//        StringBuilder possibleChars = new StringBuilder();
//        for(int i = 0; i < length; i++) {
//            possibleChars.append(POSSIBLE_CHARS[i]);
//        }
//        String possibleCharString = possibleChars.toString();
//
//        if (input.length() != length) {
//            System.out.printf("Please enter a valid %d-digit number.%n", length);
//            return false;
//        }
//        for (int i = 0; i < input.length(); i++) {
//            if(!possibleCharString.contains(String.valueOf(input.charAt(i)))) {
//                System.out.printf("Error: \"%s\" isn't a valid number.", input);
//                return false;
//            }
//        }
//        return true;
//    }

    public static boolean validateInput(int length, int numberOfSymbols) {
        // check if length is greater than numberOfSymbols as every digit is supposed to be unique
        if (length > numberOfSymbols) {
            System.out.printf("Error: it's not possible ot generate a code with a length of %d with %d unique symbols",
                    length, numberOfSymbols);
            return false;
        }
        // check if length is 0
        else if (length == 0) {
            System.out.println("Error: Cannot create secret code with length of 0!");
            return false;
        }
        // check if numberOfSymbols is greater than 36 as there are only 36 available numbers and small letters
        else if (numberOfSymbols > 36) {
            System.out.println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
            return false;
        }
        // if these check all go through we return true so that the program knows to continue
        return true;
    }

    public static String getSecretCode(int length, int numberOfSymbols) {

        // creating a new char Array and fill them with the amount of symbols the user wants
        char[] selectedChars = new char[numberOfSymbols];
        for (int i = 0; i < numberOfSymbols; i++) {
            selectedChars[i] = POSSIBLE_CHARS[i];
        }

        // Creating a new Random Object, a new HashSet and a new StringBuilder to save all the info to
        Random random = new Random();
        Set<Character> uniqueDigits = new HashSet<>();
        StringBuilder secretCode = new StringBuilder();

        // getting a new Random number to the max of the value of numberOfSymbols
        while (secretCode.length() < length) {
            int digit = random.nextInt(numberOfSymbols);
            // grabbing the char at the position of the random number and saving it to the randomSymbol variable
            char randomSymbol = selectedChars[digit];

            /*
            Checking if the length of the secretCode is 0 and the randomSymbol is 0 as the secretCode is not supposed to begin with a 0
            After that checking if the HashSet contains the randomSymbol as all characters are supposed to be unique
             */
            if ((secretCode.length() == 0 && randomSymbol == 0) || uniqueDigits.contains(randomSymbol)) {
                continue;
            }

            // adding the randomSymbol to the HashSet and the StringBuilder
            uniqueDigits.add(randomSymbol);
            secretCode.append(randomSymbol);
        }

        // grabbing the first and last number available to guess and the first and last letter available to guess
        char firstnumber = selectedChars[0];
        char lastnumber = selectedChars.length < 10 ? selectedChars[selectedChars.length - 1] : selectedChars[9];
        char firstLetter = selectedChars.length > 10 ? selectedChars[10] : ' ';
        char lastLetter = selectedChars.length > 10 ? selectedChars[selectedChars.length - 1] : ' ';

        // Printing out the available chars to guess with to help the user
        if (selectedChars.length > 10) {
            System.out.printf("The secret is prepared %s (%c-%c, %c-%c).%n", "*".repeat(length), firstnumber, lastnumber, firstLetter, lastLetter);
        } else {
            System.out.printf("The secret is prepared %s (%c-%c).%n", "*".repeat(length), firstnumber, lastnumber);
        }

        // returning the secretCode StringBuilder as a String
        return secretCode.toString();
    }

    public static int inputGrader(String secretCode, int length) {
        // Getting the guess from the user via the console
        String input = scanner.nextLine();

        // Checking if the length of the input is equal to the length of the secretCode
        // If not valid returning -1 to show that an error occurred
        if (!validateInput(input.length(), length)) {
            return -1;
        }

        // Initializing the needed variables and arrays
        int bulls = 0;
        int cows = 0;
        boolean[] codeUsed = new boolean[length];
        boolean[] inputUsed = new boolean[length];

        /*
        Iterating through the secretCode String and check if the char at the same position is the same
        If they are the same bulls get + 1 and codeUsed and inputUsed at that position get set to true
        codeUsed and inputUsed is there so the program knows these positions have already been matched and no longer
        need to be checked
         */
        for (int i = 0; i < length; i++) {
            if (input.charAt(i) == secretCode.charAt(i)) {
                bulls++;
                codeUsed[i] = true;
                inputUsed[i] = true;
            }
        }

        /*
        Iterate over the input and check all positions that have not been matched yet to find all
        chars that are the correct char but at the wrong position in the input
         */
        for (int i = 0; i < length; i++) {
            if (!inputUsed[i]) {
                for (int j = 0; j < length; j++) {
                    if (!codeUsed[j] && input.charAt(i) == secretCode.charAt(j)) {
                        cows++;
                        codeUsed[j] = true;
                        break;
                    }
                }
            }
        }

        // Printing dependent on all the game states
        if (bulls == 0 && cows == 0) {
            System.out.println("Grade: None.");
        } else if (cows == 0 && bulls != length) {
            System.out.printf("Grade: %d bull(s).%n", bulls);
        } else if (bulls == 0) {
            System.out.printf("Grade: %d cow(s).%n", cows);
        } else if (bulls == secretCode.length()) {
            System.out.printf("Grade: %d bulls%n" +
                    "Congratulations! You guessed the secret code.", length);
            // returning 1 Here to show the main method that the game was won
            return 1;
        } else {
            System.out.printf("Grade: %d bull(s) and %d cow(s).%n", bulls, cows);
        }
        // returning 0 here to show that the game wasn't won yet and needs to continue looping
        return 0;
    }
}
