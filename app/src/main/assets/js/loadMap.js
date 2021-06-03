require(["esri/config","esri/Map", "esri/views/MapView",
        "esri/widgets/Sketch/SketchViewModel",
        "esri/Graphic",
        "esri/layers/GraphicsLayer",
        "esri/geometry/support/webMercatorUtils"],
        function (esriConfig, Map, MapView,
        SketchViewModel, Graphic, GraphicsLayer, webMercatorUtils) {

        esriConfig.apiKey = "AAPKb6d54521dc1e49d5a22e52b384b12869Sj1v8uN-ROr_TPOFk0b7t4x2mpPiKZeLE4ezFkRpjf0CLo2QdVkik6jTq-PVWfXz";

        var editGraphic;

        const urlParams = new URLSearchParams(window.location.search);
        var longi = urlParams.get('longitude').split(',');
        var lati = urlParams.get('latitude').split(',');

        // GraphicsLayer to hold graphics created via sketch view model
        const graphicsLayer = new GraphicsLayer({
            id: "tempGraphics"
        });

        const map = new Map({
          basemap: "osm-standard-relief",
          layers: [graphicsLayer]
        });

        const view = new MapView({
                  map: map,
                  center: [longi[0], lati[0]], // Longitude, latitude
                  zoom: 15, // Zoom level
                  container: "mapViewDiv" // Div element
                });

        
        var points = [];
        for(var i=0; i<longi.length; i++) {
          points.push({
            type: "point", // autocasts as new Point()
            longitude: longi[i],
            latitude: lati[i]
          })
        }

        var markerSymbol = {
                type: "picture-marker",  // autocasts as new PictureMarkerSymbol()
                url: "https://daraobeirne.github.io/kisspng-drawing-pin-world-map-logo-push-vector-5ae029f6ddeaf4.198342921524640246909.png",
                width: "30px",
                height: "30px"
              };

        
        var pointGraphics = [];
        for(var i=0; i<points.length; i++) {
          pointGraphics.push({
            geometry: webMercatorUtils.geographicToWebMercator(points[i]),
            symbol: markerSymbol
          })
        }

        view.when(function () {

                graphicsLayer.graphics.addMany(pointGraphics);

                // create a new sketch view model
                const sketchViewModel = new SketchViewModel({
                  view,
                  layer: graphicsLayer,
                  updateOnGraphicClick: false
                });

              });

      });