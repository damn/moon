(ns moon.tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn- props-add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (.put props k v)))

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
  (let [layer (doto (layer/create width height tilewidth tileheight)
                (.setName name)
                (.setVisible visible?))]
    (props-add! (.getProperties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (.setCell layer (pos 0) (pos 1) (cell/create tile)))
    layer))

(defn- create-layer*
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (.getProperties tiled-map)]
    (create-layer {:width      (.get props "width")
                   :height     (.get props "height")
                   :tilewidth  (.get props "tilewidth")
                   :tileheight (.get props "tileheight")
                   :name name
                   :visible? visible?
                   :map-properties properties
                   :tiles tiles})))

(defn- tile-movement-property
  [tiled-map layer [x y]]
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
  [tiled-map]
  (->> tiled-map
       .getLayers
       reverse
       (filter #(.get (.getProperties %) "movement-properties"))))

#_(defn- movement-properties [tiled-map position]
    (for [layer (movement-property-layers tiled-map)]
      [(.getName layer)
       (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (.get (.getLayers tiled-map) layer-name)]
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

(defn create-tile
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (props-add! (static-tiled-map-tile/properties tile) {property-name property-value})
    tile))

; out of memory error -> each texture region is a new object
; so either memoize on id or property/image already calculated !? idk
(def ^:private creature-tile
  (memoize
   (fn [{:keys [tile/id
                tile/texture-region]}]
     (assert (and id
                  texture-region))
     (create-tile texture-region "id" id))))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (.add (.getLayers tiled-map)
        (create-layer* tiled-map
                       {:name "creatures"
                        :visible? false
                        :tiles (for [[position creature-property] spawn-positions]
                                 [position (creature-tile creature-property)])})))

(defn create-map
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (props-add! (.getProperties tiled-map) properties)
    (doseq [layer layers]
      (.add (.getLayers tiled-map) (create-layer* tiled-map layer)))
    tiled-map))
