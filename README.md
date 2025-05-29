# Weather-Report-Kotlin-App-Final_Project

## Introduction
This is the final project of the Android Mobile App Development Using Kotlin, Android Architectural Design Patterns, & Android Unit Testing courses taught in Information Technology Institute (ITI)'s 9-Month Professional Training Program -- Embedded Systems Track as a part of the intensive Android Automotive subfield under the supervision of Eng. Ahmed Mazen, Eng. Heba Ismail, and Eng. Yasmeen Hosny from Java Education & Technology Services (JETS) department. Additionally, since I wanted something more challenging, I took a quick detour and compiled AAOS 15 from source for my Raspberry Pi 4B and tested the application on it (more on that later). As a reference, until I talk more about it later, here is a quick overview on the app's features:

1. Colourful splashscreen.
2. Fetch current weather & forecast by GPS or through map coordinates.
3. Full Arabic language support.
4. Display weather data in multiple unit systems.
5. Scheduled updates every 14 hours.
6. Scheduled alarms that show notification and display over other apps.
7. Last but not least, since this app is a Jojo reference I pulled no punches when it came to that! From the app icon, the background, all the way to the alarm that plays Birdland.

## App Showcase
Here are some screenshots of the app: both on phone and the Pi. :)
![](./README_Photos/flyer/wezza.png)
![](./README_Photos/pi/pi_home.png)
![](./README_Photos/pi/app_installed.png)
![](./README_Photos/pi/app_home.png)
![](./README_Photos/pi/map.png)

## Architecture Breakdown
The app was built based on MVVM architecture and, I am proud to say, a single-activity design (splash screen is another activity but it does not count!). I will lay down the hierarchy for now until I comeback and put some class/sequence diagrams.

### Alarm Feature
Alarm consists of a single fragment, a *foreground service*, and a *broadcast receiver*. Each has a different purpose:
1. Fragment: responsible for UI interactions like creating a new alarm, opening a date/time dialog, starting the service, and preventing the user from creating another alarm until the set one is done.
2. Foreground Service: the actual service that runs and its layout.
3. Broadcast Receiver: accepts interactions from other apps with the alarm popup.

### Weather Details & Home Screen
Both of these are pretty similar: they observe on weather/forecast responses and populate the UI elements with this data. However, the big difference is WHAT is the source of displayed data: in case of `WeatherDetails`, the source is data from the ListOfFavouriteLocations; in case of `HomeScreen`, the source is data about the CurrentLocation.

### Settings Feature
Here, the user can change lots of settings in the app: like the way they like to switch the app language, the way they fetch current location data, or displayed data units. I have created a reliable class called AppliedSystemSettings to, you guessed it, keep track of the applied settings through shared preferences. This one in particular is quite interesting cuz I used lots of new tricks in it like Java Native Interface and other cool tricks like extracting string values from xml string resource file depending on the language.


## Unit Testing
One of the requirements of the project is to perform unit testing for two methods in Repository and any ViewModel respectively. Here is a quick rundown on the approach I took:
1. Local test using JUnit, mockk.
2. Created **Fake Test Double**.
3. Performed State-based testing for Repository. (Asserting value)
4. Performed behavioural testing for Viewmodel. (Verifying correct interaction)
