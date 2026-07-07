(ns clojure.ctx-tiled-map
  (:require [clojure.creature-tiles]
            [clojure.all-raw :refer [all-raw]]
            [clojure.moon-textures :as textures]))

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}
   world-fn]
  (let [{:keys [tiled-map
                start-position]} (world-fn
                                  {:level/creature-properties (clojure.creature-tiles/prepare
                                                               (all-raw db :properties/creatures)
                                                               #(textures/texture-region textures %))
                                   :textures textures})]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
