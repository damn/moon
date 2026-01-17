(ns moon.create.tiled-map-and-start-position
  (:require [moon.db :as db]
            [moon.textures :as textures]
            [moon.world-fns.creature-tiles]))

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}
   world-fn]
  (let [[f params] world-fn
        {:keys [tiled-map
                start-position]} (f
                                  (assoc params
                                         :level/creature-properties (moon.world-fns.creature-tiles/prepare
                                                                     (db/all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
