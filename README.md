# Trade Rev Coding Challenge
This is a simple image display Android native app for showing popular images from Unsplash photos API in a list. There's also a full screen view with details of each image. This app is being built for a code challenge with TradeRev

## Project Setup

This project should run as it is, if cloned from this repository.
- [ ] This was developed on the latest version 3.3.2 of Android studio.
- [ ] Gradle version - gradle-4.10.1
- [ ] Minimum supported SDK - Android API 22
- [ ] Compiled on - Android API 28
- [ ] Target SDK version - Android API 28
- [ ] Language used - Kotlin 1.3.21

## API Key for UnSplash photos API

This app will not function without the API key from UnSplash. The API key will have to be added to the API_KEY parameter in the app.properties file under the assets folder. No quotation marks are necessary around the API key in properties file. Also the API key entered not contain any preceding or trailing space characters.

Sample app.properties file (Only the line below)

API_KEY=YOUR_API_KEY_HERE

A blank API_KEY property has already been added in app.propeties present in the repo. The actual API key used for testing hasn't been uploaded to GitHub as instructed.

## App functionality

This app has three basic functionalities - 
### A long list of images with image previews in a staggered layout.
A StaggeredGridLayout with two rows has been used for this screen.
The staggering of grid allows for images to be shown without any cropping. Size small has been selected as image_size for the photos API. 

### Full screen image view with swipe
When an image is clicked on the grid it switches to a full screen view of that image. The user can swipe left or right in this screen to view all the available images. SnapHelper has been used to snap each photo to the middle of screen on page swipe.

### Dialog screen with image and image details
A screen that shows a dialog when an image is clicked on the full screen view. The image URL for Dialog screen image and image exif information has been fetched using the photo/:id API.
#### Both Landscape and Portrait orientations are supported

## This app uses these major components -

- [ ] Dagger2 - To perform dependency injection for Singleton items that are required through the app for network calls
- [ ] Retrofit - Retrofit has been used as the REST API client
- [ ] OkHTTP - OkHTTP is the https client. OkHTTP has been used in conjunction with Retrofit.
- [ ] RxJava and LiveData - The API requests have been handled with an observer pattern. Observers emit data and trigger HTTP requests.       Observable process those requests.
- [ ] Glide - Glide library has been used for efficient image loading and caching
- [ ] Moshi - Moshi has been used for JSON parsing instead of GSON as it has full Kotlin support and is faster
- [ ] Android paging library - This app uses the new Android paging library introduced in Android Jetpack to handle paging for the huge       popular photo list. The app loads 10 items per page for each request, and on scroll of the recyclerView loads the next page. 
- [ ] Android's PageList class is used to hold on to the data. The page key is linked to "page" parameter accepted by the unsplash photos API. This is auto incremented by the after every load of 10 image
- [ ] Nick Rout's snap helper class to detect image position on page swipe. https://medium.com/over-engineering/detecting-snap-changes-with-androids-recyclerview-snaphelper-9e9f5e95c424

# Mobile Developer Coding Challenge

This is a coding challenge for prospective mobile developer applicants applying through http://work.traderev.com/

## Goal:

#### Build simple app that allows viewing and interacting with a grid of curated photos from Unsplash

- [ ] Fork this repo. Keep it public until we have been able to review it.
- [ ] Android: _Java_ or _Kotlin_ | iOS: _Swift 4_
- [ ] Unsplash API docs are here: https://unsplash.com/documentation.
- [ ] Grid of photos should preserve the aspect ratio of the photos it's displaying, meaning you shouldn't crop the image in any way.
- [ ] App should work in both portrait and landscape orientations of the device.
- [ ] Grid should support pagination, i.e. you can scroll on grid of photos infinitely.
- [ ] When user taps on a photo on the grid it should show only the tapped photo in full screen with more information about the photo.
- [ ] When user swipes on a photo in full screen, it should show the the next photo and preserve current photo's location on the grid, so when she dismisses the full screen, grid of photos should contain the last photo she saw in photo details.

### Evaluation:
- [ ] Solution compiles. If there are necessary steps required to get it to compile, those should be covered in README.md.
- [ ] No crashes, bugs, compiler warnings
- [ ] App operates as intended
- [ ] Conforms to SOLID principles
- [ ] Code is easily understood and communicative
- [ ] Commit history is consistent, easy to follow and understand
