(ns clojure.text-field
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]))

(defn new [& args]
  (apply text-field/new args))

(defn get-text [& args]
  (apply text-field/get-text args))
