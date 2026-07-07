(ns gdx.scene2d.ui.text-tooltip
  (:require [clojure.text-tooltip :as text-tooltip]))

(defn create [tooltip skin]
  (text-tooltip/new tooltip skin))
