(ns moon.tiled-map
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapLayer
                                  MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTile
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn spawn-positions [^TiledMap tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        ^TiledMapTileLayer layer (.get (.getLayers tiled-map) layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer (int x) (int y))]
          :when cell
          :let [value (.get (.getProperties (.getTile cell)) property-key)]
          :when value]
      [position value])))

(defn tile-movement-property
  [^TiledMap tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell ^TiledMapTileLayer layer (int x) (int y))]
      (let [value (.get ^MapProperties (.getProperties ^TiledMapTile (.getTile ^TiledMapTileLayer$Cell cell))
                        "movement")]
        (assert value
                (str "Value for :movement at position "
                     position " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (.get (.getProperties tiled-map) "height"))
                                                                    (position 1))]
                     " and layer " (.getName ^TiledMapTileLayer layer) " is undefined."))
        value))))

(defn movement-property-layers [^TiledMap tiled-map]
  (->> (.getLayers tiled-map)
       reverse
       (filter #(.get (.getProperties ^TiledMapTileLayer %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(.getName ^TiledMapTileLayer layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

(defn prepare-creature-tiles [creature-properties image->texture-region]
  (for [{:keys [entity/animation
                creature/level
                property/id]} creature-properties
        :let [image (first (:animation/frames animation))
              texture-region (image->texture-region image)]]
    {:creature/level level
     :tile/id id
     :tile/texture-region texture-region}))

(defn add-creatures-layer! [^TiledMap tiled-map spawn-positions]
  (let [creature-tile (memoize
                       (fn [{:keys [tile/id
                                    tile/texture-region]}]
                         (assert (and id
                                      texture-region))
                         (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
                           (.put ^MapProperties (.getProperties ^StaticTiledMapTile tile) "id" id)
                           tile)))
        props (.getProperties tiled-map)
        ^TiledMapTileLayer layer (doto (TiledMapTileLayer. (int (.get props "width"))
                                                           (int (.get props "height"))
                                                           (int (.get props "tilewidth"))
                                                           (int (.get props "tileheight")))
                                   (.setName "creatures")
                                   (.setVisible false))]
    (doseq [[[x y] tile] (for [[position creature-property] spawn-positions]
                           [position (creature-tile creature-property)])
            :when tile]
      (.setCell layer (int x) (int y)
                (doto (TiledMapTileLayer$Cell.)
                  (.setTile ^TiledMapTile tile))))
    (.add (.getLayers tiled-map) ^MapLayer layer)))
