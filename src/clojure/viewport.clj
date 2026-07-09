(ns clojure.viewport
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn update! [& args]
  (apply viewport/update! args))

(defn unproject [& args]
  (apply viewport/unproject args))
