create a prompt workflow-prompt.md  that will help me create a model foundation for commercial banking platfrom.

also update claude.md with relevant info.

Directory domains contains 5 yaml schemas in domain\{domain-name}\model-schema.yaml.

relationships between those domains are described in domains\interdomain-map.yaml

Investigate and learn about those domains - your job will be to create models based on these schemas.

this propmt will create intial version of the domain models in model\platform-ddd.yaml and model\platform-date-eng.yaml. Same directory will be used for ux, qe and agile models.

when i see define in future I mean you will create initial version of the model , ask me 3-5 meaningfull clarifying questions based on model schema and the context, accept my feedback and modify models accordingly; 
when you make change to one model you will also check if this change needs to ripple to other models

when i say refine that means further enhance model by you asking 3-5 questions and me providing feedback and we looping with questions / feedback until I say we are done for this turn.

at any point of time i might decice not to answer questions and change direction

1. first we will define a vision and describe in platform-agile.yaml

there will also define a technology vision which will be used to create a strategic ddd model.

2. next we will define intial version of ddd and data-eng models focusing on domains, bounded context for ddd and domains and data-pipelines for data-eng

3. then we will define mvp release starting with release vision

4. then we will refine ddd model, this time we need to get into aggregate roots , entities , value objects, services etc.

5. based on this info you will also update ux, data-eng, qe , agile models

you will remember this workflow in claude.md and be able to tell me what is the next step and resume workflow at any time.


here is initial description of the platfrom - use it to create vision kick off the process.

we are large tier 1 canadian bank and our team servers commerical customers from small business (sole propriator), mid-market and large commercial clients. we are cash management team and in scope of our platform capabilities is service enrolment, user management (including  premission and approval rule manageent) , generic approval workflows based on approval rules.

we would like our platform to serve customers in both canada and us. we refer to this as north south. within us we have banks in east and west us and this is refered to east-west. We should be able to expand this to customers in UK so from design point of view this should be a global servicing platform

we can create one or many profiles for the client. client can be identified by client id type (SRF, GID, IND) where SRF is the name of canadian banking platform that has details of demographics (name, address) and list of client accounts across different systems and account types. GID will refer to Global ID used as client identifier in Capitial Markets that also spans Canada, US and other jurisdictions. IND is used for Indirect Clients which we manage on behalf of our clients. In that case the indirect client can be a person or a business (person name vs. business name) will have an address and in case of business it can have set of related persons that perform a role in a business (signing-officer, administrator, director). Same person could perform mutiple roles.

In general client has accounts of different types for example ca:dda is demand deposit account held in canadian banking dda system and us:dda would be dda account held in US system. Canadian bank accounts have structure bank-number, transit-number, account-number. in addition accounts held in our bank (we own one of those bank-numbers) will have additional info such as processoing centre (occ, bcc, qcc - refering to ontario, quebec or BC cash centre). Canadian dda has USD and CAD accounts, and if we keep money in other currencies we keep them in system called RIBS. those would be ca:fca (foreign currency accounts) and we would support currencies like EUR, GBP, JPY, AUD, NZD there. in addition to deposits we have loan accounts held in olb system (ca:olb) and those can be revolving credit lines (called operating lines) and fixed term loans. mortgages as in separate system (ca:mtg). there are also investments like GIC held in separate system (for now note it as ca:gic). customers can have also business credit cards (we issue plastic to individuals) in system tsys and commerical credit cards (client manages the credit card pool and allocates to their employees typicall for expense management). in additon we have a special type of account that is used for ach payments called GSAN in canada (lets use ca:ach) - each of those accounts is linked to one dda account but in addition has specific purpose (ach credit or ach debit). 

in general client and account systems are managed outside of our team. we currently have api access to those platfroms but since velocity of data is not high it would be conceivable that we implement daily batch and store some of the data set in our domain.

when we create client profile we could decide to enroll all accounts. in that case we should auto enroll new accounts. in general we need to know if the account has been closed so we can mark it as inactive in our client profiles. for this reason daily feed so that we can identify new or close accounts from source systems would be usefull.

client profiles can be of different types - servicing and online. for online profiles we also need to manage users. typically we would require customer to have two users with role administrator so that the critical functions they perform is what we call dual admin. other type of the user is regular user. 

we need to support first time registration of the user where based on user email and phone number we will initiate flow with identity provider (planing to use okta in the future but for now we have internal platfrom called a-and-p). once user completes the onbaording (creating password and onbaordinng mfa) we should receive an event so we can change status of the user. you should also be able to lock users - their administrators should be able to do it or bank might decide to do it because of suspicuos activity (this lock can be removed only by bank). users have logon-id which is typically in form of email address. in addition to logon-id we should also store id provider (anp, okta). there could be many users with same login acros different profiles.

we store permisions as permission policies that are owned by online profile and resemble aws iam model where subject is user, action is urn , resources are accounts. same goes for approval policies with extension for number of approvers - these models will be elaborated later and plan would be to migrate from current solution to this one but the data model will stay largely unchanged.

with regards to services they can also be broadly grouped to stand-alone services that don't have online access, online services, and indirect services.

services can link to one or many accounts that have been enrolled to the profile. there should be option to enroll all current and future accounts which means that we do not need to manually add and remove accounts. sometimes that is ok, sometimes it is not - there is a risk with bigger customers with auto enrolment of accounts. 

examples of stand alone services are - additional deposit narrative (we charge clients to provide them with additional info for their dda transactions), ach debit block (we can prevent ach debits from certain initiaitors). in addition we should have balance and transaction reporting (BTR) which should have dda accounts enrolled, type of file (bai, camt, mt) , frequency if intraday, eod is every business day, delivery (ftp, swift - only camt and mt).

examples of online services are interac send which has additional detail of email address stored, receivable serivice which will have list of gsans enrolled in the service. receivable service will also link to set of indirect clients which will in this case play a role of payor.

indirect service profile will then have receivable-approval service. indirect clients will manage their set of users, permissions, approval rules and accounts that they use for paying invoices (canadian bank account).