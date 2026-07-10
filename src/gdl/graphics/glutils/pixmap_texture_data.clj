(ns gdl.graphics.glutils.pixmap-texture-data
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]))

(defn new [& args]
  (apply pixmap-texture-data/new args))
