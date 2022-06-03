# Overview
This is my final year project (FYP) in Maynooth University.

This project involves generating two apps that helps with the automation of part of the final year project (FYP) management process. Specifically:
- <strong>FYP Grading Report Generator</strong>
  - the automatic analysis and combination of students’ grades, from Excel files, into a well-formatted PDF document
- <strong>FYP Descriptions Generator</strong>
  - the automatic generation of PDF documents containing final year project descriptions from Excel files

To sum up, both of the two sub projects are about fetching data from Excel files and exporting well-formatted PDF with the data.

This project is the <strong>FYP Grading Report Generator</strong> project.

The output PDF: [Computer_Science_FYP_Grading_Report_2022.pdf](https://github.com/longporo/excelPdfReport/blob/86dcc93d876a55262f55a1e4096a1eb2d7223113/file/output%20file/Computer_Science_FYP_Grading_Report_2022.pdf).

<img width="443" alt="image" src="https://user-images.githubusercontent.com/42689061/171750967-34b0f730-2ad9-4a48-bce4-0632dfa756f7.png">

# Techniques

Technical Information: `Java, Java Swing, Desktop Application, Maven, Apach POI, Log4J, iText 7, JFreeChart`

## Architecture
This application takes Excel files containing students' final year project(FYP) grades as input and produces a well-formatted PDF as output. The application can also combine the imported Excel files into one Excel file and output it. The component diagram and class diagram bellow descript the two main features.

<img width="600" alt="image" src="https://user-images.githubusercontent.com/42689061/171752318-90de64d8-9e84-4e98-9ed8-825bba43cfa2.png">

<img width="600" alt="image" src="https://user-images.githubusercontent.com/42689061/171753310-9cec3480-b4fc-459d-9c6c-0ea0d47027a4.png">

## The User Interface
The GUI of the project is built with Java Swing.


<img width="600" alt="image" src="https://user-images.githubusercontent.com/42689061/171753831-8446396c-98ed-43ca-99c8-d2c9875eb6b0.png">


## The libraries Used

Various third-party libraries are used for different roles within our system:

<img width="600" alt="image" src="https://user-images.githubusercontent.com/42689061/171755213-9f0f49ea-3d23-426c-a7c1-008cdd0ed270.png">

## The User Manual

The user manual for the product is available [here](https://longporo.slite.com/p/note/QhNfpiJBwx_ijfvsNlJxXN).
