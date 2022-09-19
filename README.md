# ColourAnalyser
Program that analyses the colours utilised in an image

[Run the webapp here](https://ginga17.github.io/ColourAnalyser/)

# Brief

Algorithm utilises kMeansClustering to delegate colours which serve as means in the RGB colour space to attempt to accurately summize the 'palette' of the image. 
Dominant colours can be found either by supplying k (the number of colours), or by having the algorithm iterate through values of k to find the best solution which is the value where the distance between the means and the colours becomes less substantial i.e the distance between the means and all colours for k=n and k=n+1 is insubstantial.

While its easy for humans to generally recognise colours in images, for computers colours are specific RGB values, and hence grouping colours can be difficult and seem arbitrary. By using k means clustering, this program finds colours averaging the various shades in the image, and judges that accuracy by summing the distance of all colours in the image to the chosen colours.

Initialisation within kMeansCluster utilises two methods. The first takes the results for k=n, and finds k=n+1 by 'splitting'one of the means, the one which has the highest average distance from it's cluster. The second method is Forgy initialisation which is simply setting the initial means to random values in the image.

# K-Means Clustering ALgorithm

[K-means clustering](https://en.wikipedia.org/wiki/K-means_clustering) is an algorithmic technique in which a large set of data, is attempted to be divided and assigned to k different means. This is done by initially assigning all the data to a particular cluster. Then, from each of these clusters an average value is determined as the mean for that cluster. All our elements in the cluster are then reassigned to the cluster who's mean is closest to their value. The means are then recalculated based on these new clusters, and the clusters are then reassigned. This pattern continues until it terminates when no values are reassigned in a new cluster, meaning we have found k mean values which well represent our data.

This algorithm is at the heart of this project and is made use of to divide our pixel data into k colours which represent them. This is done by making use of the Hue, Saturation, Brightness channels which let us compare two colours to evaluate similarity. Computing the mean of a colour however, uses rgb channels, to find the average value of each respective channel for a given cluster as this gave the most colour accurate result.

# Difficulties and Complexities of the Project

One complication I noticed early on was in determining how measuring the difference between colours could be achieved. Initially, I naturally utilised colours in the default rgb 3 channel form, and compared the difference between two different colours by subtracting between the 2 colours respective channels, and squaring the results to obtain the euclidean distance between them. While this worked to a degree, the colour accuracy really wasn't that accurate to the human eye. This could be seen in examples, such as between the colours rgb(50,50,50), rgb (100,50,50) and rgb(100,100,100). Based on comparing the rgb channels, the first 2 values would be observed as the closest, however, since the 1st and 3rd value maintain the same balance between the channels it ends up appearing closer to the eye, since they maintain a greyish colour, while the second is much more red. Due to this, I tested different techniques of comparing colours and discovered the Hue, Saturation, Brightness channels that are also used to represent colours. Using this, I could much more accurately map the colour accuracy of colours by weighing each of these channels when comparing the difference of these colours, with hue proving the most important. It's important to note however that since how colours are represented and what the 'distance' between two colours is still arbitrary, this algorithm is still somewhat subjective since I have made it work as looks closest to my eyes in testing.

Another issue came with naming how many colours were actually in the image. The k means clustering algorithm effectively can be used to find our colours given that we know what k is. This occurs in my algorithm when the k is directly supplied in specifying how many colours the user wants. If they do not specify however, the algorithm needs to determine how many colours it thinks are in the image. This is an issue in the project, as it means we need to read through and designate colours many additional times adding exponential time complexity. I did however still feel this was necessary to allow the algorithm to handle a wide range of images which may widely vary in the range of colours they display. In practive, the algorithm first executes k means clustering starting with k=3, as a minimum number of colours in the image. After it terminates, it then runs on the next k values, k=4, and at the end of each iteration sums the total distance from each average colour, to the pixel colours that are designated to that average. This number by our standard measures how close the averaging for that k has summised all the colurs. We then compare this distance value to the previous iteration of k, which is naturally a higher distance (since having more colours from k being higher means our colours will be closer), but if the difference is not dramatic enough for our threshold, we terminate. I found this technique quite successful at designating an appropriate number of colours.

# Purpose

This project I started on as a precursor to a larger project which aims to apply a similar technique, but on video (in particular, movies) rather than static images. Working on this thus has helped prepare the necessary techniques and algorithms for this oncoming project. I initially wrote my functionality in java as the larger project will utilise this language, but I additionally decided to makea webapp as a way to show off the results. I additionally wanted to do this to gain more experience and practice with all the web based languages javascript, css and html.


# Examples

Below are some output examples from the webapp when an image is input.

<kbd>![Spider](https://github.com/Ginga17/ColourAnalyser/blob/master/readmeimgs/spider.png?raw=true)</kbd>

<kbd>![wolf](https://github.com/Ginga17/ColourAnalyser/blob/master/readmeimgs/wolf.png?raw=true)</kbd>

<kbd>![lion](https://github.com/Ginga17/ColourAnalyser/blob/master/readmeimgs/lion.png?raw=true)</kbd>

Below is the output from inputting the same lion king image as done above, but specifying the number of colours. This limits the algorithm and has it calculate the shade of the colours to ideally match the palette of the image.

<kbd>![lion2](https://github.com/Ginga17/ColourAnalyser/blob/master/readmeimgs/lion2.png?raw=true)</kbd>


