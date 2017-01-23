# Udacity-Popular-movies-Android-App
A native Android App for movie fans, developed while studying for the Udacity's Android developer program.

##About themoviedb.org API
The API key should be set as a System environment variable called THE_MOVIE_DB_API_TOKEN, and then be called in build.gradle:
```
buildTypes.each {
            it.buildConfigField 'String', "THE_MOVIE_DB_API_TOKEN", "\"" + System.getenv("THE_MOVIE_DB_API_TOKEN") + "\""
        }
```
So, in order for the application to run correctly, please ensure that an environment variable called THE_MOVIE_DB_API_TOKEN exists in your System and contains a valid themoviedb.org APi key.