(ns game.create.tiled-map
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [moon.creature-tiles]
            [moon.db :as db]
            [moon.textures :as textures]))

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (->> ;"world_fns/modules.edn"
                        ; "world_fns/vampire.edn"
                        "world_fns/uf_caves.edn"
                        io/resource
                        slurp
                        edn/read-string)
        {:keys [tiled-map
                start-position]} ((requiring-resolve f)
                                  (assoc params
                                         :level/creature-properties (moon.creature-tiles/prepare
                                                                     (db/all-raw db :properties/creatures)
                                                                     #(textures/texture-region textures %))
                                         :textures textures))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
