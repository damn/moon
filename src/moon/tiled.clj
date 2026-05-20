(ns moon.tiled
  (:require moon.tiled-map
            com.badlogic.gdx.maps.map-layers
            com.badlogic.gdx.maps.map-properties
            com.badlogic.gdx.maps.tiled.tiled-map
            [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]
            [gdl.tiled-map :as tiled-map]
            [gdl.tiled-map.layer :as layer]
            [gdl.tiled-map.layer.cell :as cell]
            [gdl.tiled-map.layers :as layers]
            [gdl.tiled-map.props :as props]
            [gdl.tiled-map.tile :as tile]))

(def create-tile moon.tiled-map/create-tile)

(def load! tmx-map-loader/load!)

(defn create-map
  [{:keys [properties
           layers]}]
  (let [tiled-map (com.badlogic.gdx.maps.tiled.tiled-map/create)]
    (props/add! (tiled-map/properties tiled-map) properties)
    (doseq [layer layers]
      (moon.tiled-map/add-layer! tiled-map layer))
    tiled-map))
