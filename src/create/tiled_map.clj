(ns create.tiled-map
  (:require [moon.creature-tiles]
            [moon.db.all-raw :refer [all-raw]]
            [moon.textures :as textures]))

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}
   world-fn-file]
  (let [[f params] world-fn-file
        {:keys [tiled-map
                start-position]} (f
                                  (assoc params
                                         :level/creature-properties (moon.creature-tiles/prepare
                                                                     (all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
