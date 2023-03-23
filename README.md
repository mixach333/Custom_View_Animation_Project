### Android App with Loading Status

This Android application allows you to download a file from the Internet using a custom button with loading animation, which continues as long as download proces continues, and notifies you when the download is complete.
You can also choose which file to download from a list of options or by entering a custom URL.

Please note that this is a sample project to demonstrate the implementation of a custom view, without using architecture approaches such as MVVM, Clean Architecture, SOLID, etc. You can check out other projects such as the AsteroidRadar or ShoeStore projects.


## Usage
* When you open the app, you will see a list of files you can choose to download. You can also enter a custom URL in the text fields below the list.

* Once you select a file or enter a custom URL, click the custom button to start the download. The button will be animated. When the download is complete, a notification will be sent and you can click on it to view the download details.

* The details screen will show the name of the downloaded file and the status of the download, along with an animation. You can click the "OK" button to go back to the main screen.

## Contributing
If you would like to contribute to this project, feel free to fork the repository and submit a pull request. If you find any issues or have any suggestions, please open an issue on the issue tracker.

## Credits
This app was created as a project for the Udacity Kotlin Android course. The following libraries were used in the project:

*Glide: https://github.com/bumptech/glide
*Retrofit: https://github.com/square/retrofit
