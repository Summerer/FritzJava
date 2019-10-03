# How to build

Run `gradlew installDist`.

Copy `build/install/FritzJava` to your desired location and create a valid credentials.txt inside the `FritzJava` directory where the app was built into.

# Running the app

After building the app you can change into the `FritzJava` directory and run the files inside `bin`.
 

# Formatting for credentials.txt
CredentialParser assumes the following prefixes (e.g. username) and ": " as separator.

`username: user`\
 `password: pw`\
 `switchAin: 1234567890`\
 `mailSender: mymail@mail.com`\
 `mailPassword: password`\
 `mailReceiver: myothermail@mail.com`\
 `SMTP_Server: smtp.mail.com`
 
 
 