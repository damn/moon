(ns clojure.gdx.maps.tiled.tiled-map
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.tiled-map.props :as props]
            [clojure.tiled-map.layer :as layer]
            [clojure.tiled-map.layer.cell :as cell]
            [clojure.tiled-map.layers :as layers]
            [clojure.tiled-map.tile :as tile]
            [clojure.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn create
  [{:keys [properties
           layers]}]
  (let [tiled-map (com.badlogic.gdx.maps.tiled.TiledMap.)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (tiled-map/add-layer! tiled-map layer))
    tiled-map))

(defn tile-movement-property
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

(defn movement-property-layers
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
     (static-tiled-map-tile/create texture-region "id" id))))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (tiled-map/add-layer! tiled-map
                        {:name "creatures"
                         :visible? false
                         :tiles (for [[position creature-property] spawn-positions]
                                  [position (creature-tile creature-property)])}))
