(ns clojure.image-button
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]))

(defn new [& args]
  (apply image-button/new args))
