---
layout: default
title: GreenPlate
description: CS2340 Group 5
---

# Introduction
Step into a world where sustainability meets convenience with GreenPlate, the revolutionary app that transforms the way you manage your food. Say goodbye to food waste and hello to a greener lifestyle as GreenPlate empowers you to make conscious choices that benefit both your well-being and the planet. With our intuitive interface, you can effortlessly track your meals, curate your digital cookbook, and generate smart shopping lists that optimize your pantry and minimize waste. Embark on a journey towards a sustainable future, one delicious meal at a time, with GreenPlate as your trusted companion.

# Design & Architecture
GreenPlate's architecture follows the Model-View-ViewModel (MVVM) pattern. By separating the user interface logic from the business logic, MVVM promotes a clean and organized codebase that is easy to understand, test, and extend.

**Model** represents the data and business logic of the application. It encapsulates the data sources, such as our Firebase database, and provides a structured way to interact with the data. 

**View** is responsible for representing the user interface and providing an interactive experience to the users. It displays the data obtained from the ViewModel and handles user interactions, such as button clicks or input events. It also observes changes in the ViewModel and updates the UI accordingly, ensuring that the displayed information is always up to date.

**ViewModel** serves as an intermediary between the Model and the View layers. It contains the presentation logic and manages the state of the user interface, ensuring that the data is properly formatted and ready for display.
