# bitvote
An online voting system for HOA's and other organizations that loosely implements Robert's Rules of Order

Onine Demo: [Can be accessed here](http://vast-harbor-70485.herokuapp.com/)

Username: admin@admin.org

Password: admin

NEEDS:  

1) As people move and/or new leaders are elected, there is no way to access an issue
or voting history that can be referenced to understand what decisions were made and why
they were made.  Notes were kept manually, but they reside with 1 person and are easily lost.

2) Support engaging a broader range of residents by making votes more accessible.

3) Support different means of voting that may include limiting votes to a board or
enabling support for votes by quorum.


This application was created for my HOA to accomplish the following goals.

1) Document the issues that are proposed to the association
2) Document the discussions on those issues
3) Facilitate voting on the issues.
4) Store the issue and voting history for future representatives.


## About

Bitvote is a web application built on the Grails stack.

It is a multi-tentant application that can support many organizations or clubs.

#### Requirements:

grails 2.3.7

Java 1.7

#### Usage:

grails run-app

#### Integration

The email notification features of this system used mandrillapp, which is now part of MailChimp.


The system has a sign-up feature that allows organizations to register for there own discussion/voting system.  

The system integrates with the Stripe payment API.  Minimal payments were going to be required to help pay for hosting costs.
