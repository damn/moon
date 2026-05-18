(ns game.create.tiled-map
  (:require [clojure.config :as config]
            [moon.creature-tiles]
            [moon.db :as db]
            [moon.textures :as textures]))

(def world-fn-file
  ; "world_fns/modules.edn"
  ; "world_fns/vampire.edn"
   "world_fns/uf_caves.edn"
  )

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (config/edn-resource world-fn-file)
        {:keys [tiled-map
                start-position]} (f
                                  (assoc params
                                         :level/creature-properties (moon.creature-tiles/prepare
                                                                     (db/all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
