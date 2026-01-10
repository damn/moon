(ns moon.world.tiled
  (:require [gdl.maps.map-layers :as layers]
            [gdl.maps.map-properties :as props]
            [gdl.maps.tiled :as tiled-map]
            [gdl.maps.tiled.layer :as layer]
            [gdl.maps.tiled.tiled-map-tile :as tile]
            [gdl.maps.tiled.tiles :as tiles])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

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
                (layer/set-name! name)
                (layer/set-visible! visible?))]
    (props/put-all! (layer/properties layer) map-properties)
    (doseq [[position tiled-map-tile] tiles
            :when tiled-map-tile]
      (layer/set-cell! layer
                       position
                       (doto (TiledMapTileLayer$Cell.)
                         (.setTile tiled-map-tile))))
    layer))

(defn add-layer!
  "`properties` is optional. Returns nil."
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (tiled-map/properties tiled-map)
        layer (create-layer {:width      (props/get props "width")
                             :height     (props/get props "height")
                             :tilewidth  (props/get props "tilewidth")
                             :tileheight (props/get props "tileheight")
                             :name name
                             :visible? visible?
                             :map-properties (props/create properties)
                             :tiles tiles})]
    (layers/add! (tiled-map/layers tiled-map) layer))
  nil)

(defn create-tiled-map [{:keys [properties
                                layers]}]
  (let [tiled-map (tiled-map/create)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (add-layer! tiled-map layer))
    tiled-map))

(def copy-tile (memoize tiles/copy))
(def static-tiled-map-tile tiles/static-tiled-map-tile)

(defn- tile-movement-property [tiled-map layer position]
  (when-let [cell (layer/cell layer position)]
    (let [value (-> cell
                    .getTile
                    tile/properties
                    (props/get "movement"))]
      (assert value
              (str "Value for :movement at position "
                   position  " / mapeditor inverted position: " [(position 0)
                                                                 (- (dec (props/get (tiled-map/properties tiled-map) "height"))
                                                                    (position 1))]
                   " and layer " (layer/name layer) " is undefined."))
      value)))

(defn- movement-property-layers [tiled-map]
  (->> tiled-map
       tiled-map/layers
       reverse
       (filter #(props/get (layer/properties %) "movement-properties"))))

(defn movement-properties [tiled-map position]
  (for [layer (movement-property-layers tiled-map)]
    [(layer/name layer)
     (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))
