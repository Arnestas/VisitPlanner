# Used techologies
Java 17, Spring Boot 3, Maven, Thymeleaf, SQL database.

# Visit Planner System
This project is a web-based application that allows visitors to book appointments with specialists, 
check their visit details, and login to their accounts. It's built with Java and Spring Boot, 
and it offers various functionalities for visitors and specialists.

## Visitors of the website can choose from three options:
- Reserve time with a specialist,
- Check visit time,
- Login.

## Reservation Details
- All reservations with the specialist begin from the date 2023-09-01 08:00. Each visit lasts 20 minutes.
- Reservations are organized in a queue, with the possibility to check the place in line, the visit time, and time left.
- Users can check their reservation details using their unique reservation number.
- Reservation numbers are generated using timestamps.
- The visit has four possible statuses: Waiting, Started, Finished, and Canceled.
- All visitors can book a visit time to the specialist using the "Reserve time to a specialist" link; login is not needed.
- The specialist can be chosen from a list. The visitor can choose only the specialist, and the possible time of the visit will be calculated.
- Customers can cancel their reservations. 

## User Options
- The possible time to create the visit is the next available slot after the last existing visit. Visits with the status Canceled are considered invalid.
- After making a reservation, the customer can view:
  - Reservation number
  - Place in the waiting line
  - Time of the reservation
  - Time left before the meeting.

## User Roles and Login
- Only existing users can log in to the system.
- There are two possible roles: User and Admin.
- After login, users (specialists) can see a list of reservations assigned to them with statuses Waiting and Started.
- In the list of visits, users can start and finish visits.
- Specialists with the Admin role can see the Service Desk, which displays visits with statuses Started and Waiting.

## Table Structure
- **visits**
  - id
  - reservation_time
  - reserved_time
  - status
  - status_change_date
  - number
  - user_id

- **users**
  - id
  - username
  - password
  - enabled

- **user_roles**
  - id
  - role
  - username

## Future Improvements
- Develop user and role creation functionality within the system
- Revise the database structure:
  - Create separate tables for roles and user passwords.
