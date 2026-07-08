(ns clojure.moon.create-tiled-map
  (:require [clojure.db.all-raw :refer [all-raw]]
            [clojure.creature-tiles]
            [clojure.moon-textures :as textures]
            [clojure.levels.tmx :as tmx]))

(defn f [ctx]
  (let [{:keys [tiled-map
                start-position]} (tmx/vampire
                                   {:level/creature-properties (clojure.creature-tiles/prepare
                                                               (all-raw (:ctx/db ctx) :properties/creatures)
                                                               #(textures/texture-region (:ctx/textures ctx) %))
                                    :textures (:ctx/textures ctx)})]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position)))
