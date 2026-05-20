(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as tiled-map-tile-layer.cell]
            [gdl.tiled-map :as tiled-map]
            [gdl.tiled-map.layer :as layer]
            [gdl.tiled-map.layers :as layers]
            [gdl.tiled-map.props :as props])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

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
    nil))
