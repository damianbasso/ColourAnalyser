
function loadImage(file) {
    if (document.getElementById("numColours").value != "" && document.getElementById("numColours").value<=0) {
        console.log("dddd");
        document.getElementById("inputError").hidden = false;
        return;    
    }
    document.getElementById("inputError").hidden = true;
    var gif = new Image();
    gif.src = "loading.gif";
    gif.id = "load";
    document.getElementById('images').appendChild(gif)
    setTimeout(function(){process(file); },10); 
}
function process(file) {
    var img = new Image();
    img.src = URL.createObjectURL(file);

    img.onload = function() { 
        // Factor to reduce resolution by
        // resFactor is calculated to aim to reduce colours array length
        // to 8000
        var resFactor = Math.floor(Math.sqrt(img.width*img.height/6000));
        
        var startTime = performance.now();;

        colors = getColoursOfImage(img, resFactor);

        // If number of colours is specified, finds centroids by using a forgy
        // initialisation many times, and finding the most accurate result
        if (document.getElementById("numColours").value!="") {
            var k = document.getElementById("numColours").value;
            
            var centroids = findCentroids(colors,k);
            var dist = sumDistOfCentroids(centroids);

            // find the iteration with the best result (lowest sum distance from colours)
            for (var i=0; i< 25; i++) {
                var newCentroids = findCentroids(colors, k);
                
                var newDist = sumDistOfCentroids(newCentroids);
                if (newDist < dist) {
                    centroids = sumDistOfCentroids;
                    dist = newDist;
                }
            }
        }
        // Iteratively find the preffered result of centroids
        else {
            // initialise 4 centroids as the minimum number of colours
            var centroids = findCentroids(colors,4);
            var lastCentroids = centroids;
            var dist = sumDistOfCentroids(centroids);

            var lastDistance = dist * 2;
            // Additional colours are added when they shave 15% from the sumDistance
            while(lastDistance * 0.85 > dist) {
                lastDistance = dist;
                lastCentroids = centroids;
                // kMeansCluster utilises previous centroids when k = n
                // to find some when k = n + 1
                var centroids = kMeansCluster(centroids, colors);
                var dist = sumDistOfCentroids(centroids);

                for (var i = 0; i<10; i++) {
                    var currC = findCentroids(colors,centroids.length);
                    if (sumDistOfCentroids(currC) < dist) {
                        dist = sumDistOfCentroids(currC);
                        centroids = currC;
                    }
                }
            }
        }
        console.log(resFactor + "    " + (performance.now() - startTime) + "    colors = " + colors.length + "    " + sumDistanceFromColours(centroids, getColoursOfImage(img, 1)));
        document.getElementById("load").remove();
        drawResults(centroids,img);
    };


}

// Reads all the colours in the image into an array
// resFactor is the value by which the image's resolution is divided as
// to speed up the algorithm by reducing the size of the colours array
function getColoursOfImage(img, resFactor) {
    var resizeCanvas = document.createElement('canvas');
    resizeCanvas.width = img.width/resFactor;
    resizeCanvas.height = img.height/resFactor;

    ctx = resizeCanvas.getContext('2d');
    ctx.drawImage(img,0,0,img.width/resFactor,img.height/resFactor);

    var imageData = ctx.getImageData(0, 0, img.width/resFactor,img.height/resFactor );
    var data = imageData.data;
    var colors = [];
    // load colours into an array of rgb object
    for (var i = 0; i < data.length; i+=4) {  
        // bypass fully transparent pixels
        if (data[i+3] > 0) {
            colors.push({r: data[i], g: data[i+1], b: data[i+2]});
            // console.log(colors[colors.length-1]);
        }
    }
    return colors;
}

function drawResults(centroids, img) {
    var canvas = document.createElement('canvas');
        canvas.className = 'row';

        canvas.width = img.naturalWidth * 2;
        canvas.height =  img.naturalHeight;        

        ctx = canvas.getContext("2d");
        ctx.drawImage(img,0,0);
        ctx.font = '32px serif';
        
        // Draw rectangles
        var y = 0;
        centroids.sort(function(a,b){return b.weight-a.weight})
        for(var i in centroids) {
            oref = centroids[i];
            console.log(getRGBStr(oref.rgb));
            ctx.fillStyle = getRGBStr(oref.rgb);
            ctx.fillRect(img.naturalWidth,y, canvas.width, Math.round(canvas.height *oref.weight/colors.length));
            
            // Draw text
            // ctx.fillStyle = "#000000";
            // var textSize = ctx.measureText(getRGBStr(oref.rgb));
            // ctx.fillText(getRGBStr(oref.rgb), img.naturalWidth*1.5 - textSize.width/2, y + (img.naturalHeight * oref.weight/colors.length)/2);
            
            y += Math.round(img.naturalHeight *oref.weight/colors.length);  
        }
        document.getElementById('images').appendChild(canvas);
}

