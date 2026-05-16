(ns clojure.gdx.maps.tiled.tiled-map
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.map-layers :as layers]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(def layers tiled-map/layers)

(def properties tiled-map/properties)

(defn add-layer!
  "`properties` is optional. Returns nil."
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (tiled-map/properties tiled-map) ; shadowing 'properties' otherwise
        layer (layer/create {:width      (props/get props "width")
                             :height     (props/get props "height")
                             :tilewidth  (props/get props "tilewidth")
                             :tileheight (props/get props "tileheight")
                             :name name
                             :visible? visible?
                             :map-properties (props/create properties)
                             :tiles tiles})]
    (layers/add! (tiled-map/layers tiled-map) layer))
  nil)

(defn create
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (add-layer! tiled-map layer))
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
                                                                   (- (dec (props/get (properties tiled-map) "height"))
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
  (add-layer! tiled-map
              {:name "creatures"
               :visible? false
               :tiles (for [[position creature-property] spawn-positions]
                        [position (creature-tile creature-property)])}))
