(ns gdl.graphics.glutils.file-texture-data
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.glutils.file-texture-data :as file-texture-data]))

(defn new [& args]
  (apply file-texture-data/new args))