// Sums the distance between centroids and their clusters
function sumDistOfCentroids(centroids) {
    var sum = 0;
    for (c in centroids) {
        objc = centroids[c];
        sum += objc.sumDist();
    }
    // console.log("Total sum of mean to cluster distance = " + sum);
    return sum;
}

// sums the distance of the determined centroids to all colours in the image
// used to gauge the accuracy of the algorithm when using different resolutions
// of the images -> smaller the number, the more accurate the result
function sumDistanceFromColours(centroids, colours) {
    var sum = 0;
    for (c in colours) {
        // find the distance to the closest centroid
        var dist = centroids[0].dist(colours[c]);
        for (o in centroids) {
            if (centroids[o].dist(colours[c]) < dist) {
                dist = centroids[o].dist(colours[c]);
            }
        }
        sum+= dist;   
    }
    return sum;
}

function getRGBStr(rgb) {
    return 'rgb(' + rgb.r + ', ' + rgb.g + ', ' + rgb.b + ')';

}

function handleImages(files) {
    document.getElementById('images').innerHTML = '';

    for (var i = 0; i < files.length; i++) {
        loadImage(files[i]);
    }
}  

function drawColours() {
    // var c = document.getElementById("dom_colours");
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    ctx.fillStyle = "#FF0000";
    ctx.fillRect(20, 20, 150, 100);
}

class Centroid {
    constructor(colours) {
        this.newColours = [];
        this.oldColours = colours;
        this.rgb = averageColour(colours);
    }
    recalculateColour() {
        this.rgb = averageColour(this.newColours);
        if (arraysEqual(this.newColours,this.oldColours)) {
            this.newColours = [];
            return false;
        }
        this.oldColours = this.newColours;
        this.newColours = [];
        return true;
    }
    get weight() {
        return this.oldColours.length; 
    }
    split() {
        // var startTime = performance.now();
        var newCentroids = initialise2Centroids(this.oldColours);
        // console.log("init in  " + (performance.now() - startTime));

        // var totsTime = 0;
        // startTime = performance.now();
        // var centroids = findCentroids(this.oldColours, 2);
        // var dist = sumDistOfCentroids(centroids);
        // totsTime += (performance.now() - startTime);
        // for (var i = 0; i< 20; i++) {
        //     startTime = performance.now();
        //     var newCentroids = findCentroids(this.oldColours, 2);
            
        //     if(sumDistOfCentroids(newCentroids) < dist) {
        //         dist = sumDistOfCentroids(newCentroids);
        //         centroids = newCentroids;
        //     }
        //     totsTime += (performance.now() - startTime);                
        // }
        // console.log("tots time = " + totsTime);
        this.oldColours = newCentroids[0].oldColours;
        this.rgb = newCentroids[0].rgb;
        return newCentroids[1];
    }
    dist(colour) {
        return Math.pow(this.rgb.r - colour.r,2) +Math.pow(this.rgb.g - colour.g,2)+Math.pow(this.rgb.b - colour.b,2);
    }
    sumDist() {
        var sum = 0;
        for(c in this.oldColours) {
            sum += this.dist(this.oldColours[c]);
        }
        return sum;
    }
}

function arraysEqual(arr1, arr2) {

	// Check if the arrays are the same length
	if (arr1.length !== arr2.length) return false;

	// Check if all items exist and are in the same order
	for (var i = 0; i < arr1.length; i++) {
		if (arr1[i] !== arr2[i]) return false;
	}

	// Otherwise, return true
	return true;
};

function coloursEqual(col1, col2) {
    return col1.r == col2.r && col1.g == col2.g && col1.b == col2.b;
}


// Sums together the r g and b values of the colour
function sumColour(rgb) {
    return rgb.r + rgb.g + rgb.b;
}

function distBetweenColours(rgb1, rgb2) {
    return Math.pow(rgb1.r-rgb2.r,2) + Math.pow(rgb1.g-rgb2.g,2) + Math.pow(rgb1.b-rgb2.b,2);
}

// IDEA FOR FAST INITIALISATION
// If we calc average, max (r + g + b), min (r+b+g), we initialise
// two points between
function initialise2Centroids(colors) {
    startTime = performance.now();
    var min = colors[0];
    var max = colors[0];
    
    for (var i =0; i< colors.length; i++) {
        if (sumColour(colors[i]) > sumColour(max)) {
            max = colors[i];
        }
        else if (sumColour(colors[i]) < sumColour(min)) {
            min = colors[i];
        }
    }
    // Finds two colours evenly between min and average, and average and max
    min = {r:Math.floor((averageColour(colors).r + min.r)/2), g:Math.floor((averageColour(colors).g + min.g)/2), b:Math.floor((averageColour(colors).b + min.b)/2) }
    max = {r:Math.floor((averageColour(colors).r + max.r)/2), g:Math.floor((averageColour(colors).g + max.g)/2), b:Math.floor((averageColour(colors).b + max.b)/2) }

    var centroids = [new Centroid([min]), new Centroid([max])];
    
    var noChange = false;
    while(noChange == false) {

        // Assign to clusters
        for (p in colors) {
            colour = colors[p];

            var closest = centroids[0];
            var dist = centroids[0].dist(colour);
            for (o in centroids) {
                centroid = centroids[o];
                if (centroid.dist(colour) < dist) {
                    closest = centroid;
                }
            }
            closest.newColours.push(colour);
        }
        // Recalculate means of centroids, and check for change
        noChange = true;
        for (p in centroids) {
            c = centroids[p];
            if(c.recalculateColour()) {
                noChange = false;
            }
        }
    }
    // console.log(`Call to doSomething took ${performance.now() - startTime} milliseconds`)
    return centroids;
}

