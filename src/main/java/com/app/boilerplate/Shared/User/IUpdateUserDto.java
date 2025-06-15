package com.app.boilerplate.Shared.User;

import com.app.boilerplate.Shared.Authentication.Gender;

import java.time.LocalDate;

public interface IUpdateUserDto {
    String getDisplayName();
    String getPhoneNumber();
    Gender getGender();
    LocalDate getDateOfBirth();
}
