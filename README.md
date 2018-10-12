### Challenge for Microsoft's codefundo++: To predict or manage natural disasters.

# Mahabali
(since during kerala floods, people were not able to celebrate [Onam](https://en.wikipedia.org/wiki/Onam) in which it is believed that [Mahabali](https://en.wikipedia.org/wiki/Onam#Mahabali_legend) comes from heaven and thus the name of our project is Mahabali)
> Mahabali is a complete system which uses drones, thermal imaging, real-time satellite imagery and an intelligent server-side architecture for bringing about an automated search and rescue assistant. Presently it is directed mainly for flood rescue operations but later may be scaled to become an advanced search and rescue technology which would be applicable for various disasters.

### Table of Contents
1. [Objective](https://github.com/lavishsaluja/codefundo#objective)
2. [Challenges faced during Kerala Floods and how Mahabali overcomes them](https://github.com/lavishsaluja/codefundo#challenges-faced-during-kerala-floods-and-how-mahabali-overcomes-them)
3. [How Mahabali works?](https://github.com/lavishsaluja/codefundo#how-mahabali-works)
4. [Technologies Used](https://github.com/lavishsaluja/codefundo#technologies-used)
5. [FAQs](https://github.com/lavishsaluja/codefundo#faqs)

### Objective
Our main focus while ideation has been [Kerala Floods 2018](https://en.wikipedia.org/wiki/2018_Kerala_floods).
We plan to develop Jarvis which does all work from A-Z in such situations.\
The system will be able to:

1. Send Unmanned aerial vehicles(UAVs) to:
	- **Find** people who may be stranded in floods (and not easily visible from ground) and,
	- **Identify missing people**.
2. Help them **navigate** to safe locations
	- Determine an **optimal path** to take
	- **Accompany them** till they reach the destination.
	- Notify nearest rescue teams for on ground support.

### Challenges faced during Kerala Floods and how Mahabali overcomes them
1. Locating people in need of aid
> UAVs will find groups of people (using thermal imaging) that might not be visible from ground and reach out to them to help them.

2. Searching for missing people
> Drones will recognize faces of people during disaster and match them with the database of our PersonFinder portal database and will return the location of that user to the nearest rescue team.

3. It took a lot of time to manually find an on-ground safe path for navigation
> Drones will return the location to our server which will use a **situation-aware** algorithm and find the most optimal path to travel from that location to the nearest safe zone.

### How Mahabali works?

1. Drones will be sent out for surveying different flood-hit areas. These drones will have thermal imaging cameras integrated with them which would be used to spot people on the ground. The drone will also be fitted with a visual camera for getting a complete visual picture as well as for facial detection which will be used for identifying missing people. 
	- Drone-based systems offer some advantages over helicopters, including ease of deployment,  the ability to fly in tighter quarters, no requirement of trained operators, and lower operating costs. A simple listing of all the benefits of using a drone over helicopters can be found [here](http://www.ta-survey.nl/page.php?id=313&lang=EN)
	- Thermal imaging has an edge over visual imaging when it comes to spotting objects on the ground as it can see through physical obstacles like dust that may hinder a clear view of the complete field of view of the drone. A nice article which explains the benefits of thermal imaging can be found [here](https://www.photonics.com/Articles/Thermal_Camera-Equipped_UAVs_Spot_Hard-to-Find/a63435). Also, a paper describing object detection using thermal imaging can be found [here](http://ijesc.org/upload/66a2285a62996f25fd654b0ca39cd92a.Object%20Identification%20using%20Thermal%20Image%20Processing.pdf), which will be used to identify humans from air.
	- The visual cameras will gather colour images which would give a physical view of the area being surveyed. It will also be used for capturing images faces of the people which will be used for identifying missing people.

> This is how drones will be using thermal imaging to detect the prescence of people during flood or other disasters

![Thermal imaging to detect people](https://github.com/lavishsaluja/codefundo/blob/master/Thermal-1.png)

2. Real-time satellite images will be used for gathering information about the conditions around the flood-hit areas. 
	- These images will be used to map several parameters like the depth of the floods, physical barriers, flooded routes etc. A detailed analysis of the applications of Remote Sensing and GIS in flood management can be found in [this paper](https://www.researchgate.net/publication/230660751_Remote_sensing_and_GIS_Applications_in_Flood_Management)
	- Real-time images would help to develop a situation-aware algorithm as differences between images of the same area would be useful for estimating on-ground changes required for predicting the path to be taken so as to reach the nearest safety zone. Although this is not a complete explanation, (this paper)[https://www.sciencedirect.com/science/article/pii/S0022169415001031] gives some insight on near-real-time estimation of water levels.

![Week by week departure from normal](https://github.com/lavishsaluja/codefundo/blob/master/RainfallKerala.png)

3. A server side architecture which would be the brain of all the operations. This would be connected to the drones and the satellite(s) which supply real-time images based on which it would determine the outputs.
	- The server would montior the path taken by the drone. This would be used for handling the navigation of the drone without the requirement of a human controller.
	- The server would run an object detection algorithm on the images sent to it by the thermal camera of the drone. This algorithm would be used to identify any humans that may occur in the images. A technique similar to (this)[https://medium.com/nanonets/how-we-flew-a-drone-to-monitor-construction-projects-in-africa-using-deep-learning-b792f5c9c471] could also be employed for detection of humans. From this, as well as the coordinates of the drone from there the image was taken, the server would be able to identify any standed people who need assistance.
	- There would be a situation-aware algorithm which would run on the real-time satellite images of the area where the person was identified which would be used to find the safest on-ground path from the location of that person/group of people to the nearest known safe-zone.
	- A drone would be sent to that person to guide him to safety using the safe path identified above.
	- In case a safe path cannot be identified, the nearest rescue group(s) would be notified of the location so that they may manually attempt a rescue operation, by performing an air-lift, for example.
	- There would be a Person Finder portal in place in which the images along with other necessary details on missing people could be added by people who need to report a missing person. The visual camera of the survey drones will be used for capturing images of the face of people who were identified by thermal imaging above. The server would then run a face detection algorithm to compare the face of that person with the database of missing people reported in the Person Finder portal. We can use **Microsoft** Azure [Face API](https://azure.microsoft.com/en-us/services/cognitive-services/face/) to recognize the people in database.

#### Image uploaded before flood by NASA
![Kerala after flood, image by NASA](https://github.com/lavishsaluja/codefundo/blob/master/KeralaBeforeFlood.jpg)

#### Image uploaded After flood by NASA
![Kerala before flood, image by NASA](https://github.com/lavishsaluja/codefundo/blob/master/KeralaAfterFlood.jpg)

### Technologies Used
1. UAVs - Drone DIY kits compatible with Raspberry Pi: (Erle Copter DIY Kit)[https://erlerobotics.com/blog/product/erle-copter-diy-kit/] or similar
2. Dual sensor Thermal imaging and color camera: For example, (FLIR 0100-01-00S Duo Dual Sensor Drone Thermal and Color HD Camera)[https://www.amazon.com/Remote-Control-Systems-Flir436-0100-01-00S-Cameras/dp/B01MU9RLXO/ref=as_li_ss_tl?linkCode=sl1&slotNum=1&linkId=85a45a82c82e65b63e85e1072c2bd76a&keywords=flir+duo+drone+camera&imprToken=JB4sAnDCJ3zlzNqxR9gJNA&tag=dglobe_guide-20&ie=UTF8&qid=1498214485&sr=8-1]
3. (Required if 2 cannot be met) Thermal imaging camera for drones: For example, (FLIR 436-0014-00 Vue Pro, 336, 9mm 30Hz)[https://www.amazon.com/Flir-436-0014-00-Vue-30Hz-Black/dp/B01BFMFPLU]
4. (Required if 2 cannot be met) Visual camera for drones: For example, (DBPOWER Waterproof Action Camera 12MP 1080P)[https://www.amazon.in/DBPOWER-Waterproof-Action-Batteries-Accessories/dp/B00JFMT1RM]
5. Raspberri Pi Zero and Shield: (Raspberri Pi Zero)[https://www.raspberrypi.org/products/raspberry-pi-zero/] with (PXMini Open Autopilot Shield for Raspberry Pi Zero)[https://erlerobotics.com/blog/pxfmini-open-autopilot-shield-for-raspberry-pi-zero/]. 


### FAQs
1. How will drones send the data to servers without Internet connection in floods?
> we can use antennas and other popular methods to exchange data between drones and server without internet connection such as [these](https://support.dronedeploy.com/v1/docs/flying-offline).

2. How will thermal imaging works?
> There are many research papers that show one can detect people with a moving thermal imaging camera like [these](https://ieeexplore.ieee.org/document/6909985). (Note that this is IEEE research publication and you'll need some subscription to view it, we accesed it using BITS internet)
![Thermal Imaging](https://github.com/lavishsaluja/codefundo/blob/master/Thermal-4.gif)

3. How do the server find the optimal path?
> We're yet to prpose the exact algorithm but we have planned to compare the images of area, compare the flood levels (which will be estimated from satellite images of that area) and estimate the changes and predict the safest route to the nearest safest zone.
