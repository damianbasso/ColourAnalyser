# ColourAnalyser
Program that analyses the colours utilised in an image

[Run the webapp here](https://ginga17.github.io/)

#Brief

Algorithm utilises kMeansClustering to delegate colours which serve as means in the RGB colour space
to attempt to accurately summize the 'palette' of the image. 
Dominant colours can be found either by supplying k (the number of colours), or by having the algorithm
iterate through values of k to find the best solution which is the value where the distance between the means
and the colours becomes less substantial i.e the distance between the means and all colours for k=n and k=n+1 is insubstantial.

Initialisation within kMeansCluster utilises two methods. The first takes the results for k=n, and finds k=n+1 by 'splitting'
one of the means, the one which has the highest average distance from it's cluster. 
The second method is Forgy initialisation which is simply setting the initial means to random values in the image.

#Purpose

This project I started on as a precursor to a larger project which aims to apply a similar technique, but on video (in particular, 
movies) rather than static images. Working on this thus has helped prepare the necessary techniques and algorithms for this oncoming 
project. I initially wrote my functionality in java as the larger project will utilise this language, but I additionally decided to make
a webapp as a way to show off the results. I additionally wanted to do this to gain more experience and practice with all the web based
languages javascript, css and html.
