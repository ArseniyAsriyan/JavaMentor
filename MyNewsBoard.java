package MailDeliveryTask;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


public class MyNewsBoard {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Приветствую, хозяин, рассказывай, зачем пожаловал?");
        String action;
        do {
            System.out.println("\n-узнать погоду\n-узнать наш будущий минимум\n-выйти");
            action = scanner.nextLine();
                if (action.equalsIgnoreCase("узнать погоду")) {
                    getWeater();
                }
                if (action.equalsIgnoreCase("узнать наш будущий минимум")) {
                    getJavaDevSalary();
                }
        } while (!action.equalsIgnoreCase("выйти"));
    }
    public static void getWeater() {
        URL url = null;
        try {
            url = new URL("https://pogoda7.ru/prognoz/gorod134242-Russia-g_Moskva-Moskva/3days/full");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            Scanner scanner = new Scanner(is);
            System.out.println("Сегодня в Москве: ");
            while (scanner.hasNext()) {
                if (scanner.nextLine().contains(" <div class=\"grid current_name\"><span class=\"span_h2\">Сейчас  в Москве </span></div><div class=\"clear\"></div>  <div class=\"current_data\">    <div class=\"grid image\">      <img title=\"Сейчас в Москве:")) {
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                    System.out.println(scanner.nextLine());
                }
            }
            is.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getJavaDevSalary() {
        URL url1 = null;
        try {
            url1 = new URL("https://moskva.trud.com/salary/693/3331.html");
        } catch (MalformedURLException e) {

        }


        try {
            URLConnection connection = url1.openConnection();
            InputStream is = connection.getInputStream();
            Scanner scanner = new Scanner(is);
            while (scanner.hasNext()) {
                scanner.findInLine("<span class=\"ms__a\">");
                System.out.print("Средняя зарплата Java-девелопера в Москве на сегодняшний день по данным https://moskva.trud.com/   -   " + scanner.nextInt());
                System.out.print(" ");
                System.out.println(scanner.nextInt() + " ₽ в месяц.");
                break;
            }
            is.close();
            scanner.close();
        } catch (IOException e) {
        }
    }
}


