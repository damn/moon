(ns moon.tiled-map
  (:require [clojure.maps.map-layers :as layers]
            [clojure.maps.map-properties :as props]
            [clojure.maps.tiled.tiled-map :as tiled-map]
            [clojure.maps.tiled.tiled-map-tile-layer :as layer])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

; Depends on:
; * layer
; * layer.cell
; * props
; => so should go to 'layer' ....
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
    (props/add! (layer/properties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (doto (TiledMapTileLayer$Cell.)
                                   (.setTile tile))))
    layer))

; used with add-layer!
; depends on:
; * tiled-map
; * layer
; so should go to tiled-map
(defn- create-layer*
  [tiled-map {:keys [name
                     visible?
                     properties
                     tiles]}]
  (let [props (tiled-map/properties tiled-map)]
    (create-layer {:width      (props/get props "width")
                   :height     (props/get props "height")
                   :tilewidth  (props/get props "tilewidth")
                   :tileheight (props/get props "tileheight")
                   :name name
                   :visible? visible?
                   :map-properties properties
                   :tiles tiles})))

; pass 'movement' string?
(defn- tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (layer/cell layer position)]
      (let [value (-> cell
                      .getTile
                      .getProperties
                      (props/get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props/get (tiled-map/properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (layer/name layer) " is undefined."))
        value))))

; ??
(defn- movement-property-layers
  [tiled-map]
  (->> tiled-map
       tiled-map/layers
       reverse
       (filter #(props/get (layer/properties %) "movement-properties"))))

; ??
#_(defn- movement-properties [tiled-map position]
    (for [layer (movement-property-layers tiled-map)]
      [(layer/name layer)
       (tile-movement-property tiled-map layer position)]))

; ??
(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

; positions-with-proeprty?, pass stirngs ?
(defn spawn-positions [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers/get (tiled-map/layers tiled-map) layer-name)]
    (for [x (range (layer/width layer))
          y (range (layer/height layer))
          :let [position [x y]
                cell (layer/cell layer position)]
          :when cell
          :let [value (-> cell
                          .getTile
                          .getProperties
                          (props/get property-key))]
          :when value]
      [position value])))

; do create tile at add layer
(defn create-tile
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (StaticTiledMapTile. ^TextureRegion texture-region)]
    (props/add! (.getProperties tile) {property-name property-value})
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
  (layers/add! (tiled-map/layers tiled-map)
               (create-layer* tiled-map
                              {:name "creatures"
                               :visible? false
                               :tiles (for [[position creature-property] spawn-positions]
                                        [position (creature-tile creature-property)])})))

(defn create-map
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (layers/add! (tiled-map/layers tiled-map) (create-layer* tiled-map layer)))
    tiled-map))
