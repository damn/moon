(ns clojure.texture
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.texture :as texture]))

(defn new [& args]
  (apply texture/new args))

(defn new-from-pixmap [& args]
  (apply texture/new-from-pixmap args))
