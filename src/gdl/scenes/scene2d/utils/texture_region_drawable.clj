(ns gdl.scenes.scene2d.utils.texture-region-drawable
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn new [& args]
  (apply texture-region-drawable/new args))

(defn set-min-size! [& args]
  (apply texture-region-drawable/setMinSize args))

(defn tint! [& args]
  (apply texture-region-drawable/tint args))
