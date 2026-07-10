(ns gdl.texture
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.texture :as texture]))

(defn new [source]
  (texture/new source))
