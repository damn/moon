(ns create.tiled-map
  (:require [clojure.core.edn-resource :refer [edn-resource]]
            [game.constants :refer [world-fn-file]]
            [moon.creature-tiles]
            [moon.db.all-raw :refer [all-raw]]
            [moon.textures :as textures]))

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (edn-resource world-fn-file)
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
