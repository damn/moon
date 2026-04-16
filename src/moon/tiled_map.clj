(ns moon.tiled-map
  (:require [clojure.gdx.tiled-map.props :as props]
            [clj.api.com.badlogic.gdx.maps.map-layers :as layers]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile :as tile]
            [clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [clojure.gdx.tiled-map.layer.cell :as cell]
            [clj.api.com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn props [tiled-map]
  (tiled-map/properties tiled-map))

(defn layers [tiled-map]
  (tiled-map/layers tiled-map))

(defn width [tiled-map]
  (props/get (tiled-map/properties tiled-map) "width"))

(defn height [tiled-map]
  (props/get (tiled-map/properties tiled-map) "height"))

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
    (doseq [[xy tiled-map-tile] tiles
            :when tiled-map-tile]
      (layer/set-cell! layer
                       xy
                       (doto (cell/create)
                         (cell/set-tile! tiled-map-tile))))
    layer))

(defn- add-layer!
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
                             :map-properties (doto (props/create)
                                               (props/add! properties))
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

(def copy-tile
  (memoize
   (fn copy [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn static-tiled-map-tile
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (props/put! (tile/properties tile) property-name property-value)
    tile))

(defn- tile-movement-property
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (layer/cell layer position)]
      (let [value (-> cell
                      cell/tile
                      tile/properties
                      (props/get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props/get (tiled-map/properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (layer/name layer) " is undefined."))
        value))))

(defn- movement-property-layers
  [tiled-map]
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

(defn spawn-positions
  [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers/get (tiled-map/layers tiled-map) layer-name)]
    (for [x (range (layer/width layer))
          y (range (layer/height layer))
          :let [position [x y]
                cell (layer/cell layer position)]
          :when cell
          :let [value (-> cell
                          cell/tile
                          tile/properties
                          (props/get property-key))]
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
  (add-layer! tiled-map {:name "creatures"
                         :visible? false
                         :tiles (for [[position creature-property] spawn-positions]
                                  [position (creature-tile creature-property)])}))
