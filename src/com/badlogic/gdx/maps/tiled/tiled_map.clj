(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile])
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)))

(defn properties [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn add-layer!
  "`properties` is optional. Returns nil."
  [^TiledMap tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (.getProperties tiled-map) ; shadowing 'properties' otherwise
        layer (layer/create {:width      (.get props "width")
                             :height     (.get props "height")
                             :tilewidth  (.get props "tilewidth")
                             :tileheight (.get props "tileheight")
                             :name name
                             :visible? visible?
                             :map-properties (props/create properties)
                             :tiles tiles})]
    (.add (.getLayers tiled-map) layer))
  nil)

(defn create
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (props/add! (.getProperties tiled-map) properties)
    (doseq [layer layers]
      (add-layer! tiled-map layer))
    tiled-map))

(defn tile-movement-property
  [tiled-map ^TiledMapTileLayer layer [x y]]
  (let [position [x y]]
    (when-let [cell (layer/cell layer position)]
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

(defn movement-property-layers
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
     (static-tiled-map-tile/create texture-region "id" id))))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (add-layer! tiled-map
              {:name "creatures"
               :visible? false
               :tiles (for [[position creature-property] spawn-positions]
                        [position (creature-tile creature-property)])}))
