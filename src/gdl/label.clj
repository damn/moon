(ns gdl.label
  (:refer-clojure :exclude [class new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.label :as label]))

(def class
  label/class)

(defn new [& args]
  (apply label/new args))

(defn set-text! [& args]
  (apply label/setText args))
