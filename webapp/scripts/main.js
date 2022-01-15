
function addImage(file) {
    var img = new Image();
    img.src = URL.createObjectURL(file);
    img.onload = function() {
        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext("2d");
        var width = canvas.width = img.naturalWidth;
        var height = canvas.height = img.naturalHeight;
        
        ctx.drawImage(img,0,0);
    
        var imageData = ctx.getImageData(0, 0, width, height);
        var data = imageData.data;
        var colors = [];
        for (var i = 0, l = data.length; i < l; i+=4) {    
            colors.push({r: data[i], g: data[i+1], b: data[i+2]});
        }
        var rgbs = initialiseClusters(colors);
        var weight = 0;
        for(var i in rgbs) {
            orec = rgbs[i];
            weight += orec.weight;
        }
        var y = 0;     
        for(var i in rgbs) {
            orec = rgbs[i];
            // ctx.fillStyle = #//getRGBStr(orec.rgb);
            ctx.fillRect(0,y, 80,40)
            y += Math.floor(200 * orec.weight/weight);

        }
        document.getElementById('images').appendChild(canvas);

    };
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
        this.colours = [];
        this.rgb = averageColour(this.colours);
    }
    updateColour() {
        this.rgb = averageColour(this.colours);
    }
    get weight() {
        return this.colours.length; 
    }
    split() {
        var centroids = [];
        centroids.push(new Centroid(colours.split(0,this.colours.length/2)));
        centroids.push(new Centroid(colours.split(this.colours.length/2, this.colours.length)));
        return centroids;
    }
}

function findDoms(img) {
    
    // var c = document.getElementById("dom_colours")
    console.log("cheeekky");
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    var width = canvas.width = img.naturalWidth;
    var height = canvas.height = img.naturalHeight;
    
    console.log("dddd" + img.naturalHeight);
    console.log(img.naturalWidth);
    
    ctx.drawImage(img,0,0);

    var imageData = ctx.getImageData(0, 0, width, height);
    var data = imageData.data;
    console.log("cccccc");
    const colors = [];
    const centroid1 = [];
    const centroid2 = [];

    var p = 0;
    for (var i = 0, l = data.length; i < l; i+=4) {
        
        colors.push({r: data[i], g: data[i+1], b: data[i+2]});
        if (i < data.length/2) {
            centroid1.push({r: data[i], g: data[i+1], b: data[i+2]});
        }
        else {
            centroid2[p].push({r: data[i], g: data[i+1], b: data[i+2]});
        }
    }

    const ret = [];
    var rgb = averageColour(centroid1);
    ret[0] = {r:rgb.r,g:rgb.g,b:rgb.b,weight:rgb.weight};
    var rgb = averageColour(centroid1);
    ret[1] = {r:rgb.r,g:rgb.g,b:rgb.b,weight:rgb.weight};
    return ret;

    // var rgbStr = 'rgb(' + rgb.r +',' +rgb.g + ',' + rgb.b + ')';
    // ctx.fillStyle = rgbStr;
    // ctx.fillRect(10, 10, 100, 100);
    // var rgb = averageColour(centroid2);
    // var rgbStr = 'rgb(' + rgb.r +',' +rgb.g + ',' + rgb.b + ')';
    // ctx.fillStyle = rgbStr;
    // ctx.fillRect(120, 20, 100, 100);
}

function initialiseClusters(colors) {
    const centroid1 = [];
    const centroid2 = [];
    for (var i = 0, l = colors.length; i < l; i++) {
        if (i < colors.length/2) {
            centroid1.push(colors.push(i));
        }
        else {
            centroid2[p].push(colors[i]);
        }
    }

    const ret = [];
    ret.push(new Centroid(centroid1))
    ret.push(new Centroid(centroid2));
    return ret;

}


function findDomsFromColor(clusters) {
    
    // var c = document.getElementById("dom_colours")
    console.log("cheeekky");
    var clusters = [];
    for (var i = 0; i <= colours.length; i++) {
        clusters[i] = {}
    }
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    var width = canvas.width = img.naturalWidth;
    var height = canvas.height = img.naturalHeight;
    
    console.log("dddd" + img.naturalHeight);
    console.log(img.naturalWidth);
    
    ctx.drawImage(img,0,0);

    var imageData = ctx.getImageData(0, 0, width, height);
    var data = imageData.data;
    console.log("cccccc");
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

    // var rgbStr = 'rgb(' + rgb.r +',' +rgb.g + ',' + rgb.b + ')';
    // ctx.fillStyle = rgbStr;
    // ctx.fillRect(10, 10, 100, 100);
    // var rgb = averageColour(centroid2);
    // var rgbStr = 'rgb(' + rgb.r +',' +rgb.g + ',' + rgb.b + ')';
    // ctx.fillStyle = rgbStr;
    // ctx.fillRect(120, 20, 100, 100);
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
// console.log("jhhasjasdaks");

// const image_input = document.querySelector("#image_input");
// image_input.addEventListener("change", function() {
//     console.log("SSSSSSSS");
//    const reader = new FileReader();
//    reader.addEventListener("load", () => {
//    const uploaded_image = reader.result;
//    document.querySelector("#display_image").style.backgroundImage = `url(${uploaded_image})`;
//    findDoms(`url(${uploaded_image})`); 

// });
//    reader.readAsDataURL(this.files[0]);
// //    drawColours.call();
// });
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