function kMeansCluster(centroids, colors) {
    
    // finds the centroids with the highest average distance from its cluster
    var widestCentroid = 0;
    var dist = centroids[0].sumDist()/centroids[0].weight;
    for (c in centroids) {
        var curr = centroids[c];
        var dist = curr.sumDist()/curr.weight;
        if (curr > dist) {
            dist = curr;
            widestCentroid = c;
        }
    }

    // splits the centroid with the widest cluster
    centroids.push(centroids[widestCentroid].split());

    var change = true;
    while(change) {
        for (p in colors) {
            colour = colors[p];

            var closest = centroids[0];
            var dist = centroids[0].dist(colour);
            for (o in centroids) {
                centroid = centroids[o];
                if (centroid.dist(colour) < dist) {
                    closest = centroid;
                    dist = centroid.dist(colour);
                }
            }
            closest.newColours.push(colour);
        }
        change = false;
        for (c in centroids) {
            if(centroids[c].recalculateColour()) {
                change = true;
            }
        }
    }
    return centroids;
}
// Seeded RNG function
// Taken from https://stackoverflow.com/questions/521295/seeding-the-random-number-generator-in-javascript
function mulberry32(a) {
    return function() {
      var t = a += 0x6D2B79F5;
      t = Math.imul(t ^ t >>> 15, t | 1);
      t ^= t + Math.imul(t ^ t >>> 7, t | 61);
      return ((t ^ t >>> 14) >>> 0) / 4294967296;
    }
}

/** 
 * @param {Array} colors The colors present in the image
 * @param {number} k The desired number of centroids
 * @returns - the determined centroids
 * This function utilises a random partition
 */
function findCentroids(colors, k) {
    var centroids = [];
    // seed is used to maintain integrity in results 
    var rand = mulberry32(200);

    // Randomly selects colours to initialise as the means (Forgy initialisation)
    while (centroids.length < k) {
        var newColour = colors[Math.floor(rand() * colors.length)];
        // console.log("OWOW" + getRGBStr(newColour));
        // console.log(getRGBStr(newColour));
        var colourIsUnique = true;
        for (t in centroids) {
            if (coloursEqual(centroids[t].rgb, newColour)) {
                colourIsUnique = false;
                // break;
            }
        }
        if (colourIsUnique) {
            centroids.push(new Centroid([newColour]));
        }
    }

    // var initArrays = [];
    // for(var i = 0; i<k; i++) {
    //     initArrays.push([]);
    // }
    // for (var c in colors) {
    //     initArrays[Math.floor(rand() * colors.length)].push(colors[c]);
    // }
    // for (var i = 0; i<k; i++) {
    //     centroids.push(new Centroid(initArrays[i]));
    // }

    
    var change = true;
    // While assigning step is changing the centroid means
    while(change) {
        // Assign each colour to their closest centroid's cluster
        for (p in colors) {
            colour = colors[p];

            var closest = centroids[0];
            var dist = centroids[0].dist(colour);
            for (o in centroids) {
                centroid = centroids[o];
                if (centroid.dist(colour) < dist) {
                    closest = centroid;
                    dist = centroid.dist(colour);
                }
            }
            closest.newColours.push(colour);
        }
        change = false;
        
        // Recalculate centroid and check for whether it changes
        for (p in centroids) {
            c = centroids[p];
            // recalculateColour() returns true if it changes the mean
            if(c.recalculateColour()) {
                change = true;
            }
        }
    }
    return centroids;
}

function averageColour(colours) {
    var r = 0;
    var g = 0;
    var b = 0;
    for (var i = 0, l = colours.length; i < l; i++) {
        r += colours[i].r;
        g += colours[i].g;
        b += colours[i].b;
    }
    r = Math.floor(r/colours.length);
    g = Math.floor(g/colours.length);
    b = Math.floor(b/colours.length);

    return {r:r, g:g, b:b, weight:colours.length}
}

document.ondragover = function(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'copy';
};
  
document.ondrop = function(event) {
    event.preventDefault();
    handleImages(event.dataTransfer.files);
};

(function() {
    var upload = document.getElementById('upload');
    var target = document.getElementById('target');
  
    upload.onchange = function() {
      handleImages(this.files);
    };
  
    target.onclick = function() {
      upload.click();
    };
  })();