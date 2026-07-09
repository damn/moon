(ns clojure.moon.create-tiled-map
  (:require [clojure.db.all-raw :refer [all-raw]]
            [clojure.levels.tmx :as tmx]
            [clojure.moon-textures :as textures]
            [clojure.tiled-map.creature-tiles :as creature-tiles]))

(defn f [ctx]
  (let [{:keys [tiled-map
                start-position]} (tmx/vampire
                                 {:level/creature-properties (creature-tiles/prepare
                                                             (all-raw (:ctx/db ctx) :properties/creatures)
                                                             #(textures/texture-region (:ctx/textures ctx) %))
                                  :textures (:ctx/textures ctx)})]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
