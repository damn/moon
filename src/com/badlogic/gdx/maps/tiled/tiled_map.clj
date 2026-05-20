(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as tiled-map-tile-layer.cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [gdl.tiled-map :as tiled-map]
            [gdl.tiled-map.layer :as layer]
            [gdl.tiled-map.layers :as layers]
            [gdl.tiled-map.props :as props]
            [gdl.tiled-map.tile :as tile])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

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
  (let [layer (doto (tiled-map-tile-layer/create width height tilewidth tileheight)
                (.setName name)
                (layer/set-visible! visible?))]
    (props/add! (layer/properties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (tiled-map-tile-layer.cell/create tile)))
    layer))

(defn create []
  (TiledMap.))

(extend-type TiledMap
  tiled-map/TiledMap
  (dispose! [this]
    (.dispose this))

  (properties [this]
    (.getProperties this))

  (layers [this]
    (.getLayers this))

  (add-layer! [tiled-map {:keys [name
                                 visible?
                                 properties
                                 tiles]}]
    (let [props (tiled-map/properties tiled-map) ; shadowing 'properties' otherwise
          layer (create-layer {:width      (props/get props "width")
                               :height     (props/get props "height")
                               :tilewidth  (props/get props "tilewidth")
                               :tileheight (props/get props "tileheight")
                               :name name
                               :visible? visible?
                               :map-properties properties
                               :tiles tiles})]
      (layers/add! (tiled-map/layers tiled-map) layer))
    nil)

  (add-creatures-layer! [tiled-map spawn-positions]
    (tiled-map/add-layer! tiled-map
                          {:name "creatures"
                           :visible? false
                           :tiles (for [[position creature-property] spawn-positions]
                                    [position (creature-tile creature-property)])})))
