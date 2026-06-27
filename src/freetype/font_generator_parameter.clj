(ns freetype.font-generator-parameter
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator$free-type-font-parameter :as params]))

(defn create
  [{:keys [size
           min-filter
           mag-filter]}]
  (doto (params/create)
    (params/set-size! size)
    (params/set-min-filter! min-filter)
    (params/set-mag-filter! mag-filter)))
