package com.yawar.memo.model;

  public class UserModel{
   String userId;
   String userName;
   String lastName;
   String email;
   String phone;
   String secretNumber;


      public UserModel(String userId, String userName, String lastName, String email, String phone,String secretNumber) {
          this.userId = userId;
          this.userName = userName;
          this.lastName = lastName;
          this.email = email;
          this.phone = phone;
          this.secretNumber = secretNumber;
      }

      public String getUserId() {
          return userId;
      }

      public void setUserId(String userId) {
          this.userId = userId;
      }

      public String getUserName() {
          return userName;
      }

      public void setUserName(String userName) {
          this.userName = userName;
      }

      public String getLastName() {
          return lastName;
      }

      public void setLastName(String lastName) {
          this.lastName = lastName;
      }

      public String getEmail() {
          return email;
      }

      public void setEmail(String email) {
          this.email = email;
      }

      public String getPhone() {
          return phone;
      }

      public void setPhone(String phone) {
          this.phone = phone;
      }

      public String getSecretNumber() {
          return secretNumber;
      }

      public void setSecretNumber(String secretNumber) {
          this.secretNumber = secretNumber;
      }
  }
