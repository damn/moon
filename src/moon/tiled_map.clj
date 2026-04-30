(ns moon.tiled-map
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn width [^TiledMap tiled-map]
  (.get (.getProperties tiled-map) "width"))

(defn height [^TiledMap tiled-map]
  (.get (.getProperties tiled-map) "height"))

(defn- create-layer
  [{:keys [width
           height
           tilewidth
           tileheight
           name
           visible?
           map-properties
           tiles]}]
  {:pre [(string? name)
         (boolean? visible?)]}
  (let [layer (doto (TiledMapTileLayer. width height tilewidth tileheight)
                (.setName name)
                (.setVisible visible?))]
    (.putAll (.getProperties layer) map-properties)
    (doseq [[xy tiled-map-tile] tiles
            :let [[x y] xy]
            :when tiled-map-tile]
      (.setCell layer x y (doto (TiledMapTileLayer$Cell.)
                            (.setTile tiled-map-tile))))
    layer))

(defn- props-add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (.put props k v)))

(defn- add-layer!
  "`properties` is optional. Returns nil."
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (TiledMap/.getProperties tiled-map) ; shadowing 'properties' otherwise
        layer (create-layer {:width      (.get props "width")
                             :height     (.get props "height")
                             :tilewidth  (.get props "tilewidth")
                             :tileheight (.get props "tileheight")
                             :name name
                             :visible? visible?
                             :map-properties (doto (MapProperties.)
                                               (props-add! properties))
                             :tiles tiles})]
    (.add (.getLayers tiled-map) layer))
  nil)

(defn create-tiled-map [{:keys [properties
                                layers]}]
  (let [tiled-map (TiledMap.)]
    (props-add! (.getProperties tiled-map) properties)
    (doseq [layer layers]
      (add-layer! tiled-map layer))
    tiled-map))

(def copy-tile
  (memoize
   (fn copy [tile]
     (assert tile)
     (StaticTiledMapTile. tile))))

(defn static-tiled-map-tile
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
    (.put (.getProperties tile) property-name property-value)
    tile))

(defn- tile-movement-property
  [tiled-map ^TiledMapTileLayer layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell layer x y)]
      (let [value (-> cell
                      .getTile
                      .getProperties
                      (.get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (.get (.getProperties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (.getName layer) " is undefined."))
        value))))

(defn- movement-property-layers
  [^TiledMap tiled-map]
  (->> tiled-map
       .getLayers
       reverse
       (filter #(.get (TiledMapTileLayer/.getProperties %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(TiledMapTileLayer/.getName layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

(defn spawn-positions
  [^TiledMap tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (.get (.getLayers tiled-map) ^String layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer x y)]
          :when cell
          :let [value (-> cell
                          .getTile
                          .getProperties
                          (.get property-key))]
          :when value]
      [position value])))

; out of memory error -> each texture region is a new object
; so either memoize on id or property/image already calculated !? idk
(def ^:private creature-tile
  (memoize
   (fn [{:keys [tile/id
                tile/texture-region]}]
     (assert (and id
                  texture-region))
     (static-tiled-map-tile texture-region "id" id))))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (add-layer! tiled-map
              {:name "creatures"
               :visible? false
               :tiles (for [[position creature-property] spawn-positions]
                        [position (creature-tile creature-property)])}))
