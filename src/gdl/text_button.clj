(ns gdl.text-button
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-button :as text-button]))

(defn new [& args]
  (apply text-button/new args))
