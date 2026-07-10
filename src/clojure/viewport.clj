(ns clojure.viewport
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn update! [& args]
  (apply viewport/update args))

(defn unproject [& args]
  (apply viewport/unproject args))

(defn get-camera [& args]
  (apply viewport/getCamera args))

(defn get-world-width [& args]
  (apply viewport/getWorldWidth args))

(defn get-world-height [& args]
  (apply viewport/getWorldHeight args))
