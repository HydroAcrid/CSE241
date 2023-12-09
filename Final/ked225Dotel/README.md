# CSE 241 Final - Kevin Dotel 

# Intro
- Welcome to my database project! In here you can find assumptions, a walk through of what I want you to input and other useful resources 

# Assumptions
## Tenant 
- Any tenant can pay off the rent for their apartment. Rent is not divided amongst them, so as long as they're on the lease they see and can pay the entire bill.
- Because emails must be unique in my database, tenant  authentication is based on their email address.
- Because every tenant is extremely kind, a tenant can pay the bill for ANY Lease in the database, depending on what lease_id they select. Since I do not believe this is illegal, I left it as is. 

## Property Manager
- No login is required for property manager - I only had a login for tenant because I wanted to specify the user. Every property manager has access to all information. 

# Test Runs 

## Property Manager Interface
- No knowledge from the database is needed to fulfill these commands. As a helpful reminder, I currently have 25 rows of lease data in my table. The lead_id column follows this pattern: 1,2,3,4...25 so you can use any of them when lease_id is requested. 
- Tenant_id follows the same pattern as lease_id 

## Tenant Interface
- Use this email when asked: katie.holmes@gmail.com (You can use any other email in my database this is just an easy one)
- To figure out what lease id is associated with the tenat you selected, login with their email and then press: "1. Check Payment Status"
- Besides this, there is no knowledge needed from the database to fulfill the commands (Like before, lease_id is from 1 to 25).

# Triggers, Sequences, and Data 
- You can see all of the triggers, sequences, and data I inputted into the system in the "DATA.md" file

# How to run without creating a jar (MAC/Unix)
1. Make you sure you are in the "Final" Directory 
2. Run: javac -d . -cp "ked225Dotel/lib/*" $(find ked225Dotel -name "*.java")
3. Run: java -cp ".:ked225Dotel/lib/*" Final.ked225Dotel.main


