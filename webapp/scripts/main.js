
function addImage(file) {
    var img = new Image();
    img.src = URL.createObjectURL(file);

    // Factor to reduce resolution by
    var resFactor = 7;
    img.onload = function() {        
        // for(var resFactor = 8; resFactor < 9; resFactor++) {
            
            var startTime = performance.now();;

            colors = getColoursOfImage(img, resFactor);
            var centroids = findCentroids(colors,5);
            var dist = sumDistOfCentroids(centroids);
            
            var newC = findCentroids(colors,3);
            var lastCentroids = centroids;
            var dist = sumDistOfCentroids(centroids);
            var lastDistance = dist * 2;
            while(lastDistance * 0.85 > dist) {
                lastDistance = dist;
                lastCentroids = newC;
                var newC = findCentroids(colors,centroids.length + 1);
                var dist = sumDistOfCentroids(newC);
                for (var i = 0; i<50; i++) {
                    var currC = findCentroids(colors,centroids.length + 1);
                    if (sumDistOfCentroids(currC) < dist) {
                        dist = sumDistOfCentroids(currC);
                        newC = currC;
                    }
                }

            }
            // var centroids = initialiseClusters(colors)
            // var dist = sumDistOfCentroids(centroids)
            // var lastDistance = dist*2;
            // console.log("MARK");
            // for(c in centroids) {
            //     console.log(getRGBStr(centroids[c].rgb) + "w" + centroids[c].weight);
            // }
            // while(lastDistance * 0.85 > dist) {
            //     // console.log("K = " + centroids.length);
            //     centroids = kMeansCluster(centroids,colors);
            //     // console.log("MARK");
            //     // for(c in centroids) {
            //     //     console.log(getRGBStr(centroids[c].rgb) + "w" + centroids[c].weight);
            //     // }
            //     lastDistance = dist;
            //     dist = sumDistOfCentroids(centroids);
            // }
            
            console.log(resFactor + "    " + (performance.now() - startTime) + "    " + sumDistanceFromColours(centroids, getColoursOfImage(img, 1)));
        // }
        
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
        }
    }
    return colors;
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
        addImage(files[i]);
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

        // var newCent = new Centroid(this.oldColours.splice(0, this.oldColours.length/2));
        // // refresh colour
        // this.rgb = averageColour(this.oldColours);
        // newCent.newColours = newCent.oldColours;
        // return newCent;
        var newCentroids = initialiseClusters(this.oldColours);
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
function initialiseClusters(colors) {
    
    const centroid1 = [];
    const centroid2 = [];
    var min = sumColour(colors[0]);
    var max = sumColour(colors[0]);
    var minColour = colors[0];
    var maxColour = colors[0];
    
    for (var i =0; i< colors.length; i++) {
        var size = sumColour(colors[i]);
        if (size > max) {
            max = size;
            minColour = colors[i];
        }
        else if (size < min) {
            min = size;
            maxColour = colors[i];
        }
    }
    minColour = {r:Math.floor((averageColour(colors).r + minColour.r)/2), g:Math.floor((averageColour(colors).g + minColour.g)/2), b:Math.floor((averageColour(colors).b + minColour.b)/2) }
    maxColour = {r:Math.floor((averageColour(colors).r + maxColour.r)/2), g:Math.floor((averageColour(colors).g + maxColour.g)/2), b:Math.floor((averageColour(colors).b + maxColour.b)/2) }
    
    for (var i = 0, l = colors.length; i < l; i++) {
        if (distBetweenColours(minColour, colors[i]) < distBetweenColours(maxColour, colors[i])) {
            centroid1.push(colors[i]);
            // console.log("BBBB");
        }
        else {
            centroid2.push(colors[i]);
            // console.log("AAAA");
        }
    }
    
    const centroids = [new Centroid(centroid1), new Centroid(centroid2)];
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
    var sum = 0;
    for (c in centroids) {
        sum += centroids[c].weight;
    }
    // console.log(centroids.length);
    var widestCentroid = 0;
    var dist = centroids[0].sumDist()/centroids[0].weight;
    // console.log("oooo");
    // console.log(centroids);
    for (c in centroids) {
        var curr = centroids[c];
        var dist = curr.sumDist()/curr.weight;
        if (curr > dist) {
            dist = curr;
            widestCentroid = c;
        }
    }
    centroids.push(centroids[widestCentroid].split());
    // console.log(centroids.length);

    var noChange = false;
    while(noChange == false) {
        // for (p in centroids) {
        //     c = centroids[p];
        //     c.colours = [];
        // }
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
        noChange = true;
        var sum = 0;
        for (c in centroids) {
            sum += centroids[c].weight;
        }
        for (p in centroids) {
            c = centroids[p];

            if(c.recalculateColour()) {
                noChange = false;
            }
        }
    }
    return centroids;
}

function findCentroids(colors, n) {
    var noChange = false;
    var centroids = [];
    for (var i = 0; i< n; i++) {
        centroids.push(new Centroid([colors[Math.floor(Math.random() * colors.length)]]))
    }
    
    while(noChange == false) {
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
        noChange = true;
        var sum = 0;
        for (c in centroids) {
            sum += centroids[c].weight;
        }
        for (p in centroids) {
            c = centroids[p];

            if(c.recalculateColour()) {
                noChange = false;
            }
        }
    }
    return centroids;

}

function findDomsFromColor(clusters) {
    
    var clusters = [];
    for (var i = 0; i <= colours.length; i++) {
        clusters[i] = {}
    }
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    var width = canvas.width = img.naturalWidth;
    var height  = canvas.height = img.naturalHeight;
     
    ctx.drawImage(img,0,0);

    var imageData = ctx.getImageData(0, 0, width, height);
    var data = imageData.data;
    const colors = [];
    const centroid1 = [];
    const centroid2 = [];

    var p = 0;
    for (var i = 0, l = data.length; i < l; i+=4) {
        
        colors[p] = {r: data[i], g: data[i+1], b: data[i+2]};
        if (i < data.length/2) {
            centroid1[p] = {r: data[i], g: data[i+1], b: data[i+2]};
        }
        else {
            centroid2[p] = {r: data[i], g: data[i+1], b: data[i+2]};
        }
        p++;
    }

    const ret = [];
    var rgb = averageColour(centroid1);
    ret[0] = {r:rgb.r,g:rgb.g,b:rgb.b,weight:rgb.weight};
    var rgb = averageColour(centroid1);
    ret[1] = {r:rgb.r,g:rgb.g,b:rgb.b,weight:rgb.weight};
    return ret;
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