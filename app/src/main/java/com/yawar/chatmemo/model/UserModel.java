package com.yawar.chatmemo.model;

  public class UserModel{
   String userId;
   String userName;
   String lastName;
   String email;
   String phone;


      public UserModel(String userId, String userName, String lastName, String email, String phone) {
          this.userId = userId;
          this.userName = userName;
          this.lastName = lastName;
          this.email = email;
          this.phone = phone;
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
  }
