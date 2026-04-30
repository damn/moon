(ns moon.tiled-map
  (:require [clojure.gdx.maps.props :as props]
            [clojure.gdx.maps.layers :as layers]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.tiled.tile :as tile])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn props [tiled-map]
  (tiled-map/properties tiled-map))

(defn layers [tiled-map]
  (tiled-map/layers tiled-map))

(defn width [tiled-map]
  (props/get (tiled-map/properties tiled-map) "width"))

(defn height [tiled-map]
  (props/get (tiled-map/properties tiled-map) "height"))

(defn create-tiled-map [{:keys [properties
                                layers]}]
  (let [tiled-map (tiled-map/create)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (tiled-map/add-layer! tiled-map layer))
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
    (props/put! (tile/properties tile) property-name property-value)
    tile))

(defn- tile-movement-property
  [tiled-map ^TiledMapTileLayer layer [x y]]
  (let [position [x y]]
    (when-let [cell (.getCell layer x y)]
      (let [value (-> cell
                      .getTile
                      tile/properties
                      (props/get "movement"))]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (props/get (tiled-map/properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (.getName layer) " is undefined."))
        value))))

(defn- movement-property-layers
  [tiled-map]
  (->> tiled-map
       tiled-map/layers
       reverse
       (filter #(props/get (TiledMapTileLayer/.getProperties %) "movement-properties"))))

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
  [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers/get (tiled-map/layers tiled-map) layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer x y)]
          :when cell
          :let [value (-> cell
                          .getTile
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
  (tiled-map/add-layer! tiled-map
                        {:name "creatures"
                         :visible? false
                         :tiles (for [[position creature-property] spawn-positions]
                                  [position (creature-tile creature-property)])}))
