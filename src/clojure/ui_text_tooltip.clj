(ns clojure.ui-text-tooltip
  (:require [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]))

(defn create [tooltip skin]
  (text-tooltip/new tooltip skin))
