# android-running-tracker
Android running tracker app built in android studio  
Makes use of MVVM architecture  
Location tracking implemented through google play services API  

Main functionality of this application includes:
 - A cutsom location tracking foreground service to track a run's speed, time, and route
 - A local SQL database to save previous runs
 - Users are able to add notes and ratings to their previous saved runs

Given more time, future fixes would include:
 - Correcting the route saving functionality so that users can view the routes of their previous runs on a google maps window
 - This fix would involve refactoring the SQL logic to store the routes using a new table with a secondary key
