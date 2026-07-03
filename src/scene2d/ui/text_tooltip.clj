(ns scene2d.ui.text-tooltip
  (:require [clojure.gdx.text-tooltip.new :as new-text-tooltip]))

(defn create [tooltip skin]
  (new-text-tooltip/f tooltip skin))
