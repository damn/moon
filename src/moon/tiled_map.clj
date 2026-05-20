(ns moon.tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [gdl.tiled-map :as tiled-map]
            [gdl.tiled-map.props :as props]
            [gdl.tiled-map.layer :as layer]
            [gdl.tiled-map.layer.cell :as cell]
            [gdl.tiled-map.layers :as layers]
            [gdl.tiled-map.tile :as tile]))

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

#_(defn- movement-properties [tiled-map position]
    (for [layer (movement-property-layers tiled-map)]
      [(layer/name layer)
       (tile-movement-property tiled-map layer position)]))

(defn movement-property [tiled-map position]
  (or (->> tiled-map
           movement-property-layers
           (some #(tile-movement-property tiled-map % position)))
      "none"))

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
                          cell/tile
                          tile/properties
                          (props/get property-key))]
          :when value]
      [position value])))

(defn create-tile
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (props/add! (tile/properties tile) {property-name property-value})
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
  (tiled-map/add-layer! tiled-map
                        {:name "creatures"
                         :visible? false
                         :tiles (for [[position creature-property] spawn-positions]
                                  [position (creature-tile creature-property)])}))
