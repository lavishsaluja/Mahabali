### Challenge for Microsoft's codefundo++: To predict or manage natural disasters.

# Mahabali
(since during kerala floods, people were not able to celebrate [Onam](https://en.wikipedia.org/wiki/Onam) in which it is believed that [Mahabali](https://en.wikipedia.org/wiki/Onam#Mahabali_legend) comes from heaven and thus the name of our project is Mahabali)
> Mahabali is a complete system which comprise of 2 android apps - MAhabali & Mahabali Rescue team (just like Uber & Uber-Driver apps)
They use Google Maps API, real-time location tracking and an intelligent server-side architecture for bringing about an automated search and rescue assistant. Presently it is directed mainly for after - **geological** and **meteorological** rescue operations but later may be scaled to become an advanced search and rescue technology which would be applicable for various disasters.

### Table of Contents
1. [Objective](https://github.com/lavishsaluja/codefundo#objective)
2. [Challenges faced during Indian dust storms 2018 and how Mahabali overcomes them](https://github.com/lavishsaluja/codefundo#challenges-faced-during-kerala-floods-and-how-mahabali-overcomes-them)
3. [How Mahabali works?](https://github.com/lavishsaluja/codefundo#how-mahabali-works)
4. [Technologies Used](https://github.com/lavishsaluja/codefundo#technologies-used)

### Objective
Our main focus while ideation has been [Indian dust storms 2018](https://en.wikipedia.org/wiki/2018_Indian_dust_storms).
We plan to advance the rescue operations using technology and help people by providing help as fast as possible since more than 50 people out of the 215 died were because of slow operations after the storm and slow rescue of people who were in need.
The system will comprise of:

1. **Mahabali App**:
  - The app will provide the users the real-time locations of active rescue teams around them (just like Uber app shows user the real time location of drivers).
  - Provide a functionality to place a request (like immediate rescue/medical help/food/ others).
  - The algorithm will automatically send a notification to the nearest active (not already booked) rescue team and provide them the real-time location of person that called for help.

2. **Mahabali Rescue Team App**:
  - The second app will only allow the authorized rescue teams to login.
  - The app will show rescue teams the real-time location of users who need help and have placed a request.
  - The app will send a notification and pair up the nearest rescue team to the person who place a request for help using Mahabali app.  
  - The app will provide the shortest path to the rescue teams to reach considering the real-time weather updates and real-time location of user who placed the request.

### Challenges faced during Uttar Pradesh Storm 2018 and how Mahabali overcomes them
1. Locating people in need of aid:
> A lot of people who needed help were not located fast because they were not having any suitable communication channel to reach out to the rescue operations, Mahabali fills that gap and help connecting people who might need help with rescue teams in real time.

2. Knowing exact need of people:
> The Mahabali app provides them a couple of options along with a custom field to place request for specific things thus helping rescue teams know what to carry with them when they reach that particular person and thus exponentially improving the time taken earlier to help the people for rescue and other help operations.

3. It took a lot of time to manually reach the people who need help.
> It used to take rescue teams a lot of time in finding exact location of people, since the Mahbali app will keep tracking the users and asking people where they are in their office or house. Thus helping rescue teams with the shortest path to reach the persons' place as well as locate him fast inside that building.

### How Mahabali works?

1. Users will login and see a lot of active rescue teams around them in the app just like when you open uber-app and see a lot of active drivers around you.

2. Users will fill what they need (we call it 'query' here) (there will be some options to select from, like food, immediate rescue, clothing, etc and a custom field to fill).

3. The Algorithm will consider the prioritise the queries like it will prioritise 'immediate rescue' over 'food' and find the nearest rescue team and send them a notification and pair them with that person calling for help thus completely removing any manual work and helping people connect with rescue teams.

4. Rescue Teams will get notification over Mahabali Rescue Team app and the shortest path to travel to the destination (that persons' location) and will carry the things that will be needed according to the query of the person calling for help.

### Further Scope

1. Connecting People with People during disasters:
> People can be provided with portal in which they can offer help to their neighbours and upload the things they have to donate (from food, clothing to money or personal care, etc) and people those who need those things (as they fill it in queries) will be provided by the people who wish to donate thus linking people who want to help with other people in need.

2. Missing People:
> A portal can be provided which can help people in recognising missing people by the photos uploaded by people. Just like PersonFinder launched by Google during Kerala floods recently.


### Technologies Used
1. Android Studio
2. Azure SQL Database
3. Azure Virtual Machine
4. Google maps API
5. Firebase Authentication
