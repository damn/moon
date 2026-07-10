(ns gdl.text-tooltip
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]))

(defn new [& args]
  (apply text-tooltip/new args))
