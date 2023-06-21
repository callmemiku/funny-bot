package com.TgInfo.AbiturientTg.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbiturientUser {

    int id;
    long chatId;
    String surname;
    String nam3;
    String otchestvo;
    String sex;
    String bdate;
    String snils;
    String email;
    String phone;

    public String toString() {
        return "Данные которые вы указали: \n" +
                "\n" + "ФИО: " + getSurname() + " " + getNam3() + " " + getOtchestvo() + ";"+
                "\nДата Рождения: " + getBdate() +";"+ "\nПочта: " + getEmail()+";" +
                "\nПол: " + getSex() + ";" + "\nНомер телефона: " + getPhone() + ";" +
                "\nСНИЛС: " + getSnils();
    }
}
