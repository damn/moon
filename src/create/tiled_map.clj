(ns create.tiled-map
  (:require [clojure.core-ext :refer [edn-resource]]
            [moon.creature-tiles]
            [moon.db :as db]
            [moon.textures :as textures]))

(def world-fn-file
   "config/world_fns/modules.edn"
  ; "config/world_fns/vampire.edn"
  ; "config/world_fns/uf_caves.edn"
  )

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}]
  (let [[f params] (edn-resource world-fn-file)
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
