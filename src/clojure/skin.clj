(ns clojure.skin
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn new [& args]
  (apply skin/new args))

(defn get-font [& args]
  (apply skin/get-font args))
