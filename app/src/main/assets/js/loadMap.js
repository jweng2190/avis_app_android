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
        var longi = urlParams.get('longitude');
        var lati = urlParams.get('latitude');

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
                  center: [longi, lati], // Longitude, latitude
                  zoom: 15, // Zoom level
                  container: "mapViewDiv" // Div element
                });

        var point = {
                type: "point", // autocasts as new Point()
                longitude: longi,
                latitude: lati
              };

        var markerSymbol = {
                type: "picture-marker",  // autocasts as new PictureMarkerSymbol()
                url: "https://daraobeirne.github.io/kisspng-drawing-pin-world-map-logo-push-vector-5ae029f6ddeaf4.198342921524640246909.png",
                width: "30px",
                height: "30px"
              };

        var pointGraphic = new Graphic({
                geometry: webMercatorUtils.geographicToWebMercator(point),
                symbol: markerSymbol
              });

        view.when(function () {

                graphicsLayer.graphics.addMany([pointGraphic]);

                // create a new sketch view model
                const sketchViewModel = new SketchViewModel({
                  view,
                  layer: graphicsLayer,
                  updateOnGraphicClick: false
                });

              });

      });