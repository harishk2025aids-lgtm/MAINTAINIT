# MaintainIt

MaintainIt is a Java Spring Boot backend for preventive maintenance scheduling for equipment.

## Features
- Machine CRUD
- Technician management
- Usage log tracking
- Automatic preventive maintenance task creation
- Duplicate task prevention
- Overdue detection
- Technician assignment and status updates
- MySQL integration
- Swagger UI

## Project structure
- src/main/java/machine/maintaninIt/controller
- src/main/java/machine/maintaninIt/service
- src/main/java/machine/maintaninIt/repository
- src/main/java/machine/maintaninIt/entity
- src/main/java/machine/maintaninIt/dto
- src/main/java/machine/maintaninIt/exception
- src/main/java/machine/maintaninIt/config

## Run locally
1. Create a MySQL database named `maintainit_db`.
2. Update the username/password in `application.properties`.
3. Run:
   mvn clean install
   mvn spring-boot:run
4. Open Swagger at http://localhost:8080/swagger-ui.html

## API examples

### Create machine
POST /api/machines
{
  "name": "Hydraulic Press",
  "machineCode": "HP-001",
  "description": "Production line machine",
  "maintenanceIntervalHours": 500,
  "currentUsageHours": 0,
  "lastMaintenanceUsageHours": 0,
  "status": "ACTIVE"
}

### Create usage log
POST /api/usage-logs
{
  "machineId": 1,
  "usageHours": 500,
  "remarks": "Normal cycle run"
}

### Create maintenance task
POST /api/maintenance-tasks
{
  "machineId": 1,
  "technicianId": 1,
  "taskTitle": "Preventive Service",
  "description": "Inspect and service the machine",
  "scheduledUsageHours": 500,
  "status": "OPEN",
  "priority": "HIGH",
  "notes": "Check lubrication and belt tension"
}

## Postman example
- Base URL: http://localhost:8080
- Use the controllers in the API folder with requests to /api/machines, /api/usage-logs, /api/maintenance-tasks, and /api/technicians.
