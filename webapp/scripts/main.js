
function drawColours() {
    // var c = document.getElementById("dom_colours");
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    ctx.fillStyle = "#FF0000";
    ctx.fillRect(20, 20, 150, 100);
}

function findDoms(imgUrl) {
    
    var img = new Image;
    img.src = imgUrl;
    // var c = document.getElementById("dom_colours")
    console.log("cheeekky");
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext("2d");
    ctx.drawImage(img,0,0);
    console.log("dddd" + img.naturalHeight);
    console.log(img.naturalWidth);
    img.onload = function() {
        var width = canvas.width = img.naturalWidth;
        var height = canvas.height = img.naturalHeight;

        console.log("pppp" + img.naturalHeight);
        console.log(img.naturalWidth);
    
        var imageData = ctx.getImageData(0, 0, width, height);
        var data = imageData.data;
    }
    console.log("cccccc");
    // const colors = [];
    // const centroid1 = [];
    // const centroid2 = [];



    // var p = 0;
    // for (var i = 0, l = data.length; i < l; i+=4) {
        
    //     colors[p] = {r: data[i], g: data[i+1], b: data[i+2]};
    //     if (i < data.length/2) {
    //         centroid1[p] = {r: data[i], g: data[i+1], b: data[i+2]};
    //     }
    //     else {
    //         centroid2[p] = {r: data[i], g: data[i+1], b: data[i+2]};
    //     }
    //     p++;
    // }

    // var rgb = averageColour(centroid1);
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
    return {r:r, g:g, b:b, weight:colours.length}
}
console.log("jhhasjasdaks");

const image_input = document.querySelector("#image_input");
image_input.addEventListener("change", function() {
    console.log("SSSSSSSS");
   const reader = new FileReader();
   reader.addEventListener("load", () => {
   const uploaded_image = reader.result;
   document.querySelector("#display_image").style.backgroundImage = `url(${uploaded_image})`;
   findDoms(`url(${uploaded_image})`); 

});
   reader.readAsDataURL(this.files[0]);
//    drawColours.call();
});

// (function() {
//     var upload = document.getElementById('upload');
//     // var target = document.getElementById('target');
  
//     upload.onchange = function() {
//       handleImages(this.files);
//     };
  
//     // target.onclick = function() {
//     //   upload.click();
//     // };
//   })();