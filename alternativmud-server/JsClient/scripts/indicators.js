function createIndicator(globalCenterX, globalCenterY, radius, strokeColor, strokeWidth) {
    var indicator = new Object();
    indicator.centerX = globalCenterX;
    indicator.centerY = globalCenterY;
    indicator.width = 2*radius+strokeWidth;
    indicator.height = 2*radius+strokeWidth;
    indicator.localCenterX = radius+strokeWidth/2;
    indicator.localCenterY = radius+strokeWidth/2;
    indicator.globalStartX = globalCenterX-radius-strokeWidth;
    indicator.globalStartY = globalCenterY-radius-strokeWidth;
    indicator.radius = radius;
    indicator.raphael = Raphael(indicator.globalStartX, indicator.globalStartY, indicator.width, indicator.height);
    //indicator.raphael.rect(0, 0, indicator.width, indicator.height);
    //indicator.raphael.image(indicator.imgSrc, indicator.localCenterX-indicator.imgWidth/2,
    //    indicator.localCenterY-indicator.imgHeight/2, indicator.imgWidth, indicator.imgHeight);
    indicator.param = {
        stroke: strokeColor, 
        "stroke-width": strokeWidth
    },
    indicator.raphael.customAttributes.arc = function (radius, percent) {
        //alert(centerX+" "+centerY+" "+radius+" "+percent);
        var alpha = 360 / 100 * (percent-0.001),
        a = (90 - alpha) * Math.PI / 180,
        x = indicator.localCenterX + radius * Math.cos(a),
        y = indicator.localCenterY - radius * Math.sin(a),
        path = [["M", indicator.localCenterX, indicator.localCenterX-radius], ["A", radius, radius, 0, +(alpha > 180), 1, x, y]];
        return {
            path: path
        };
    };
    indicator.arc = indicator.raphael.path().attr(indicator.param).attr({
        arc: [indicator.radius, 0]
        });
    
    indicator.updateVal = function(percent) {
        this.arc.animate({
            arc: [this.radius, percent]
        }, 1500, "easeOut");
    }
    
    return indicator;
}

$(document).ready(function () {
    
});