(ns moon.tiled-map
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [moon.tiled-map-tile-layer.create :as tiled-map-tile-layer]
            [moon.map-properties :as map-properties]))

(defn get-property [tiled-map k]
  (-> tiled-map
      tiled-map/getProperties
      (map-properties/get k)))

(defn create-layer
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (tiled-map/getProperties tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (map-properties/get props "width")
      :height     (map-properties/get props "height")
      :tilewidth  (map-properties/get props "tilewidth")
      :tileheight (map-properties/get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))

(defn add-layer! [tiled-map layer]
  (map-layers/add (tiled-map/getLayers tiled-map)
                  (create-layer tiled-map layer)))

(defn prepare-creature-tiles [creature-properties image->texture-region]
  (for [{:keys [entity/animation
                creature/level
                property/id]} creature-properties
        :let [image (first (:animation/frames animation))
              texture-region (image->texture-region image)]]
    {:creature/level level
     :tile/id id
     :tile/texture-region texture-region}))

(defn add-creatures-layer! [tiled-map spawn-positions]
  (add-layer! tiled-map
              (let [creature-tile (memoize
                                   (fn [{:keys [tile/id
                                                tile/texture-region]}]
                                     (assert (and id
                                                  texture-region))
                                     (let [tile (static-tiled-map-tile/new texture-region)]
                                       (map-properties/put! (static-tiled-map-tile/getProperties tile) "id" id)
                                       tile)))]
                {:name "creatures"
                 :visible? false
                 :tiles (for [[position creature-property] spawn-positions]
                          [position (creature-tile creature-property)])})))
